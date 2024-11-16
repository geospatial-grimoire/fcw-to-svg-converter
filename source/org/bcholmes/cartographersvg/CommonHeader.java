package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bcholmes.cartographersvg.util.HexDump;

class CommonHeader {

	private int size;
	private byte type;
	private byte flags;
	private byte flags2;
	private int color;
	private int color2;
	private byte thick;
	private short plane;
	private short layer;
	private short lineStyle;
	private short groupId;
	short fillStyle;
	private float width;
	int tag;
	ByteBuffer rest;

	private CommonHeader() {
	}

	static CommonHeader fromBytes(byte[] b, int offset, int versionNumber) {
		CommonHeader result = new CommonHeader();
		result.size = ByteToIntegerUtil.toInteger(b, offset);
		byte[] temp = ArrayUtils.subarray(b, offset + 4, offset + result.size);
		if (temp.length > 0) {
			ByteBuffer buffer = ByteBuffer.wrap(temp);
			buffer.order(ByteOrder.LITTLE_ENDIAN);

			switch (versionNumber) {

			case 21:
				parseVersion21(result, buffer);
				break;
			case 24:
			default:
				parseVersion24(result, buffer);
			}
			
			if (result.type == 4) {
				HexDump.write(b, offset, result.size);
			}
		}
		return result;
	}

	// BCH: some detail still needs to be determined
	private static void parseVersion21(CommonHeader result, ByteBuffer buffer) {
		result.type = buffer.get();
		if (result.size > 5) {
			result.flags = buffer.get(); // 5
			result.flags2 = buffer.get(); // 6
			result.color = buffer.get() & 0xff; // 7
			result.color2 = buffer.get() & 0xff; // 8
			result.thick = buffer.get(); // 9
			result.plane = buffer.getShort(); // 10-11
			result.layer = buffer.getShort(); // 12-13
			result.lineStyle = buffer.getShort(); // 14-15
			result.groupId = buffer.getShort(); // 16-17
			result.fillStyle = buffer.getShort(); // 18-19
			if (result.size >= 24) {
				result.width = buffer.getFloat(); // 20-23
//				if (result.size >= 28) {
//					result.tag = buffer.getInt(); // 24-27
//				}
			}

			result.rest = buffer;
		}
	}

	private static void parseVersion24(CommonHeader result, ByteBuffer buffer) {
		result.type = buffer.get();
		if (result.size > 5) {
			result.flags = (result.size > 5) ? buffer.get() : (byte) 0; // 5
			result.flags2 = buffer.get(); // 6
			result.color = buffer.get() & 0xff; // 7
			result.color2 = buffer.get() & 0xff; // 8
			result.thick = buffer.get(); // 9
			result.plane = buffer.getShort(); // 10-11
			result.layer = buffer.getShort(); // 12-13
			result.lineStyle = (result.size > 15) ? buffer.getShort() : 0; // 14-15
			result.groupId = (result.size > 17) ? buffer.getShort() : 0; // 16-17
			result.fillStyle = (result.size > 19) ? buffer.getShort() : 0; // 18-19
			result.width = (result.size > 23) ? buffer.getFloat() : 0.0f; // 20-23
			result.tag = (result.size > 27) ? buffer.getInt() : 0; // 24-27
			
			result.rest = buffer;
		}
	}
	
	public boolean isFilled() {
		if (isTrivialFilledLine()) {
			return false;
		} else {
			return this.fillStyle != 0;
		}
	}

	public int getSize() {
		return this.size;
	}

	public byte getType() {
		return this.type;
	}
	public String getStrokeColor() {
		return "#" + StringUtils.leftPad(Integer.toHexString(Color.DEFAULT_PALETTE[this.color]), 6, '0');
	}
	public String getFillColor() {
		return isCurrentColor() 
				? "currentColor" 
				: "#" + StringUtils.leftPad(Integer.toHexString(Color.DEFAULT_PALETTE[this.color2]), 6, '0');
	}
	public float getPenThickness() {
		return (this.thick & 0xff) * 0.25f;
	}
	public float getStrokeWidth() {
		return this.width;
	}
	
	public boolean isOutlinePresent() {
		return (this.flags2 & 0x01) == 0;
	}
	
	public float getLineDistance() {
		return this.width;
	}
	
	public boolean isTrivialFilledLine() {
		return isFilledLine() && StringUtils.equals(getStrokeColor(), getFillColor());
	}

	public boolean isFilledLine() {
		return this.fillStyle != 0 && this.width > 0;
	}

	public boolean isCurrentColor() {
		return (this.flags & 0x08) != 0;
	}
	public int getTag() {
		return this.tag;
	}

	public short getLayer() {
		return this.layer;
	}
}