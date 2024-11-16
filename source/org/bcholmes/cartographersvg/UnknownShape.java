package org.bcholmes.cartographersvg;

import org.w3c.dom.Element;

public class UnknownShape extends Group {

	public UnknownShape(CommonHeader header) {
		super(header);
		System.out.println("Unknown shape " + (int) header.getType());
		
	}

	public void appendAsXml(Element parent, Drawing drawing) {
		
	}
}
