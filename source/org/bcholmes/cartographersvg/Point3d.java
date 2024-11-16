package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;

public class Point3d {

	private final float x;
	private final float y;
	private final float z;

	private Point3d(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Point3d from(ByteBuffer buffer) {
		float x = buffer.getFloat();
		float y = buffer.getFloat();
		float z = buffer.getFloat();
		return new Point3d(x, y, z);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

}
