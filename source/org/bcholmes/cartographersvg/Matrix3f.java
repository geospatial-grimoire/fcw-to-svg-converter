package org.bcholmes.cartographersvg;

public class Matrix3f {

	float[][] v = new float[3][3];

	public static Matrix3f createMatrix3fIdentity() {
		Matrix3f r = new Matrix3f();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				r.v[i][j] = 0.0f;
			}
		}
		for (int k = 0; k < 3; k++) {
			r.v[k][k] = 1.0f;
		}
		return r;
	}

	public static Matrix3f multiply(Matrix3f a, Matrix3f b) {
		Matrix3f c = new Matrix3f();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				float s = 0.0f;
				for (int k = 0; k < 3; k++) {
					s += a.v[k][j] * b.v[i][k];
				}
				c.v[i][j] = s;
			}
		}
		return c;
	}
}
