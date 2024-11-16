package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Line extends BasePath implements Shape {

	Line(List<Point> nodes, CommonHeader header) {
		super(nodes, header);
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	public static Line from(ByteBuffer buffer, CommonHeader header) {
		
		List<Point> nodes = new ArrayList<Point>();
		nodes.add(Point.from(buffer));
		nodes.add(Point.from(buffer));
		
		return new Line(nodes, header);
	}
	
	@Override
	protected boolean isFillable() {
		return false;
	}
}
