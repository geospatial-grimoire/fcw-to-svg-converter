package org.bcholmes.cartographersvg;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class Path extends BasePath implements Shape {
	
	public enum SmootherType {
		NO_SMOOTHING, CUBIC_B_SPLINE, PARABOLIC_BLEND, BEZIER
	}

	private final byte flags;
	private final byte smootherType;
	private final byte smootherResolution;
	private final float startParm;
	private final float endParm;

	private Path(List<Point> nodes, byte flags, CommonHeader header, byte smootherType, byte smootherResolution, float startParm, float endParm) {
		super(nodes, header);
		this.flags = flags;
		this.smootherType = smootherType;
		this.smootherResolution = smootherResolution;
		this.startParm = startParm;
		this.endParm = endParm;
		
		if (getSmootherType() == SmootherType.BEZIER || getSmootherType() == SmootherType.PARABOLIC_BLEND) {
			System.out.println("resolution --> " + this.smootherResolution);
			System.out.println("start --> " + this.startParm);
			System.out.println("end   --> " + this.endParm);
			for (Point point : nodes) {
				System.out.println("point --> " + point);
			}
		}
	}

	@Override
	public boolean isClosed() {
		return (this.flags & 0x80) != 0;
	}
	
	public SmootherType getSmootherType() {
		return SmootherType.values()[this.smootherType];
	}
	
	@Override
	protected String createPathNodes(Point startPoint, CoordinateSystem drawing) {
		if (getSmootherType() == SmootherType.CUBIC_B_SPLINE) {
			return createBezierFromCubicBSpline(startPoint, drawing);
		} else {
			return super.createPathNodes(startPoint, drawing);
		}
	}

	private String createBezierFromCubicBSpline(Point startPoint,
			CoordinateSystem drawing) {
		List<Point> b = this.nodes;
		StringBuilder builder = new StringBuilder();
		if (startPoint == null || !startPoint.isApproximatelyEqual(getFirstPoint())) {
			appendPointData("M", getFirstPoint(), builder, drawing);
		}
		
		for (int i = 1, length = b.size(); i < length; i++) {
			
			Point c1 = b.get(i-1).multiply(2.0f / 3.0f).add(b.get(i).multiply(1.0f / 3.0f));
			Point c2 = b.get(i-1).multiply(1.0f / 3.0f).add(b.get(i).multiply(2.0f / 3.0f));
			
			appendPointData(" C ", c1, builder, drawing);
			appendPointData(" ", c2, builder, drawing);
			appendPointData(" ", calculateS(i, b), builder, drawing);
		}
		
		return builder.toString();
	}
	
	private Point calculateS(int i, List<Point> b) {
		if (i == 0) {
			return b.get(0);
		} else if (i == b.size()-1) {
			return b.get(b.size()-1);
		} else {
			Point b0 = b.get(i-1);
			Point b1 = b.get(i);
			Point b2 = b.get(i+1);
			
			Point s = (b0.multiply(1.0f / 6.0f)).add((b1.multiply(2.0f / 3.0f))).add(b2.multiply(1.0f / 6.0f));
			return s;
		}
	}

	@Override
	boolean isFilled() {
		return (this.flags & 0x40) != 0 ||
			(super.isFilled() && isClosed());
	}

	public static Path from(ByteBuffer buffer, CommonHeader header) {
		
		byte smootherType = buffer.get();
		byte smootherResolution = buffer.get();
		float startParm = buffer.getFloat();
		float endParm = buffer.getFloat();
		short count = buffer.getShort();
		byte extraFlags = buffer.get();
		byte unused = buffer.get();
		
		List<Point> nodes = new ArrayList<Point>();
		for (int i = 0; i < count; i++) {
			nodes.add(Point.from(buffer));
		}
		
		return new Path(nodes, extraFlags, header, smootherType, smootherResolution, startParm, endParm);
	}

}
