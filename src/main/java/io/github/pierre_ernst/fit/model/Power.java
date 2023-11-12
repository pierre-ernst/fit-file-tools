package io.github.pierre_ernst.fit.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Power {

	/**
	 * Watts with 1000 offset
	 * 
	 * @param fitValue raw value from Fit file
	 * @return
	 */
	public static String format(long fitValue) {
		return String.format("%dW", convertFitValueToWatts(fitValue));
	}

	public static int parseWatts(String s) {
		Matcher matcher = Pattern.compile("\\s*(\\d+)\\s*[wW]").matcher(s);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group(1));
		}
		return -1;
	}

	public static long convertWattsToFitValue(int watts) {
		return watts + 1000l;
	}

	public static int convertFitValueToWatts(long fitValue) {
		return (int) (fitValue - 1000);
	}
}
