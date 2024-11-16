package org.bcholmes.cartographersvg;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtil {

	public static DecimalFormat format = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));

	
	public static String formatFloat(float f) {
		return format.format(f);
	}
}
