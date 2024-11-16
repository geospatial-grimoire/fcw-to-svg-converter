package org.bcholmes.cartographersvg;

import static org.bcholmes.cartographersvg.NumberUtil.formatFloat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bcholmes.cartographersvg.util.XmlUtil;
import org.w3c.dom.Element;

public abstract class BasePath extends BaseShape {

	final List<Point> nodes = new ArrayList<Point>();

	public BasePath(List<Point> nodes, CommonHeader header) {
		super(header);
		this.nodes.addAll(nodes);
	}

	public BasePath(CommonHeader header) {
		this(Collections.<Point>emptyList(), header);
	}

	public void appendAsXml(Element parent, Drawing drawing) {
		parent.appendChild(createPathElement(parent, drawing));
	}

	protected Element createPathElement(Element parent, Drawing drawing) {
		Element path = parent.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "path");
		assignId(path);
		
		path.setAttribute("d", createPathNodes(null, drawing));
		setStrokeAndFill(path, drawing);
		return path;
	}

	protected String createPathNodes(Point startPoint, CoordinateSystem drawing) {
		StringBuilder builder = new StringBuilder();
		if (startPoint == null || !startPoint.isApproximatelyEqual(getFirstPoint())) {
			appendPointData("M", getFirstPoint(), builder, drawing);
		}
		for (Point point : this.nodes.subList(1, this.nodes.size())) {
			appendPointData(" L", point, builder, drawing);
		}
		if (isClosed()) {
			builder.append(" Z");
		}
		return builder.toString();
	}

	public boolean isReversible() {
		return true;
	}
	
	public void reverse() {
		Collections.reverse(this.nodes);
	}

	Point getFirstPoint() {
		return this.nodes.get(0);
	}

	Point getLastPoint() {
		return isClosed() ? null : this.nodes.get(this.nodes.size()-1);
	}
	
	protected void appendPointData(String command, Point point,
			StringBuilder builder, CoordinateSystem drawing) {
		Point mapped = drawing.map(point);
		builder.append(command).append(formatFloat(mapped.getX())).append(",").append(formatFloat(mapped.getY()));
	}

	public abstract boolean isClosed();
}
