package org.bcholmes.cartographersvg;

class StringSanitizer {

	static String sanitize(String name) {
		StringBuilder builder = new StringBuilder();
		for (char c : name.trim().toCharArray()) {
			if ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-0123456789".indexOf(c) >= 0) {
				builder.append(c);
			} else {
				builder.append("_");
			}
		}
		return builder.toString();
	}
}
