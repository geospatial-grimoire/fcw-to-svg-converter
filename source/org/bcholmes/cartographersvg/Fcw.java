package org.bcholmes.cartographersvg;

import static org.bcholmes.cartographersvg.util.XmlUtil.SVG_NAMESPACE;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.bcholmes.cartographersvg.InfoBlockFillDetails.FillStyle;
import org.bcholmes.cartographersvg.InfoBlockFonts.Font;
import org.bcholmes.cartographersvg.util.HexDump;
import org.bcholmes.cartographersvg.util.XmlUtil;
import org.bcholmes.packing.Dimension;
import org.bcholmes.packing.PackItemImpl;
import org.bcholmes.packing.Packer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Fcw implements FilenameResolver, SymbolResolver {
	
	class DrawingImpl implements Drawing {

		private final Point3d high;
		private final Point3d low;
		private float width;
		private float height;

		public DrawingImpl(InfoBlockHeader header) {
			this(header.getLow(), header.getHi());
		}

		public DrawingImpl(Point3d low, Point3d high) {
			this.low = low;
			this.high = high;
			this.width = high.getX() - low.getX();
			this.height = high.getY() - low.getY();
		}

		// map the point from the CC coordinate-space to 
		// the SVG coordinate space
		public Point map(Point point) {
			float y = mapY(point.getY());
			float x = mapX(point.getX());
			return new Point(x, y);
		}

		public float mapX(float x) {
			return x - this.low.getX();
		}

		public float mapY(float y) {
			return this.high.getY() - y;
		}

		public float getWidth() {
			return this.width;
		}

		public float getHeight() {
			return this.height;
		}

		public Font getFont(short fontId) {
			InfoBlockFonts fonts = (InfoBlockFonts) Fcw.this.infoblocks.get(InfoBlockType.FONTS);
			return fonts == null ? null : fonts.getFontById(fontId);
		}

		public String getFillStyle(short fillStyle, String color) {
			return Fcw.this.getFillStyle(fillStyle, color);
		}

		public float getBaseStroke() {
			float base = Math.max(this.width, this.height);
			return Math.max(base * 0.4f / 1000.0f, 0.02f);
		}

		public void initializeSvgDimensions(Element element) {
			float hypotenuse = (float) Math.sqrt(this.height * this.height + this.width * this.width);
			
			if (hypotenuse > 1000 && hypotenuse < 1600) {
				element.setAttribute("width", NumberUtil.formatFloat(getWidth())  + "px");
				element.setAttribute("height", NumberUtil.formatFloat(getHeight()) + "px");
			} else {
				float scale = 1200 / hypotenuse;
				
				element.setAttribute("width", NumberUtil.formatFloat(getWidth() * scale)  + "px");
				element.setAttribute("height", NumberUtil.formatFloat(getHeight() * scale) + "px");
				element.setAttribute("viewBox", "0 0 " + NumberUtil.formatFloat(getWidth()) + " " + NumberUtil.formatFloat(getHeight()));
			}
		}
	}

	class SymbolDrawingImpl extends DrawingImpl {

		public SymbolDrawingImpl(InfoBlockHeader header) {
			super(header);
		}

		public Point map(Point point) {
			return new Point(point.getX(), -point.getY());
		}
		
		public float mapX(float x) {
			return x;
		}

		public float mapY(float y) {
			return -y;
		}
		
	}
	
	
	final boolean isCatalog;
	final File original;
	Map<InfoBlockType,InfoBlock> infoblocks = new HashMap<InfoBlockType,InfoBlock>();
	final FileId fileId;
	private List<Shape> entities = new ArrayList<Shape>();
	private Document document;
	private Element defs;
	
	public Fcw(File file, FileId fileId, boolean isCatalog) {
		this.original = file;
		this.fileId = fileId;
		this.isCatalog = isCatalog;
	}

	public String getFillStyle(short fillStyle, String color) {
		InfoBlockFillDetails infoBlock = (InfoBlockFillDetails) this.infoblocks.get(InfoBlockType.FILL_STYLES);
		
		FillStyle style = infoBlock.getStyle(fillStyle);
		if (style == null) {
			return "none";
		} else if (style.isPattern()) {
			return style.initialize(this.defs, this, this, color).toSvgReference();
		} else if (style.isBitmap()) {
			return style.initialize(this.defs, this, this, color).toSvgReference();
		} else if (style.isSymbol()) {
			return style.initialize(this.defs, this, this, color).toSvgReference();
		} else {
			return "yellow";
		}
	}

	public SymbolDefinition findSymbol(String name) {
		SymbolDefinition result = null;
		for (Shape shape : this.entities) {
			if (shape.isSymbolDefinition() && StringUtils.equals(name, ((SymbolDefinition) shape).getName())) {
				result = (SymbolDefinition) shape;
				break;
			}
		}
		return result;
	}

	public static void decodeBuffer(File file, FileId fileId, byte[] b) {
		
		Fcw fcw = new Fcw(file, fileId, isCatalogFileName(file));
		
		
		Stack<List<Shape>> stack = new Stack<List<Shape>>();
		stack.push(fcw.entities);
		
		for (int offset = 0; offset < b.length; ) {
			
			CommonHeader header = CommonHeader.fromBytes(b, offset, fcw.fileId.getVersionNumber());
			List<Shape> e = stack.peek();
			
			if (header.getSize() == 0) {
				break;
			} else if (header.getSize() == 5) {
				if (header.getType() == 0) {
					Group group = (Group) (e.get(e.size()-1));
					stack.push(group.getSublist());
				} else {
					stack.pop();
				}
			} else {
				if (header.getType() == 0) {
					HexDump.write(b, offset, header.getSize());
					InfoBlock infoBlock = InfoBlock.from(header.getSize(), header.getType(), 
							b[offset + 5], ArrayUtils.subarray(b, offset, offset + header.getSize()));
					System.out.println("Info Block type " + infoBlock.getIType() + " (" + header.getSize() + ")");
					fcw.infoblocks.put(infoBlock.getIType(), infoBlock);
				} else {
					e.add(ShapeFactory.createShape(header));
					
				}
			}
			offset += header.getSize();
		}
		
		fcw.fcwToSvg();
	}

	private void fcwToSvg() {
		
		InfoBlockHeader header = getInfoBlockHeader();
		DrawingImpl drawing = new DrawingImpl(header);
		SymbolDrawingImpl symbolDrawing = new SymbolDrawingImpl(header);
		
		this.document = XmlUtil.createEmptyDocument();
		Element element = this.document.createElementNS(SVG_NAMESPACE, "svg");
		element.setAttribute("version", "1.1");
		if (!isCatalog) {
			drawing.initializeSvgDimensions(element);
		}
		
		this.document.appendChild(element);

		this.defs = this.document.createElementNS(SVG_NAMESPACE, "defs");
		element.appendChild(this.defs);

		for (Shape shape : this.entities) {
			if (shape.isSymbolDefinition()) {
				shape.appendAsXml(this.defs, symbolDrawing);
			} else {
				shape.appendAsXml(element, drawing);
			}
		}
		
		if (isCatalog) {
			createUse(element);
		}
		
		XmlUtil.writeXml(this.document, getSvgFile());
	}

	private void createUse(Element element) {
		
		List<SymbolDefinition> symbols = getSymbolDefinitions();
		
		List<PackItemImpl> items = new ArrayList<PackItemImpl>();
		for (SymbolDefinition definition : symbols) {
			items.add(new PackItemImpl((int) Math.ceil(definition.getWidth() * 1.1), (int) Math.ceil(definition.getHeight() * 1.1), definition));
		}

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Dimension dimension = new Packer().pack(items);
		stopWatch.stop();
		System.out.println(" width = " + dimension.getWidth() + ", height = " + dimension.getHeight());
		System.out.println(" time = " + stopWatch.getTime());
		this.document.getDocumentElement().setAttribute("width", NumberUtil.formatFloat(dimension.getWidth()) + "px");
		this.document.getDocumentElement().setAttribute("height", NumberUtil.formatFloat(dimension.getHeight()) + "px");
		
		for (PackItemImpl item : items) {
			SymbolDefinition definition = (SymbolDefinition) item.getItem();
			Element use = this.document.createElementNS(XmlUtil.SVG_NAMESPACE, "use");
			String id = SymbolDefinition.createSymbolName(definition.getName());
			
			use.setAttributeNS(XmlUtil.XLINK_NAMESPACE, "href", "#" + id);
			use.setAttribute("x", NumberUtil.formatFloat(item.getX() + (0.05f * definition.getWidth())));
			use.setAttribute("y", NumberUtil.formatFloat(item.getY() + (0.05f * definition.getHeight())));
			use.setAttribute("color", "red");
			use.setAttribute("transform", "translate(" + 
					NumberUtil.formatFloat(-definition.getLow().getX()) + "," + 
					NumberUtil.formatFloat(definition.getHigh().getY()) + ")");
			element.appendChild(use);
		}
	}

	private List<SymbolDefinition> getSymbolDefinitions() {
		List<SymbolDefinition> symbols = new ArrayList<SymbolDefinition>();
		for (Shape shape : this.entities) {
			if (shape.isSymbolDefinition()) {
				symbols.add((SymbolDefinition) shape);
			}
		}
		return symbols;
	}

	private InfoBlockHeader getInfoBlockHeader() {
		InfoBlock block = this.infoblocks.get(InfoBlockType.HEADER);
		return (InfoBlockHeader) block;
	}

	private Element createLayer(Document document, short layerId) {
		InfoBlockLayers layers = (InfoBlockLayers) this.infoblocks.get(InfoBlockType.LAYERS);
		Element g = document.createElementNS(SVG_NAMESPACE, "g");
		InfoBlockLayers.Layer layer = layers.getLayerById(layerId);
		if (layer != null) {
			System.out.println("Layer " + layer.getName());
			g.setAttributeNS(XmlUtil.INKSCAPE_NAMESPACE, "groupmode", "layer");
			g.setAttributeNS(XmlUtil.INKSCAPE_NAMESPACE, "label", layer.getName());
			g.setAttribute("id", "layer-" + layerId);
			if (layer.isHidden()) {
				g.setAttribute("style", "display:none");
			}
			if (layer.isLocked()) {
				g.setAttributeNS(XmlUtil.SODIPODI_NAMESPACE, "insensitive", "true");
			}
		} else {
			g.setAttributeNS(XmlUtil.INKSCAPE_NAMESPACE, "groupmode", "layer");
			g.setAttributeNS(XmlUtil.INKSCAPE_NAMESPACE, "label", "Layer " + layerId);
			g.setAttribute("id", "layer-" + layerId);
		}
		return g;
	}

	private File getSvgFile() {
		String name = FilenameUtils.getBaseName(this.original.getName());
		return new File(this.original.getParentFile(), name + ".svg");
	}

	private static boolean isCatalogFileName(File file) {
		return "fsc".equalsIgnoreCase(FilenameUtils.getExtension(file.getName()));
	}

	@Override
	public File resolve(String name) {
		if (name.startsWith("$")) {
			return new File(this.original.getParent(), name.substring(1).replace('\\', File.separatorChar));
		} else {
			return null;
		}
	}
}