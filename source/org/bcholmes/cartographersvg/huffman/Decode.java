package org.bcholmes.cartographersvg.huffman;

public class Decode {

	int branch;
	int left;
	int right;
	int value;
	
	public Decode(int branch, int left, int right, int value) {
		this.branch = branch;
		this.left = left;
		this.right = right;
		this.value = value;
	}

	static Decode[] initTree1() {
		return new Decode[] {
			BRANCH_NODE(0, 1, 2),
			BRANCH_NODE(1, 3, 4),
			BRANCH_NODE(2, 5, 6),
			BRANCH_NODE(3, 7, 8),
			BRANCH_NODE(4, 9, 10),
			BRANCH_NODE(5, 11, 12),
			LEAF_NODE  (6, 1),
			BRANCH_NODE(7, 13, 14),
			BRANCH_NODE(8, 15, 16),
			BRANCH_NODE(9, 17, 18),
			LEAF_NODE  (10, 3),
			LEAF_NODE  (11, 2),
			LEAF_NODE  (12, 0),
			BRANCH_NODE(13, 19, 20),
			BRANCH_NODE(14, 21, 22),
			BRANCH_NODE(15, 23, 24),
			LEAF_NODE  (16, 6),
			LEAF_NODE  (17, 5),
			LEAF_NODE  (18, 4),
			BRANCH_NODE(19, 25, 26),
			BRANCH_NODE(20, 27, 28),
			LEAF_NODE  (21, 10),
			LEAF_NODE  (22, 9),
			LEAF_NODE  (23, 8),
			LEAF_NODE  (24, 7),
			BRANCH_NODE(25, 29, 30),
			LEAF_NODE  (26, 13),
			LEAF_NODE  (27, 12),
			LEAF_NODE  (28, 11),
			LEAF_NODE  (29, 15),
			LEAF_NODE  (30, 14)
		};
		
	}

	private static Decode LEAF_NODE(int a, int b) {
		return new Decode(0, 0, 0, b);
	}

	private static Decode BRANCH_NODE(int a, int b, int c) {
		return new Decode(1, b, c, 0);
	}

