package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;

public class TableMatrix {

	float m11;    		// first column
	float m21;
	float m31;
	float m41;
	float m12;    		// second column
	float m22;
	float m32;
	float m42;
	float m13;		    // third column
	float m23;
	float m33;
	float m43;
	
	public TableMatrix(float m11, float m21, float m31, float m41, float m12,
			float m22, float m32, float m42, float m13, float m23, float m33,
			float m43) {
		super();
		this.m11 = m11;
		this.m21 = m21;
		this.m31 = m31;
		this.m41 = m41;
		this.m12 = m12;
		this.m22 = m22;
		this.m32 = m32;
		this.m42 = m42;
		this.m13 = m13;
		this.m23 = m23;
		this.m33 = m33;
		this.m43 = m43;
	}
	
	public static TableMatrix from(ByteBuffer buffer) {
		
		float m11 = buffer.getFloat();
		float m21 = buffer.getFloat();
		float m31 = buffer.getFloat();
		float m41 = buffer.getFloat();
		float m12 = buffer.getFloat();
		float m22 = buffer.getFloat();
		float m32 = buffer.getFloat();
		float m42 = buffer.getFloat();
		float m13 = buffer.getFloat();
		float m23 = buffer.getFloat();
		float m33 = buffer.getFloat();
		float m43 = buffer.getFloat();

		return new TableMatrix(m11, m21, m31, m41, m12, m22, m32, m42, m13, m23, m33, m43);
	}
	
}
