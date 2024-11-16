package org.bcholmes.cartographersvg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bcholmes.cartographersvg.huffman.Huff;
import org.bcholmes.cartographersvg.huffman.Huffman;

public class Explode {

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.out.println("Please specify the file name you want to convert");
		} else {
			decodeFile(new File(args[0]), 0);
		}

	}

	static void decodeFile(File file, int dump) throws IOException {
		byte[] b = toByteArray(file);
		FileId header = new FileId(b);
		if (header.getVersionNumber() == 24) {
			if (header.isCompressed()) {
				System.out.println("Uncompressing the input file.");

				b = ArrayUtils.subarray(b, 128, b.length);
				Huff huff = new Huffman().HuffDecodeBuffer(b);

				byte[] decoded = ArrayUtils.subarray(huff.out, 0, huff.outOffset);
				Fcw.decodeBuffer(file, header, decoded);
			} else {
				b = ArrayUtils.subarray(b, 128, b.length);
				Fcw.decodeBuffer(file, header, b);
			}
		} else {
			System.out.println("Unsupported version number : " + header.getVersionNumber());
		}
	}

	static byte[] toByteArray(File file) throws IOException {
		InputStream input = new FileInputStream(file);
		try {
			return IOUtils.toByteArray(input);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}
