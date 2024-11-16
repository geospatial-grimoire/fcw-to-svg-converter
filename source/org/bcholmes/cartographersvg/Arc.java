package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;
import java.util.Collections;

public class Arc extends BasePath implements Shape {

	private final float deltaAngle;
	private final float radius;
	private Point firstPoint;
	private Point lastPoint;
	private boolean sweepPositive;

	public Arc(CommonHeader header, Point centre, float radius, float startAngle, float deltaAngle) {
		super(Collections.<Point>emptyList(), header);
		this.radius = radius;
		this.deltaAngle = deltaAngle;
		
		this.firstPoint = point(startAngle, radius, centre);
		this.lastPoint = point(startAngle + this.deltaAngle, radius, centre);
		this.sweepPositive = true;
	}

	public static Shape from(ByteBuffer buffer, CommonHeader header) {
		
		Point centre = Point.from(buffer);
		float radius = buffer.getFloat();
		float sAng = buffer.getFloat();
		float angW = buffer.getFloat();
		
		return new Arc(header, centre, radius, sAng, angW);
	}
	
	@Override
	protected String createPathNodes(Point point, CoordinateSystem drawing) {
		Point startPoint = drawing.map(getFirstPoint());
		Point endPoint = drawing.map(getLastPoint());
		
		String initialPath = "";
		
		if (point == null || !point.isApproximatelyEqual(getFirstPoint())) {
			initialPath = "M " + NumberUtil.formatFloat(startPoint.getX()) + "," 
							+ NumberUtil.formatFloat(startPoint.getY());
		}
		return initialPath + " A " + NumberUtil.formatFloat(this.radius) + "," 
			+ NumberUtil.formatFloat(this.radius) + " " 
			+ "0 " + (isLargeArc() ? "1" : "0")
			+ "," + (isSweepPositive() ? "0" : "1") + " " 
			+ NumberUtil.formatFloat(endPoint.getX()) + "," 
			+ NumberUtil.formatFloat(endPoint.getY()); 
	}

	@Override
	public void reverse() {
		Point temp = this.firstPoint;
		this.firstPoint = this.lastPoint;
		this.lastPoint = temp;
		this.sweepPositive = !this.sweepPositive;
	}
	@Override
	public boolean isReversible() {
		return true;
	}

	@Override
	Point getLastPoint() {
		return this.lastPoint;
	}

	@Override
	Point getFirstPoint() {
		return this.firstPoint;
	}
	protected boolean isSweepPositive() {
		return this.sweepPositive;
	}

	protected boolean isLargeArc() {
		return this.deltaAngle > Math.PI;
	}
	
	static Point point(float angle, float radius, Point centre) {
		return new Point(centre.getX() + (float) (radius * Math.cos(angle)), 
				centre.getY() + (float) (radius * Math.sin(angle)));
	}

	@Override
	public boolean isClosed() {
		return false;
	}
	
	@Override
	protected boolean isFillable() {
		return false;
	}	
}
