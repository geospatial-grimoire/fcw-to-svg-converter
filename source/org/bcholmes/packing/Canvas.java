package org.bcholmes.packing;


class Canvas {

	int height;
	int width;
	
	int maxWidth;
	int maxHeight;
	
	boolean[][] cells;
	
	Canvas(int width, int height) {
		this.height = height;
		this.width = width;
		this.maxWidth = 0;
		this.maxHeight = 0;
		cells = new boolean[height][width];
	}
	
	void print() {
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				System.out.print(this.cells[i][j] ? "X" : ".");
			}
			System.out.println();
		}
	}
	
	void fill(int x, int y, int width, int height) {
		for (int i = y; i < y+height; i++) {
			for (int j = x; j < x+width; j++) {
				this.cells[i][j] = true;
			}
		}
		this.maxHeight = Math.max(this.maxHeight, y+height);
		this.maxWidth = Math.max(this.maxWidth, x+width);
	}
	boolean canFit(int x, int y, int width, int height) {
		boolean isPartiallyFilled = false;
		outer: for (int i = y; i < y+height; i++) {
			for (int j = x; j < x+width; j++) {
				isPartiallyFilled |= this.cells[i][j];
				if (isPartiallyFilled) {
					break outer;
				}
			}
		}
		return !isPartiallyFilled;
	}
	
	Point assignBestLocation(int width, int height) {
		Point result = null;
		outer: for (int x = 0; x <= this.width - width; x++) {
			for (int y = 0; y <= this.height - height; y++) {
				if (!this.cells[y][x] && canFit(x, y, width, height)) {
					fill(x, y, width, height);
					result = new Point(x, y);
					break outer;
				}
			}
		}
		return result;
	}
	Point assignBestLocation(Dimension dimension) {
		return assignBestLocation(dimension.getWidth(), dimension.getHeight());
	}

	int getWidth() {
		return this.width;
	}

	public int getArea() {
		return this.height * this.width;
	}

	void trim() {
		trimWidth();
		trimHeight();
	}
	
	void trimWidth() {
		this.width = this.maxWidth;
	}
	void trimHeight() {
		this.height = this.maxHeight;
	}

	public int getHeight() {
		return this.height;
	}	
}
