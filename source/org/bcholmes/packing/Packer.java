package org.bcholmes.packing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Packer {

	class DimensionImpl implements Dimension {

		private int width;
		private int heigth;

		public DimensionImpl(int width, int heigth) {
			super();
			this.width = width;
			this.heigth = heigth;
		}
		public int getHeight() {
			return this.heigth;
		}
		public int getWidth() {
			return this.width;
		}
	}
	
	class InterimResultItem {
		private final PackItem item;
		private final Point point;

		InterimResultItem(PackItem item, Point point) {
			this.item = item;
			this.point = point;
		}
		public PackItem getItem() {
			return this.item;
		}
		public Point getPoint() {
			return this.point;
		}
	}
	
	class InterimResult {
		
		List<InterimResultItem> items = new ArrayList<InterimResultItem>();
		private final Canvas canvas;
		
		public InterimResult(Canvas canvas) {
			this.canvas = canvas;
		}
		
		void addItem(PackItem packItem, Point point) {
			this.items.add(new InterimResultItem(packItem, point));
		}

		public int getArea() {
			return this.canvas.getArea();
		}

		public Canvas getCanvas() {
			return this.canvas;
		}
	}
	
	public Dimension pack(Collection<? extends PackItem> items) {

		System.out.println("packing " + items.size() + " items");
		List<PackItem> list = new ArrayList<PackItem>(items);
		Collections.sort(list, new ComparatorImpl());
		
		int trivialWidth = 0;
		int totalArea = 0;
		int minimumWidth = 0;
		for (PackItem packItem : list) {
			trivialWidth += packItem.getWidth();
			totalArea += (packItem.getHeight() * packItem.getWidth());
			minimumWidth = Math.max(minimumWidth, packItem.getWidth());
		}
		
		int height = list.get(0).getHeight();
		InterimResult best = null;
		InterimResult current = new InterimResult(new Canvas(trivialWidth, height));
		for (int width = current.getCanvas().getWidth(); width >= minimumWidth; ) {

//System.out.println("trying " + current.getCanvas().getWidth() + " x " + current.getCanvas().getHeight());
			
			if (best != null && current.getCanvas().getArea() >= best.getCanvas().getArea()) {
				width = current.getCanvas().getWidth() - 1;
			} else if (current.getCanvas().getArea() >= totalArea) {
				boolean allFit = true;
				for (PackItem packItem : list) {
					Point point = current.getCanvas().assignBestLocation(packItem);
					if (point == null) {
						allFit = false;
						break;
					} else {
						current.addItem(packItem, point);
					}
				}
				
				if (allFit) {
					current.getCanvas().trimWidth();
					if (best == null || best.getArea() > current.getArea()) {
						best = current;
						if (isLargeArea(trivialWidth) && isCloseEnoughToOptimized(totalArea, best)) {
							// this is a good-enough result
							break;
						}
					}
					width = current.getCanvas().getWidth() - 1;
					height += getHeightIncrease(current);
				} else {
					height++;
				}
			} else {
				height++;
			}
			
			current = new InterimResult(new Canvas(width, height));
		}
		
		return activateBestResult(best);
	}

	private boolean isCloseEnoughToOptimized(int totalArea, InterimResult best) {
		return best.getArea() < (totalArea * 1.25);
	}

	private boolean isLargeArea(int trivialWidth) {
		return trivialWidth > 3000;
	}

	private int getHeightIncrease(InterimResult current) {
		int xEdge = current.getCanvas().getWidth();
		int height = 1;
		for (InterimResultItem item : current.items) {
			if (item.getPoint().getX() + item.getItem().getWidth() == xEdge) {
				height = Math.max(height, item.getItem().getHeight());
			}
		}
		System.out.println(" min height = " + height);
		return height;
	}

	private Dimension activateBestResult(InterimResult best) {
		for (InterimResultItem item : best.items) {
			item.getItem().setX(item.getPoint().getX());
			item.getItem().setY(item.getPoint().getY());
		}
		
//		best.getCanvas().print();
		
		return new DimensionImpl(best.getCanvas().getWidth(), best.getCanvas().getHeight());
	}
}
