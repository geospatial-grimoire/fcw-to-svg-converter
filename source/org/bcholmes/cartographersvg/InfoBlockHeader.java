package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;

public class InfoBlockHeader extends InfoBlock {

	private short creator;
	private int allocLength;
	private byte headerVersion;
	private byte version;
	private short dwgFlags;
	private short fstyle;
	private short color;
	private short layer;
	private short lstyle;
	private short wplane;
	private short dstyle;
	private short bcolor;
	private short gridId;
	private Point3d low;
	private Point3d hi;

	public InfoBlockHeader(int length, int etype, int itype) {
		super(length, etype, itype);
	}

	public static InfoBlockHeader from(int length, int etype, int itype, ByteBuffer buffer) {
		
		InfoBlockHeader result = new InfoBlockHeader(length, etype, itype);
		buffer.getInt();
		buffer.get();
		buffer.get();
		
		result.creator = buffer.getShort();
		result.allocLength = buffer.getInt();
		result.headerVersion = buffer.get();
		result.version = buffer.get();
		result.dwgFlags = buffer.getShort();
		result.fstyle = buffer.getShort();
		result.color = buffer.getShort();
		result.layer = buffer.getShort();
		result.lstyle = buffer.getShort();
		result.wplane = buffer.getShort();
		result.dstyle = buffer.getShort();
		result.bcolor = buffer.getShort();
		result.gridId = buffer.getShort();
		result.low = Point3d.from(buffer);
		result.hi = Point3d.from(buffer);
		
		return result;
	}

	public Point3d getLow() {
		return this.low;
	}

	public Point3d getHi() {
		return this.hi;
	}
	
}
