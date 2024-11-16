package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;

import org.apache.commons.lang.builder.EqualsBuilder;

public class Point {

	private final float y;
	private final float x;

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public static Point from(ByteBuffer buffer) {
		float x = buffer.getFloat();
		float y = buffer.getFloat();
		return new Point(x, y);
	}

	public float getY() {
		return this.y;
	}

	public float getX() {
		return this.x;
	}

	public boolean isApproximatelyEqual(Point point) {
		if (point == null) {
			return false;
		} else {
			return Math.abs(this.x - point.x) <= 0.015 && 
				Math.abs(this.y - point.y) <= 0.015;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj.getClass() != this.getClass()) {
			return false;
		} else {
			Point that = (Point) obj;
			return new EqualsBuilder().append(this.x, that.x).append(this.y, that.y).isEquals();
		}
	}

	public Point multiply(float m) {
		return new Point(this.x * m, this.y * m);
	}
	
	public Point add(Point point) {
		return new Point(this.x + point.x, this.y + point.y);
	}
	
	@Override
	public String toString() {
		return NumberUtil.formatFloat(this.x) + "," + NumberUtil.formatFloat(this.y);
	}
}
