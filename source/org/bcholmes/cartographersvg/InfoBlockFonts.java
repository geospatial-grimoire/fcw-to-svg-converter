package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class InfoBlockFonts extends InfoBlock {

	public static class Font {

		short id;
		public String name;
		public String getName() {
			return this.name;
		}
		
	}

	List<Font> fonts = new ArrayList<Font>();
	
	protected InfoBlockFonts(int length, int etype, int itype) {
		super(length, etype, itype);
	}

	public static InfoBlock from(int length, int etype, int itype,
			ByteBuffer buffer) {
		
		short infoBlockVersion = 2;
		
		InfoBlockFonts result = new InfoBlockFonts(length, etype, itype);
		buffer.getInt();
		buffer.get();
		buffer.get();
		
		short creator = buffer.getShort();
		int allocationLength = buffer.getInt();
		short count = buffer.getShort();
		if ((count & 0xffff) == 0xffff) {
			infoBlockVersion = buffer.getShort();
			short nextId = buffer.getShort();
		}
		
		int fontLength = buffer.getInt();
		while (fontLength > 0) {
			Font font = new Font();
			result.fonts.add(font);
			font.id = buffer.getShort();
			short typeId = buffer.getShort();
			if (typeId == 2 || typeId == 0) {
				font.name = BaseShape.getString(buffer, 32);
				byte flags = buffer.get();
				byte iconId = buffer.get();
				byte unused = buffer.get();
				int unused2 = buffer.getInt();
				int fontHandle = buffer.getInt();
			} else if (typeId == 1) {
				font.name = BaseShape.getString(buffer, 32);
				byte flags = buffer.get();
				byte iconId = buffer.get();
				byte unused = buffer.get();
				byte unused_byte_2 = buffer.get();
				int unused2 = buffer.getInt();
				int height = buffer.getInt();
				int weight = buffer.getInt();
				byte charset = buffer.get();
				byte pitchAndFamily = buffer.get();
				float heightAdjustment = buffer.getFloat();
			}
			fontLength = buffer.getInt();
		}
		
		return result;
	}

	public Font getFontById(short fontId) {
		Font result = null;
		for (Font font : this.fonts) {
			if (font.id == fontId) {
				result = font;
				break;
			}
		}
		return result;
	}
	
}
