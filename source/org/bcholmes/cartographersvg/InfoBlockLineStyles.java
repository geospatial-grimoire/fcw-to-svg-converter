package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class InfoBlockLineStyles extends InfoBlock {
	
	public static class LineStyle {

		private final String name;
		private final short id;
		List<Float> segments = new ArrayList<Float>();
		private final short flags;

		public LineStyle(short id, String name, short flags) {
			this.id = id;
			this.name = name;
			this.flags = flags;
		}
		
	}


	private List<LineStyle> styles = new ArrayList<InfoBlockLineStyles.LineStyle>();

	protected InfoBlockLineStyles(int length, int etype, int itype) {
		super(length, etype, itype);
	}

	
	public static InfoBlockLineStyles from(int length, int etype, int itype, ByteBuffer buffer) {
		InfoBlockLineStyles result = new InfoBlockLineStyles(length, etype, itype);
		
		buffer.getInt();
		buffer.get();
		buffer.get();

		buffer.getShort();
		buffer.getInt();
		int version = buffer.getShort();
		buffer.getShort();
		int count = buffer.getShort();
		
		for (int i = 0; i < count; i++) {
			int rLength = buffer.getInt();
			short id = buffer.getShort();
			buffer.getFloat();
			float styleLength = buffer.getFloat();
			int numberOfSegments = buffer.getShort();
			short flags = buffer.getShort();
			String name = BaseShape.getString(buffer, 32);
			System.out.println(id + " > " + rLength + " " + numberOfSegments + " " + name + " " + styleLength + " " + flags);
			
			LineStyle style = new LineStyle(id, name, flags);
			for (int j = 0; j < numberOfSegments; j++) {
				style.segments.add( buffer.getFloat());
			}
			result.styles.add(style);
			System.out.println(style.segments);
		}
		
		
		return result;
	}
}
