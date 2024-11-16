package org.bcholmes.cartographersvg;

public interface Drawing extends CoordinateSystem {
	public InfoBlockFonts.Font getFont(short fontId);
	public String getFillStyle(short fillStyle, String color);
	public float getBaseStroke();
}
