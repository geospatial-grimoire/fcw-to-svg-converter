package org.bcholmes.cartographersvg;

public class Paint {

	private final String id;

	public Paint(String string) {
		this.id = string;
	}

	public String toSvgReference() {
		return this.id;
	}
}
