package org.bcholmes.cartographersvg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.lang.ArrayUtils;

public class FileId {

	char[] progId;
	char[] verText;
	char[] verTextS;
	byte[] dosChars;
	byte dbVer;
	boolean compressed;
	byte[] filler;
	byte endFileID;

	public FileId(byte[] buffer) throws IOException {
		CharsetDecoder ascii = Charset.forName("ISO-8859-1").newDecoder();
        progId = ascii.decode(ByteBuffer.wrap(ArrayUtils.subarray(buffer, 0, 26))).array();
        verText = ascii.decode(ByteBuffer.wrap(ArrayUtils.subarray(buffer, 26, 4))).array();
        verTextS = ascii.decode(ByteBuffer.wrap(ArrayUtils.subarray(buffer, 30, 14))).array();
        dosChars = ArrayUtils.subarray(buffer, 44, 3);
        dbVer = buffer[47];
        compressed = buffer[48] == 0 ? false : true;
        filler = ArrayUtils.subarray(buffer, 49, 78);
        endFileID = buffer[127];
	}

	public boolean isCompressed() {
		return this.compressed;
	}

	public int getVersionNumber() {
		return this.dbVer & 0xff;
	}
	
}
