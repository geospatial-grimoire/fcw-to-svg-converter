package org.bcholmes.cartographersvg;

import java.util.ArrayList;
import java.util.List;

import org.bcholmes.cartographersvg.util.XmlUtil;
import org.w3c.dom.Element;

public class Group extends BaseShape {

	private List<Shape> sublist = new ArrayList<Shape>();

	public Group(CommonHeader header) {
		super(header);
	}
	public List<Shape> getSublist() {
		return this.sublist;
	}

	public void appendAsXml(Element parent, Drawing drawing) {
		if (!this.sublist.isEmpty()) {
			Element g = createGroupElement(parent);
			for (Shape entity : this.sublist) {
				entity.appendAsXml(g, drawing);
			}
			parent.appendChild(g);
		}
	}
	protected Element createGroupElement(Element parent) {
		Element g = parent.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "g");
		assignId(g);
		return g;
	}
}
