package org.bcholmes.cartographersvg;

import static org.bcholmes.cartographersvg.NumberUtil.formatFloat;

import java.nio.ByteBuffer;
import java.util.Collections;

public class EllipticalArc extends BasePath {

	private final Point centre;
	private final float radius;
	private final float eccentricity;
	private final float inclination;
	private final float startAngle;
	private final float deltaAngle;
	private Point firstPoint;
	private Point lastPoint;
	private boolean sweepPositive = true;

	public EllipticalArc(CommonHeader header, Point centre, float radius, float eccentricity, float inclination, float startAngle, float deltaAngle) {
		super(Collections.<Point>emptyList(), header);
		this.centre = centre;
		this.radius = radius;
		this.eccentricity = eccentricity;
		this.inclination = inclination;
		this.startAngle = startAngle;
		this.deltaAngle = deltaAngle;
		
		this.firstPoint = point(this.startAngle, this.centre, this.radius, this.eccentricity);
		this.lastPoint = point(this.startAngle + this.deltaAngle, this.centre, this.radius, this.eccentricity);
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
	public boolean isClosed() {
		return false;
	}

	public static Shape from(ByteBuffer buffer, CommonHeader header) {
		Point centre = Point.from(buffer);
		float radius = buffer.getFloat();
		float eccentricity = buffer.getFloat();
		float inclination = buffer.getFloat();
		float sAng = buffer.getFloat();
		float angW = buffer.getFloat();
		return new EllipticalArc(header, centre, radius, eccentricity, inclination, sAng, angW);
	}

	private static float fmod(float value, float modulus) {
		float result = value % modulus;
		return result < 0 ? (result + modulus) : result;
	}
	
	@Override
	Point getFirstPoint() {
		return this.firstPoint;
	}
	
	static Point point(float angle, Point centre, float radius, float eccentricity) {
		float x;
		float y;
		float a = fmod(angle, (float) (Math.PI * 2.0));
		if (a > 3.0f * Math.PI / 2.0f) {
			x = (float) Math.cos(Math.atan(Math.tan(2 * Math.PI - a) / eccentricity));
			y = - (float) Math.sin(Math.atan(Math.tan(2 * Math.PI - a) / eccentricity));
		} else if (a > Math.PI) {
			x = - (float) Math.cos(Math.atan(Math.tan(a - Math.PI) / eccentricity));
			y = - (float) Math.sin(Math.atan(Math.tan(a - Math.PI) / eccentricity));
		} else if (a > Math.PI / 2.0f ) {
			x = - (float) Math.cos(Math.atan(Math.tan(Math.PI - a) / eccentricity));
			y = (float) Math.sin(Math.atan(Math.tan(Math.PI - a) / eccentricity));
		} else {
			x = (float) Math.cos(Math.atan(Math.tan(a) / eccentricity));
			y = (float) Math.sin(Math.atan(Math.tan(a) / eccentricity));
		}
		
		return new Point(centre.getX() + x * radius, centre.getY() + y * radius * eccentricity);
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
		return initialPath + " A " + formatFloat(this.radius) + "," 
			+ formatFloat(this.radius * this.eccentricity) + " " 
			+ formatFloat(this.inclination) + " " + (isLargeArc() ? "1" : "0")
			+ "," + (isSweepPositive() ? "0" : "1") + " " 
			+ formatFloat(endPoint.getX()) + "," 
			+ formatFloat(endPoint.getY()); 
	}

	protected boolean isSweepPositive() {
		return this.sweepPositive;
	}

	protected boolean isLargeArc() {
		return this.deltaAngle > Math.PI;
	}

	@Override
	Point getLastPoint() {
		return this.lastPoint;
	}
	
	@Override
	protected boolean isFillable() {
		return false;
	}
}
