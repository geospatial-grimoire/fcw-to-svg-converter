package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class InfoBlock {

	private int length;
	private int etype;
	private int itype;
	private final byte[] contents;

	protected InfoBlock(int length, int etype, int itype) {
		this(length, etype, itype, null);
	}
	protected InfoBlock(int length, int etype, int itype, byte[] contents) {
		this.length = length;
		this.etype = etype;
		this.itype = itype;
		this.contents = contents;
	}

	public InfoBlockType getIType() {
		return (itype < InfoBlockType.values().length) ? InfoBlockType.values()[this.itype] : null;
	}
	
	private static ByteBuffer toByteBuffer(byte[] contents) {
		ByteBuffer buffer = ByteBuffer.wrap(contents);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer;
	}
	
	public static InfoBlock from(int length, int etype, int itype, byte[] contents) {
		if (itype < InfoBlockType.values().length) {
			switch (InfoBlockType.values()[itype]) {
			case HEADER:
				return InfoBlockHeader.from(length, etype, itype, toByteBuffer(contents));
			case LINE_STYLES:
				return InfoBlockLineStyles.from(length, etype, itype, toByteBuffer(contents));
			case FILL_STYLES:
				return InfoBlockFillDetails.from(length, etype, itype, toByteBuffer(contents));
			case LAYERS:
				return InfoBlockLayers.from(length, etype, itype, toByteBuffer(contents));
			case FONTS:
				return InfoBlockFonts.from(length, etype, itype, toByteBuffer(contents));
			default:
				return new InfoBlock(length, etype, itype, contents);
			}
		} else {
			return new InfoBlock(length, etype, itype);
		}
	}
}
