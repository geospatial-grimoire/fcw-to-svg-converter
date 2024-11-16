package org.bcholmes.cartographersvg;

public interface CoordinateSystem {
	public Point map(Point point);
	public float mapX(float x);
	public float mapY(float y);
}
