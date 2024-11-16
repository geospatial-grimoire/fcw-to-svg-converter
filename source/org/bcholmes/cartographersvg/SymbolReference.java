package org.bcholmes.cartographersvg;

import static org.bcholmes.cartographersvg.NumberUtil.formatFloat;

import java.nio.ByteBuffer;

import org.bcholmes.cartographersvg.util.XmlUtil;
import org.w3c.dom.Element;

// Symbol reference is a group because apparently it can contain attributes
public class SymbolReference extends Group implements Shape {

	final String name;
	final Point3d high;
	final Point3d low;
	private final TableMatrix matrix;

	private SymbolReference(CommonHeader header, String name, Point3d low, Point3d high, TableMatrix matrix) {
		super(header);
		this.name = name;
		this.low = low;
		this.high = high;
		this.matrix = matrix;
	}

	public void appendAsXml(Element parent, Drawing drawing) {
		Element element = parent.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "use");
		element.setAttributeNS(XmlUtil.XLINK_NAMESPACE, "href", "#" + SymbolDefinition.createSymbolName(this.name));
		
		Matrix3f symtrans = Matrix3f.createMatrix3fIdentity();
		symtrans.v[0][0] = this.matrix.m11;
		symtrans.v[1][0] = -this.matrix.m12;
		symtrans.v[0][1] = -this.matrix.m21;
		symtrans.v[1][1] = this.matrix.m22;
		symtrans.v[0][2] = drawing.mapX(this.matrix.m41);
		symtrans.v[1][2] = drawing.mapY(this.matrix.m42);
		
		String matrixTransform = "matrix(" + 
				formatFloat(symtrans.v[0][0]) + " " +
				formatFloat(symtrans.v[1][0]) + " " +
				formatFloat(symtrans.v[0][1]) + " " +
				formatFloat(symtrans.v[1][1]) + " " +
				formatFloat(symtrans.v[0][2]) + " " +
				formatFloat(symtrans.v[1][2]) + ")";
		
		element.setAttribute("transform", matrixTransform);
		element.setAttribute("color", this.header.getFillColor());
		parent.appendChild(element);
	}

	static Shape from(ByteBuffer buffer, CommonHeader header) {
		
		Point3d low = Point3d.from(buffer);
		Point3d high = Point3d.from(buffer);
		
		String name = getString(buffer, 32);
		
		int defAdr = buffer.getInt();
		TableMatrix matrix = TableMatrix.from(buffer);
		
		return new SymbolReference(header, name, low, high, matrix);
	}
	
}
