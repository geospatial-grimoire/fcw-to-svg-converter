package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;

import org.apache.commons.lang.ClassUtils;
import org.bcholmes.cartographersvg.util.XmlUtil;
import org.w3c.dom.Element;

public class Sheet extends Group {
	
	private static int sheetNumber = 1;

	private final int status;
	private final String name;

	private final int number;

	public Sheet(CommonHeader header, int status, String name, int number) {
		super(header);
		this.status = status;
		this.name = name;
		this.number = number;
	}
	
	@Override
	protected Element createGroupElement(Element parent) {
		Element g = super.createGroupElement(parent);
		g.setAttributeNS(XmlUtil.INKSCAPE_NAMESPACE, "groupmode", "layer");
		g.setAttributeNS(XmlUtil.INKSCAPE_NAMESPACE, "label", this.name);
		if (isHidden()) {
			g.setAttribute("style", "display:none");
		}
		/*
		if (layer.isLocked()) {
			g.setAttributeNS(XmlUtil.SODIPODI_NAMESPACE, "insensitive", "true");
		}
		*/
		return g;
	}

	private boolean isHidden() {
		return (status & 2) == 0;
	}

	@Override
	protected void assignId(Element element) {
		element.setAttribute("id", 
				ClassUtils.getShortClassName(getClass()) + "-" + this.number + "-" + StringSanitizer.sanitize(this.name));
	}
	
	public static Shape from(ByteBuffer buffer, CommonHeader header) {
		int status = buffer.getInt();
		String name = getString(buffer, 64);
		return new Sheet(header, status, name, sheetNumber++);
	}

}
