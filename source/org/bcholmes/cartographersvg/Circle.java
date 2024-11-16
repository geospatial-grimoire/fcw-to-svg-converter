package org.bcholmes.cartographersvg;

import static org.bcholmes.cartographersvg.NumberUtil.formatFloat;

import java.nio.ByteBuffer;

import org.bcholmes.cartographersvg.util.XmlUtil;
import org.w3c.dom.Element;

public class Circle extends BasePath implements Shape {
	
	private final float radius;
	private final Point centre;

	public Circle(CommonHeader header, Point centre, float radius) {
		super(header);
		this.centre = centre;
		this.radius = radius;
	}

	public static Circle from(ByteBuffer buffer, CommonHeader header) {
		Point centre = Point.from(buffer);
		float radius = buffer.getFloat();
		return new Circle(header, centre, radius);
	}
	
	@Override
	public String toString() {
		return "Circle (x=" + this.centre.getX() + ",y=" + this.centre.getY() + ",radius=" + radius + ")";
	}

	public void appendAsXml(Element parent, Drawing drawing) {
		Element circle = parent.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "circle");
		assignId(circle);
		Point mapped = drawing.map(this.centre);
		circle.setAttribute("cx", formatFloat(mapped.getX()));
		circle.setAttribute("cy", formatFloat(mapped.getY()));
		circle.setAttribute("r", formatFloat(this.radius));

		setStrokeAndFill(circle, drawing);
		parent.appendChild(circle);
	}

	@Override
	public boolean isClosed() {
		return true;
	}
	
	@Override
	Point getFirstPoint() {
		return Arc.point(0.0f, this.radius, this.centre);
	}
	Point getLastPoint() {
		return getFirstPoint();
	}
	
	@Override
	protected String createPathNodes(Point ignored, CoordinateSystem drawing) {
		StringBuilder builder = new StringBuilder();
		
		Point startPoint = drawing.map(getFirstPoint());
		builder.append("M " + NumberUtil.formatFloat(startPoint.getX()) + "," 
					+ NumberUtil.formatFloat(startPoint.getY()));
		
		for (int i = 1; i <= 4; i++) {
			Point nextPoint = drawing.map(Arc.point((float) (Math.PI * i / 2.0), this.radius, this.centre));
			builder.append(" A ").append(NumberUtil.formatFloat(this.radius)).append(",")
					.append(NumberUtil.formatFloat(this.radius)).append(" 0 0,0 ")
					.append(NumberUtil.formatFloat(nextPoint.getX())).append(",")
					.append(NumberUtil.formatFloat(nextPoint.getY()));
		}
		
		builder.append(" Z ");
		return builder.toString();
	}
}
