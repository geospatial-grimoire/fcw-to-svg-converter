package org.bcholmes.cartographersvg.huffman;

import org.apache.commons.lang.ArrayUtils;

public class Huffman {

	Decode[] tree1 = Decode.initTree1();
	Decode[] tree2 = Decode.initTree2();

	int getBit(Huff h) {
		if (h.offset >= h.in.length) {
			return -1;
		} else {
			int res = h.in[h.offset] >> h.bit;
			res &= 0x01;

			h.bit++;
			if (h.bit >= 8) {
				h.bit = h.bit % 8;
				h.offset++;
			}

			return res;
		}
	}

	public Huff HuffDecodeBuffer(byte[] inbuf) {
		int[] M = new int[256];

//		int p1 = inbuf[0];
		int p2 = inbuf[1];

		M[0] = 7;
		for (int k = 1; k < 256; k++) {
			M[k] = M[k - 1] + (1 << (k - 1));
		}

		Huff huff = new Huff();
		huff.in = ArrayUtils.subarray(inbuf, 2, inbuf.length);

		huff.outMax = 4096;
		huff.out = new byte[huff.outMax];

		int encoded;

		while (((encoded = getBit(huff)) >= 0)) {
			if (encoded != 0) {
				int len;
				int l1 = HuffDecode(huff, tree1, 0);
				if (l1 <= 7) {
					len = l1 + 2;
				} else {
					int l2 = GetNBits(huff, l1 - 7);
					len = l2 + M[l1 - 7] + 2;
				}

				int d1 = HuffDecode(huff, tree2, 0);
				int d2;

				if (len == 2) {
					d1 <<= 2;
					d2 = GetNBits(huff, 2);
				} else {
					d1 <<= p2;
					d2 = GetNBits(huff, p2);
				}

				int dist = (d1 | d2) + 1;
				int j;

				if (huff.outOffset + len >= huff.outMax) {
					huff.outMax += 4096;
					byte[] temp = new byte[huff.outMax];
					System.arraycopy(huff.out, 0, temp, 0, huff.out.length);
					huff.out = temp;
					// assert(huff->out);
				}
				for (j = 0; j < len; j++)
					huff.out[huff.outOffset + j] = huff.out[huff.outOffset
							- dist + j];
				huff.outOffset += j;
			} else {
				if (huff.outOffset >= huff.outMax) {
					huff.outMax += 4096;
					byte[] temp = new byte[huff.outMax];
					System.arraycopy(huff.out, 0, temp, 0, huff.out.length);
					huff.out = temp;
					// assert(huff->out);
				}
				huff.out[huff.outOffset] = GetByte(huff);
				huff.outOffset++;
			}
		}

		return huff;
	}

	byte GetByte(Huff h) {
		byte res = (byte) (((getBit(h) << 0) | (getBit(h) << 1)
				| (getBit(h) << 2) | (getBit(h) << 3) | (getBit(h) << 4)
				| (getBit(h) << 5) | (getBit(h) << 6) | (getBit(h) << 7)));
		return res;
	}

	int GetNBits(Huff h, int n) {
		int res = 0;
		for (int i = 0; i < n; i++) {
			res |= getBit(h) << i;
		}
		return res;
	}

	int HuffDecode(Huff h, Decode[] d, int node) {
		if (d[node].branch == 0) {
			return d[node].value;
		} else {
			int bit = getBit(h);
			return HuffDecode(h, d, bit == 1 ? d[node].right : d[node].left);
		}
	}
}
