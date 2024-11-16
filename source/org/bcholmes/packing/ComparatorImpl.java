package org.bcholmes.packing;

import java.util.Comparator;

class ComparatorImpl implements Comparator<Dimension> {
	public int compare(Dimension d0, Dimension d1) {
		if (d1.getHeight() == d0.getHeight()) {
			return d1.getWidth() - d0.getWidth();
		} else {
			return d1.getHeight() - d0.getHeight();
		}
	}
}
