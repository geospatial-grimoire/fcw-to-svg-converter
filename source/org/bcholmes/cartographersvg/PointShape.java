package org.bcholmes.cartographersvg;

import static org.bcholmes.cartographersvg.NumberUtil.formatFloat;

import java.nio.ByteBuffer;

import org.w3c.dom.Element;

public class PointShape extends BaseShape {

	private final Point point;

	public PointShape(CommonHeader header, Point point) {
		super(header);
		this.point = point;
	}

	public void appendAsXml(Element parent, Drawing drawing) {
		/*
		Element circle = parent.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "circle");
		assignId(circle);
		Point mapped = drawing.map(this.point);
		circle.setAttribute("cx", formatFloat(mapped.getX()));
		circle.setAttribute("cy", formatFloat(mapped.getY()));
		circle.setAttribute("r", formatFloat(1.0f));

		setStrokeAndFill(circle, drawing);
		parent.appendChild(circle);
		*/
	}

	public static Shape from(ByteBuffer buffer, CommonHeader header) {
		Point point = Point.from(buffer);
		return new PointShape(header, point);
	}
	
	@Override
	protected boolean isFillable() {
		return false;
	}
}
