package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;

import org.w3c.dom.Element;

public class SymbolDefinition extends Group implements Shape {

	private final int flags;
	private final String name;
	private final Point3d low;
	private final Point3d high;

	public SymbolDefinition(CommonHeader header, String name, int flags, Point3d low, Point3d high) {
		super(header);
		this.name = name;
		this.flags = flags;
		this.low = low;
		this.high = high;
	}

	protected Element createGroupElement(Element parent) {
		Element g = super.createGroupElement(parent);
		g.setAttribute("id", createSymbolName(getName()));
		g.setAttribute("title", getName());
		return g;
	}
	
	public static Shape from(ByteBuffer buffer, CommonHeader header) {
		Point3d low = Point3d.from(buffer);
		Point3d high = Point3d.from(buffer);
		
		int flags = buffer.getInt();
		String name = BaseShape.getString(buffer, 32);
		
		return new SymbolDefinition(header, name, flags, low, high);
	}

	public String getName() {
		return this.name;
	}

	public static String createSymbolName(String name) {
		return "Symbol-" + StringSanitizer.sanitize(name);
	}
	public float getWidth() {
		return this.high.getX() - this.low.getX();
	}
	public float getHeight() {
		return this.high.getY() - this.low.getY();
	}

	public Point3d getLow() {
		return this.low;
	}

	public Point3d getHigh() {
		return this.high;
	}
	public boolean isSymbolDefinition() {
		return true;
	}
	
}
