package org.bcholmes.cartographersvg;

import static org.bcholmes.cartographersvg.NumberUtil.formatFloat;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.commons.lang.ClassUtils;
import org.w3c.dom.Element;

public abstract class BaseShape implements Shape {

	CommonHeader header;

	public int getEtype() {
		return this.header.getType();
	}
	public boolean isSymbolDefinition() {
		return false;
	}
	public CommonHeader getHeader() {
		return this.header;
	}
	
	static String getString(ByteBuffer buffer, int maxLength) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		boolean append = true;
		for (int i = 0; i < maxLength || maxLength < 0; i++) {
			byte b = buffer.get();
			if (b == 0) {
				append = false;
				if (maxLength < 0) {
					break;
				}
			}
			if (append) {
				output.write(b);
			}
		}
		try {
			return new String(output.toByteArray(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			return new String(output.toByteArray());
		}
	}


	public BaseShape(CommonHeader header) {
		this.header = header;
	}

	protected void setStrokeAndFill(Element element, Drawing drawing) {
		
		if (this.header.isFilledLine()) {
			element.setAttribute("fill", "none");
		} else if (isSvgFill() && isFillable()) {
			String fill = drawing.getFillStyle(this.header.fillStyle, this.header.getFillColor());
			element.setAttribute("fill", fill);
			element.setAttribute("fill-rule", "evenodd");
		} else {
			element.setAttribute("fill", "none");
		}

		if (isStroke()) {
			if (this.header.isFilledLine()) {
				element.setAttribute("stroke", drawing.getFillStyle(this.header.fillStyle, this.header.getFillColor()));
				element.setAttribute("stroke-width", formatFloat(header.getStrokeWidth()));
			} else if (header.getStrokeWidth() != 0) {
				element.setAttribute("stroke", drawing.getFillStyle(this.header.fillStyle, this.header.getStrokeColor()));
				element.setAttribute("stroke-width", formatFloat(header.getStrokeWidth()));
			} else if (header.isOutlinePresent() && isFilled()) {
				element.setAttribute("stroke", drawing.getFillStyle(this.header.fillStyle, this.header.getStrokeColor()));
				element.setAttribute("stroke-width", NumberUtil.formatFloat(drawing.getBaseStroke()) + "px");
			} else if (header.isOutlinePresent()) {
				element.setAttribute("stroke", this.header.getStrokeColor());
				element.setAttribute("stroke-width", NumberUtil.formatFloat(drawing.getBaseStroke()) + "px");
			} else {
				element.setAttribute("stroke", "none");
			}
		}
	}
	
	protected boolean isFillable() {
		return true;
	}
	
	protected boolean isSvgFill() {
		return isFilled() && this.header.getStrokeWidth() == 0;
	}
	protected boolean isStroke() {
		return true;
	}

	boolean isFilled() {
		return this.header.isFilled();
	}

	protected void assignId(Element element) {
		element.setAttribute("id", ClassUtils.getShortCanonicalName(getClass()) + "-" + getHeader().tag);
	}

}
