package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;

import org.apache.commons.lang.StringUtils;
import org.bcholmes.cartographersvg.InfoBlockFonts.Font;
import org.bcholmes.cartographersvg.util.XmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class Label extends BaseShape implements Shape {

	private final String text;
	private final Point location;
	private final short fontId;
	private final short textFlags;
	private final float height;
	private final short justification;
	private final float spacing;

	public Label(String text, Point location, CommonHeader header, short fontId, float height, short textFlags, short justification, float spacing) {
		super(header);
		this.text = text;
		this.location = location;
		this.fontId = fontId;
		this.height = height;
		this.textFlags = textFlags;
		this.justification = justification;
		this.spacing = spacing;
	}

	public static Label from(ByteBuffer buffer, CommonHeader header) {
		
		short fontId = buffer.getShort();
		Point location = Point.from(buffer);
		float height = buffer.getFloat();
		float xScale = buffer.getFloat();
		float angle = buffer.getFloat();
		float spacing = buffer.getFloat();
		short textFlags = buffer.getShort();
		short dFlags = buffer.getShort();
		short justification = buffer.getShort();
		
		String text = getString(buffer, -1);
		return new Label(text, location, header, fontId, height, textFlags, justification, spacing);
	}

	public String getText() {
		return this.text;
	}

	public void appendAsXml(Element parent, Drawing drawing) {
		Element element = parent.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "text");
		Point mapped = drawing.map(this.location);
		float x = mapped.getX();
		float y = mapped.getY();
		element.setAttribute("x", NumberUtil.formatFloat(x));
		element.setAttribute("y", NumberUtil.formatFloat(y));

		Font font = drawing.getFont(this.fontId);
		if (font != null) {
			element.setAttribute("font-family", font.getName());
			element.setAttribute("font-size", NumberUtil.formatFloat(this.height));
		} else {
			element.setAttribute("font-family", "Sans");
			element.setAttribute("font-size", NumberUtil.formatFloat(this.height));
		}
		if (isBold()) {
			element.setAttribute("font-weight", "bold");
		}
		if (isItalic()) {
			element.setAttribute("font-style", "italic");
		}
		if (isCentered()) {
			element.setAttribute("text-anchor", "middle");
			element.setAttribute("text-align", "center");
		} else if (isRightJustified()) {
			element.setAttribute("text-anchor", "end");
			element.setAttribute("text-align", "right");
		}

		element.setAttribute("fill", this.header.getFillColor());
		element.setAttribute("stroke", "none");
		
		int lineNumber = 0;
		String[] lines = getLines();
		
		if (isVerticalAlignmentBottom()) {
			y -= ((lines.length-1) * this.height);			
		} else if (isVerticalAlignmentMiddle()) {
			y -= ((lines.length-1) * this.height / 2.0f);
		}
		
		if (StringUtils.equals(this.text, "150")) {
			System.out.println("height: " + this.height);
//			System.out.println("font height: " + font.);
			System.out.println("spacing: " + this.spacing);
			System.out.println("justification: " + this.justification);
			System.out.println("x: " + x);
			System.out.println("y: " + y);
		}
		
		for (String line : lines) {
			if (StringUtils.isNotBlank(line)) {
				Element tspan = parent.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "tspan");
				tspan.setAttribute("x", NumberUtil.formatFloat(x));
				tspan.setAttribute("y", NumberUtil.formatFloat(y + (lineNumber) * this.height));
			
				tspan.appendChild(parent.getOwnerDocument().createTextNode(line));
				element.appendChild(tspan);
				lineNumber++;
			}
		}
		
		parent.appendChild(element);
	}

	private boolean isVerticalAlignmentBottom() {
		return this.justification == 0 || this.justification == 1 
				|| this.justification == 2 || this.justification == 11 
				|| this.justification == 12 || this.justification == 13 
				|| this.justification == 14 || this.justification == 15;
	}

	private boolean isVerticalAlignmentMiddle() {
		return this.justification == 3 || this.justification == 4 
				|| this.justification == 5 || this.justification == 9 
				|| this.justification == 10;
	}
	
	private boolean isRightJustified() {
		return this.justification == 2 || this.justification == 5 || this.justification == 8 
				|| this.justification == 10 || this.justification == 14 || this.justification == 15
				|| this.justification == 19 || this.justification == 20;
	}

	private String[] getLines() {
		return StringUtils.split(this.text.replaceAll("\r", ""), "\n");
	}

	private boolean isCentered() {
		return this.justification == 1 || this.justification == 4 || this.justification == 7 || this.justification == 13;
	}

	public boolean isBold() {
		return (this.textFlags & 0x10) != 0;
	}
	public boolean isItalic() {
		return (this.textFlags & 0x20) != 0;
	}

	@Override
	protected boolean isStroke() {
		return false;
	}
	protected boolean isFilled() {
		return true;
	}
	public Point getLocation() {
		return this.location;
	}
}
