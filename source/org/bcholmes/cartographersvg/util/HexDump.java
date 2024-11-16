package org.bcholmes.cartographersvg.util;

import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;

public class HexDump {

	public static void write(byte[] b, int offset, int size) {
		byte[] temp = ArrayUtils.subarray(b, offset, offset + size);
		try {
			org.apache.commons.io.HexDump.dump(temp, 0, System.out, 0);
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (IllegalArgumentException e) {
		} catch (IOException e) {
		}
		System.out.println();
	}

}
