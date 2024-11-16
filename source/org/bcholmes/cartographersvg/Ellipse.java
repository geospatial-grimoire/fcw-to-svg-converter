package org.bcholmes.cartographersvg;

import static org.bcholmes.cartographersvg.NumberUtil.formatFloat;

import java.nio.ByteBuffer;

import org.bcholmes.cartographersvg.util.XmlUtil;
import org.w3c.dom.Element;

public class Ellipse extends BasePath {

	private final Point centre;
	private final float radius;
	private final float eccentricity;
	private final float inclination;

	public Ellipse(CommonHeader header, Point centre, float radius, float eccentricity, float inclination) {
		super(header);
		this.centre = centre;
		this.radius = radius;
		this.eccentricity = eccentricity;
		this.inclination = inclination;
	}
	
	@Override
	public void appendAsXml(Element parent, Drawing drawing) {
		
		Element element = parent.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "ellipse");
		assignId(element);
		Point mapped = drawing.map(this.centre);
		element.setAttribute("cx", formatFloat(mapped.getX()));
		element.setAttribute("cy", formatFloat(mapped.getY()));
		element.setAttribute("rx", formatFloat(this.radius));
		element.setAttribute("ry", formatFloat(this.radius * this.eccentricity));

		element.setAttribute("transform", "rotate(" + NumberUtil.formatFloat((float) (this.inclination * 180.0 / Math.PI)) + ")");
		setStrokeAndFill(element, drawing);
		parent.appendChild(element);
	}
	
	@Override
	Point getFirstPoint() {
		return EllipticalArc.point(0.0f, this.centre, this.radius, this.eccentricity);
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
			Point nextPoint = drawing.map(EllipticalArc.point((float) (Math.PI * i / 2.0), this.centre, this.radius, this.eccentricity));
			builder.append(" A ").append(NumberUtil.formatFloat(this.radius)).append(",")
					.append(NumberUtil.formatFloat(this.radius * this.eccentricity))
					.append(NumberUtil.formatFloat(this.inclination)).append(" 0,0 ")
					.append(NumberUtil.formatFloat(nextPoint.getX())).append(",")
					.append(NumberUtil.formatFloat(nextPoint.getY()));
		}
		
		builder.append(" Z ");
		return builder.toString();
	}
	

	@Override
	public boolean isClosed() {
		return true;
	}

	public static Shape from(ByteBuffer buffer, CommonHeader header) {
		Point centre = Point.from(buffer);
		float radius = buffer.getFloat();
		float eccentricity = buffer.getFloat();
		float inclination = buffer.getFloat();
		return new Ellipse(header, centre, radius, eccentricity, inclination);
	}
}
