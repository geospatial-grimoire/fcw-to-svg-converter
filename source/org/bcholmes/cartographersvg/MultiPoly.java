package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.bcholmes.cartographersvg.util.XmlUtil;
import org.w3c.dom.Element;

public class MultiPoly extends Group {
	
	enum OrganizationPhase {
		INITIAL, REVERSE;
		
		OrganizationPhase next() {
			return this == INITIAL ? REVERSE : null;
		}
	}
	
	class SubPath {
		
		List<BasePath> elements = new ArrayList<BasePath>();
		private boolean closed;
		
		SubPath(BasePath basePath) {
			this.elements.add(basePath);
			this.closed = basePath.isClosed();
		}
		
		Point getFirstPoint() {
			return this.elements.get(0).getFirstPoint();
		}
		
		Point getLastPoint() {
			return this.elements.get(this.elements.size()-1).getLastPoint();
		}

		void join(SubPath subPath) {
			this.elements.addAll(subPath.elements);
		}

		public boolean isClosed() {
			return this.closed;
		}
		public boolean isReversible() {
			boolean result = true;
			for (BasePath path : this.elements) {
				result &= path.isReversible();
			}
			return result;
		}
		public void reverse() {
			if (isReversible()) {
				for (BasePath path : this.elements) {
					path.reverse();
				}
				Collections.reverse(elements);
			}
		}
	}

	private final short multiPolyFlags;

	public MultiPoly(CommonHeader header, short multiPolyFlags) {
		super(header);
		this.multiPolyFlags = multiPolyFlags;
	}

	public void appendAsXml(Element parent, Drawing drawing) {
		if (!getSublist().isEmpty()) {
			Element path = parent.getOwnerDocument().createElementNS(XmlUtil.SVG_NAMESPACE, "path");
			assignId(path);

			String d = getPathCoordinates(drawing);
			path.setAttribute("d", d.trim());
			setStrokeAndFill(path, drawing);
			parent.appendChild(path);
		}
	}

	private String getPathCoordinates(Drawing drawing) {
		String d = "";
		Point start = null;
		for (SubPath subPath : getSubPaths()) {
			for (BasePath basePath : subPath.elements) {
				d += (" " + basePath.createPathNodes(start, drawing));
				start = basePath.getLastPoint();
			}
		}
		return d;
	}

	public static Shape from(ByteBuffer buffer, CommonHeader header) {
		short flags = buffer.getShort();
		return new MultiPoly(header, flags);
	}
	
	List<SubPath> getSubPaths() {
		if (this.header.tag == 4446) {
			System.out.println("H");
		}
		
		List<SubPath> subPaths = new ArrayList<MultiPoly.SubPath>();
		for (Shape shape : getSublist()) {
			if (shape instanceof BasePath) {
				subPaths.add(new SubPath((BasePath) shape));
			} else {
				throw new RuntimeException("Multipoly doens't support " + ClassUtils.getShortCanonicalName(shape.getClass()));
			}
		}

		boolean done = false;
		List<SubPath> result = null;
		OrganizationPhase phase = OrganizationPhase.INITIAL;
		while (!done) {
			int originalSize = subPaths.size();
			
			result = organize(phase, subPaths);
			if (result.size() == 1) {
				done = true;
			} else if (result.size() == originalSize) {
				phase = phase.next();
				if (phase == null) {
					done = true;
				}
			}
			
			subPaths = result;
		}

		
		return result;
	}

	private List<SubPath> organize(OrganizationPhase phase, List<SubPath> subPaths) {

		List<SubPath> result = new ArrayList<MultiPoly.SubPath>();
		while (!subPaths.isEmpty()) {
			SubPath current = subPaths.remove(0);
			if (!current.isClosed()) {
				for (SubPath subPath : result) {
					//System.out.println("compare: [" + current.getFirstPoint() + "],[" + current.getLastPoint() + "] -> [" + subPath.getFirstPoint() + "],[" + subPath.getLastPoint() + "]");
					if (subPath.isClosed()) {
						// leave it alone
					} else if (current.getFirstPoint().isApproximatelyEqual(subPath.getLastPoint())) {
						subPath.join(current);
						current = null;
						break;
					} else if (phase == OrganizationPhase.REVERSE &&
							subPath.isReversible() && 
							current.getFirstPoint().isApproximatelyEqual(subPath.getFirstPoint())) {
						subPath.reverse();
						subPath.join(current);
						current = null;
						break;
					} else if (phase == OrganizationPhase.REVERSE &&
							current.isReversible() && 
							subPath.getLastPoint().isApproximatelyEqual(current.getLastPoint())) {
						current.reverse();
						subPath.join(current);
						current = null;
						break;
					}
				}
			}
			if (current != null) {
				result.add(current);
			}
		}
		return result;
	}
}