	public static Decode[] initTree2() {
		return new Decode[] {
				BRANCH_NODE(0, 1, 2),
				BRANCH_NODE(1, 3, 4),
				BRANCH_NODE(2, 5, 6),
				BRANCH_NODE(3, 7, 8),
				BRANCH_NODE(4, 9, 10),
				BRANCH_NODE(5, 11, 12),
				LEAF_NODE  (6, 0),
				BRANCH_NODE(7, 13, 14),
				BRANCH_NODE(8, 15, 16),
				BRANCH_NODE(9, 17, 18),
				BRANCH_NODE(10, 19, 20),
				BRANCH_NODE(11, 21, 22),
				BRANCH_NODE(12, 23, 24),
				BRANCH_NODE(13, 25, 26),
				BRANCH_NODE(14, 27, 28),
				BRANCH_NODE(15, 29, 30),
				BRANCH_NODE(16, 31, 32),
				BRANCH_NODE(17, 33, 34),
				BRANCH_NODE(18, 35, 36),
				BRANCH_NODE(19, 37, 38),
				BRANCH_NODE(20, 39, 40),
				BRANCH_NODE(21, 41, 42),
				BRANCH_NODE(22, 43, 44),
				LEAF_NODE  (23, 2),
				LEAF_NODE  (24, 1),
				BRANCH_NODE(25, 45, 46),
				BRANCH_NODE(26, 47, 48),
				BRANCH_NODE(27, 49, 50),
				BRANCH_NODE(28, 51, 52),
				BRANCH_NODE(29, 53, 54),
				BRANCH_NODE(30, 55, 56),
				BRANCH_NODE(31, 57, 58),
				BRANCH_NODE(32, 59, 60),
				BRANCH_NODE(33, 61, 62),
				BRANCH_NODE(34, 63, 64),
				BRANCH_NODE(35, 65, 66),
				BRANCH_NODE(36, 67, 68),
				BRANCH_NODE(37, 69, 70),
				BRANCH_NODE(38, 71, 72),
				BRANCH_NODE(39, 73, 74),
				BRANCH_NODE(40, 75, 76),
				LEAF_NODE  (41, 6),
				LEAF_NODE  (42, 5),
				LEAF_NODE  (43, 4),
				LEAF_NODE  (44, 3),
				BRANCH_NODE(45, 77, 78),
				BRANCH_NODE(46, 79, 80),
				BRANCH_NODE(47, 81, 82),
				BRANCH_NODE(48, 83, 84),
				BRANCH_NODE(49, 85, 86),
				BRANCH_NODE(50, 87, 88),
				BRANCH_NODE(51, 89, 90),
				BRANCH_NODE(52, 91, 92),
				BRANCH_NODE(53, 93, 94),
				BRANCH_NODE(54, 95, 96),
				BRANCH_NODE(55, 97, 98),
				BRANCH_NODE(56, 99, 100),
				BRANCH_NODE(57, 101, 102),
				BRANCH_NODE(58, 103, 104),
				BRANCH_NODE(59, 105, 106),
				BRANCH_NODE(60, 107, 108),
				BRANCH_NODE(61, 109, 110),
				LEAF_NODE  (62, 21),
				LEAF_NODE  (63, 20),
				LEAF_NODE  (64, 19),
				LEAF_NODE  (65, 18),
				LEAF_NODE  (66, 17),
				LEAF_NODE  (67, 16),
				LEAF_NODE  (68, 15),
				LEAF_NODE  (69, 14),
				LEAF_NODE  (70, 13),
				LEAF_NODE  (71, 12),
				LEAF_NODE  (72, 11),
				LEAF_NODE  (73, 10),
				LEAF_NODE  (74, 9),
				LEAF_NODE  (75, 8),
				LEAF_NODE  (76, 7),
				BRANCH_NODE(77, 111, 112),
				BRANCH_NODE(78, 113, 114),
				BRANCH_NODE(79, 115, 116),
				BRANCH_NODE(80, 117, 118),
				BRANCH_NODE(81, 119, 120),
				BRANCH_NODE(82, 121, 122),
				BRANCH_NODE(83, 123, 124),
				BRANCH_NODE(84, 125, 126),
				LEAF_NODE  (85, 47),
				LEAF_NODE  (86, 46),
				LEAF_NODE  (87, 45),
				LEAF_NODE  (88, 44),
				LEAF_NODE  (89, 43),
				LEAF_NODE  (90, 42),
				LEAF_NODE  (91, 41),
				LEAF_NODE  (92, 40),
				LEAF_NODE  (93, 39),
				LEAF_NODE  (94, 38),
				LEAF_NODE  (95, 37),
				LEAF_NODE  (96, 36),
				LEAF_NODE  (97, 35),
				LEAF_NODE  (98, 34),
				LEAF_NODE  (99, 33),
				LEAF_NODE  (100, 32),
				LEAF_NODE  (101, 31),
				LEAF_NODE  (102, 30),
				LEAF_NODE  (103, 29),
				LEAF_NODE  (104, 28),
				LEAF_NODE  (105, 27),
				LEAF_NODE  (106, 26),
				LEAF_NODE  (107, 25),
				LEAF_NODE  (108, 24),
				LEAF_NODE  (109, 23),
				LEAF_NODE  (110, 22),
				LEAF_NODE  (111, 63),
				LEAF_NODE  (112, 62),
				LEAF_NODE  (113, 61),
				LEAF_NODE  (114, 60),
				LEAF_NODE  (115, 59),
				LEAF_NODE  (116, 58),
				LEAF_NODE  (117, 57),
				LEAF_NODE  (118, 56),
				LEAF_NODE  (119, 55),
				LEAF_NODE  (120, 54),
				LEAF_NODE  (121, 53),
				LEAF_NODE  (122, 52),
				LEAF_NODE  (123, 51),
				LEAF_NODE  (124, 50),
				LEAF_NODE  (125, 49),
				LEAF_NODE  (126, 48)
		};
	}
	
}


