package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;

public class ShapeFactory {

	public static Shape createShape(CommonHeader header) {

		ByteBuffer buffer = header.rest;
		switch (header.getType()) {
		case 1:
			return PointShape.from(buffer, header);
		case 2:
			return Line.from(buffer, header);
		case 3:
			return Path.from(buffer, header);
		case 5:
			return Label.from(buffer, header);
		case 6:
			return Circle.from(buffer, header);
		case 7:
			return Arc.from(buffer, header);
		case 8:
			return Ellipse.from(buffer, header);
		case 9:
			return EllipticalArc.from(buffer, header);
		case 0x11:
			return MultiPoly.from(buffer, header);
		case 0x12:
			return new Group(header);
		case 0x1C:
			return SymbolDefinition.from(buffer, header);
		case 0x1D:
			return SymbolReference.from(buffer, header);
		case 41:
			return Sheet.from(buffer, header); // sheet?
		default:
			return new UnknownShape(header);
		}
	}
}
