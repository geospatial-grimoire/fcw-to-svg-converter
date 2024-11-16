package org.bcholmes.packing;


public class PackItemImpl implements PackItem {

	private final int width;
	private final int height;
	private final Object item;
	private int x;
	private int y;

	public PackItemImpl(int width, int height) {
		this(width, height, null);
	}
	public PackItemImpl(int width, int height, Object item) {
		this.width = width;
		this.height = height;
		this.item = item;
	}
	public int getHeight() {
		return this.height;
	}
	public int getWidth() {
		return this.width;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Object getItem() {
		return this.item;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
}
