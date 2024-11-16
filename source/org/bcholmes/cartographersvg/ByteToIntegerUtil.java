package org.bcholmes.cartographersvg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang.ArrayUtils;

public class ByteToIntegerUtil {
	
	public static int toInteger(byte[] b, int offset) {
		byte[] arr = ArrayUtils.subarray(b, offset, offset + 4);
		
		//  create a byte buffer and wrap the array
		ByteBuffer bb = ByteBuffer.wrap(arr);

		//  if the file uses little endian as apposed to network
		//  (big endian, Java's native) format,
		//  then set the byte order of the ByteBuffer
//		if(use_little_endian)
		    bb.order(ByteOrder.LITTLE_ENDIAN);

		//  read your integers using ByteBuffer's getInt().
		//  four bytes converted into an integer!
		int result = bb.getInt();
		return result;
	}

}
