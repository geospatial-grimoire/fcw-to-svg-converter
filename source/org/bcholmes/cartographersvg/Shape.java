package org.bcholmes.cartographersvg;

import org.w3c.dom.Element;

public interface Shape {
	public void appendAsXml(Element parent, Drawing drawing);
	public CommonHeader getHeader();
	public boolean isSymbolDefinition();
}
