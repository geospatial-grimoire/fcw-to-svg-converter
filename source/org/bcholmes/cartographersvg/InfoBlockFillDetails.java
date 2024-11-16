package org.bcholmes.cartographersvg;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.bcholmes.cartographersvg.util.XmlUtil;
import org.w3c.dom.Element;

public class InfoBlockFillDetails extends InfoBlock {

	public class PatternFillStyle extends FillStyle {
		@Override
		public Paint initialize(Element defs, FilenameResolver resolver, SymbolResolver symbolResolver, String color) {
			if (StringUtils.endsWithIgnoreCase("hollow", getName())) {
				return new Paint("none");
			} else if (StringUtils.endsWithIgnoreCase("solid", getName())) {
				return new Paint(color);
			} else {
				String name = createId(color);
				if (!InfoBlockFillDetails.this.definedIds.contains(name)) {
					createDefinition(defs, color);
					InfoBlockFillDetails.this.definedIds.add(name);
				}
				return new Paint("url(#" + name + ")");
			}
		}
		
		void createDefinition(Element defs, String color) {
			String id = createId(color);
			Element pattern = defs.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "pattern");
			pattern.setAttribute("height", "8");
			pattern.setAttribute("width", "8");
			pattern.setAttribute("id", id);
			pattern.setAttribute("patternUnits", "userSpaceOnUse");

			pattern.appendChild(createRectangle(defs, "#ffffff", "8.2", "8.2", "-0.1", "-0.1"));
			byte[] bitmap = this.details;

			for (int i = 0, length = bitmap == null ? 0 : bitmap.length; i <length; i++) {
				int bitNumber = 0x01;
				for (int j = 0; j < 8; j++) {
					if ((bitmap[i] & bitNumber) != 0) {
						pattern.appendChild(createRectangle(defs, color, "1", "1", "" + i, "" + j));
					}
					bitNumber = bitNumber << 1;
				}
			}
			
			defs.appendChild(pattern);
		}
		
		Element createRectangle(Element defs, String color, String width, String height, String x, String y) {
			Element background = defs.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "rect");
			background.setAttribute("fill", color);
			background.setAttribute("width", width);
			background.setAttribute("height", height);
			background.setAttribute("stroke", "none");
			background.setAttribute("fill-rule", "even");
			background.setAttribute("x", x);
			background.setAttribute("y", y);
			return background;
		}

		String createId(String color) {
			return "BrushPattern-" + StringSanitizer.sanitize(getName()) + "-" + this.id + "-color-" + StringUtils.remove(color, "#");
		}

	}
	
	public class SymbolFillStyle extends FillStyle {
		
		boolean symbolResolved = false;
		SymbolFill symbol;
		
		protected void doInitialization(Element defs, FilenameResolver resolver, SymbolResolver symbolResolver) {
			SymbolDefinition definition = symbolResolver.findSymbol(this.symbol.getName());
			
			if (definition != null) {
			
				Element pattern = defs.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "pattern");
				pattern.setAttribute("height", NumberUtil.formatFloat(definition.getHeight()));
				pattern.setAttribute("width", NumberUtil.formatFloat(definition.getWidth()));
//				pattern.setAttribute("viewBox",  
//						NumberUtil.formatFloat(definition.getLow().getX()) + " " +
//						NumberUtil.formatFloat(-definition.getHigh().getY()) + " " + 
//						NumberUtil.formatFloat(definition.getHigh().getX()) + " " +
//						NumberUtil.formatFloat(-definition.getLow().getY()) + ")");
				
				pattern.setAttribute("x", NumberUtil.formatFloat(definition.getLow().getX()));
				pattern.setAttribute("y", NumberUtil.formatFloat(-definition.getHigh().getY()));
				String id = getSvgId();
				pattern.setAttribute("id", id);
				pattern.setAttribute("patternUnits", "userSpaceOnUse");
	
				Element use = defs.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "use");
				use.setAttributeNS(XmlUtil.XLINK_NAMESPACE, "href", "#" + SymbolDefinition.createSymbolName(definition.getName()));
				use.setAttribute("transform", "translate(" + NumberUtil.formatFloat(-definition.getLow().getX()) 
						+ "," + NumberUtil.formatFloat(definition.getHigh().getY()) + ")");
				pattern.appendChild(use);
				
				defs.appendChild(pattern);
				symbolResolved = true;
			}
		}

		public String getSvgId() {
			return "SymbolPattern-" + this.id + "-" + StringSanitizer.sanitize(getName());
		}	
		
		@Override
		public Paint initialize(Element defs, FilenameResolver resolver,
				SymbolResolver symbolResolver, String color) {
			super.initialize(defs, resolver, symbolResolver, color);
			return symbolResolved ? new Paint("url(#" + getSvgId() + ")") : new Paint("yellow");
		}
	}
	
	
	public class BitmapFillStyle extends FillStyle {
		String bitmapName;
		public float realWidth;
		public float realHeight;
		
		protected void doInitialization(Element defs, FilenameResolver resolver, SymbolResolver symbolResolver) {
			Element pattern = defs.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "pattern");
			pattern.setAttribute("id", getSvgId());
			pattern.setAttribute("patternUnits", "userSpaceOnUse");
			
			Element image = defs.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "image");
			image.setAttribute("x", "0");
			image.setAttribute("y", "0");
			
			File imageFile = resolver.resolve(this.bitmapName);
			if (imageFile != null) {
				Dimension d = getImageDimensions(imageFile);
					
				pattern.setAttribute("height", NumberUtil.formatFloat((float) d.getHeight()));
				pattern.setAttribute("width", NumberUtil.formatFloat((float) d.getWidth()));
				
				image.setAttribute("height", NumberUtil.formatFloat((float) d.getHeight()));
				image.setAttribute("width", NumberUtil.formatFloat((float) d.getWidth()));
				
				image.setAttributeNS(XmlUtil.XLINK_NAMESPACE, "href", imageFile.toURI().toASCIIString());
			}
			
			pattern.appendChild(image);
			
			defs.appendChild(pattern);
		}

		private Dimension getImageDimensions(File imageFile) {
			try {
				return Sanselan.getImageSize(imageFile);
			} catch (ImageReadException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
		}

		public String getSvgId() {
			return "BitmapFillStyle-" + this.id;
		}
	}

	private Set<String> definedIds = new HashSet<String>();

	
	protected InfoBlockFillDetails(int length, int etype, int itype) {
		super(length, etype, itype);
	}

	private Map<Short, FillStyle> styles = new HashMap<Short,FillStyle>();
	
	public static class SymbolFill {

		float xstep;
		float ystep;
		float xscale;
		float yscale;
		String symbolName;
		public float xyrot;
		
		public String getName() {
			return this.symbolName;
		}
		
	}
	
	public class FillStyle {

		boolean initialized;
		
		int structureLength;
		byte type;
		byte dofst;
		short id;
		byte flags;
		String name;
		byte[] details;
		
		public boolean isPattern() {
			return type == 0;
		}
		public boolean isHatch() {
			return type == 1;
		}
		public boolean isBitmap() {
			return type == 2;
		}
		public boolean isSymbol() {
			return type == 3;
		}
		public String getName() {
			return this.name;
		}
		public Paint initialize(Element defs, FilenameResolver resolver, SymbolResolver symbolResolver, String color) {
			if (!initialized) {
				initialized = true;
				doInitialization(defs, resolver, symbolResolver);
			}
			return new Paint("url(#" + getSvgId() + ")");
		}
		protected void doInitialization(Element defs, FilenameResolver resolver, SymbolResolver symbolResolver) {
			
		}
		public String getSvgId() {
			return "FillStyle-" + this.id;
		}
	}

	public static InfoBlockFillDetails from(int length, int etype, int itype, ByteBuffer buffer) {
		
		InfoBlockFillDetails result = new InfoBlockFillDetails(length, etype, itype);

		buffer.getInt();
		buffer.get();
		buffer.get();
		buffer.getShort();
		buffer.getInt();
		short versionNumber = buffer.getShort();
		buffer.getShort();
		
		while (buffer.remaining() > 0) {
			int structureLength = buffer.getInt();
			if (structureLength > 0) {
				FillStyle style = createFillStyle(result, buffer.get());
				style.structureLength = structureLength;
				style.dofst = buffer.get();
				style.id = buffer.getShort();
				style.flags = buffer.get();
				style.name = BaseShape.getString(buffer, -1);
				result.styles.put(style.id, style);
				
				if (style.isSymbol()) {
					int detailsLength = style.structureLength - (style.dofst & 0xff);
					SymbolFill symbolFill = new SymbolFill();
					symbolFill.xstep = buffer.getFloat();
					symbolFill.ystep = buffer.getFloat();
					symbolFill.xscale = buffer.getFloat();
					symbolFill.yscale = buffer.getFloat();
					symbolFill.xyrot = buffer.getFloat();
					symbolFill.symbolName = BaseShape.getString(buffer, detailsLength - 20);
					((SymbolFillStyle) style).symbol = symbolFill;
					
				} else if (style.isBitmap() && versionNumber == 2) {
					int detailsLength = style.structureLength - (style.dofst & 0xff);
					BitmapFillStyle bitmapFillStyle = (BitmapFillStyle) style;

					byte fit = buffer.get();
					byte tcode = buffer.get();
					byte flags = buffer.get();
					byte rsvdf = buffer.get();
					bitmapFillStyle.realWidth = buffer.getFloat();
					bitmapFillStyle.realHeight = buffer.getFloat();
					float realAngle = buffer.getFloat();
					int transparencyColor = buffer.getInt();
					int rsvd = buffer.getInt();
					bitmapFillStyle.bitmapName = BaseShape.getString(buffer, detailsLength - 24);
				} else {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					for (int i = 0; i < style.structureLength - (style.dofst & 0xff); i++ ) {
						output.write(buffer.get());
					}
					style.details = output.toByteArray();
				}
			}
		}	
		
		return result;
	}

	private static FillStyle createFillStyle(InfoBlockFillDetails details, byte b) {
		FillStyle style = details.new FillStyle();
		if (b == 0) {
			style = details.new PatternFillStyle();
		} else if (b == 2) {
			style = details.new BitmapFillStyle();
		} else if (b == 3) {
			style = details.new SymbolFillStyle();
		}
		style.type = b;
		return style;
	}

	public FillStyle getStyle(short styleId) {
		return this.styles.get(styleId);
	}

}
