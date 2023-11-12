package io.github.pierre_ernst.fit.model;

public class Pace {

	/**
	 * Convert m/s (scale:1000) to pace
	 * 
	 * @param fitValue raw value from Fit file
	 * @return
	 */
	public static String format(long fitValue) {
		float speedMs = (float) fitValue / 1000;
		double nbrSecondsToCover1K = 1000 / speedMs;
		int min = (int) (nbrSecondsToCover1K / 60);
		int sec = (int) (nbrSecondsToCover1K - min * 60);
		return String.format("%d:%02d", min, sec);
	}

	public static long parseFitValue(String pace) {
		double nbrSecondsToCover1K = parseSeconds(pace);
		if (nbrSecondsToCover1K > 0) {
			return convertSecondsToFitValue(nbrSecondsToCover1K);
		}
		return -1;
	}

	public static double parseSeconds(String pace) {
		String[] parts = pace.split(":");
		if (parts.length == 2) {
			return 60 * Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
		}
		return -1;
	}

	public static long convertSecondsToFitValue(double nbrSecondsToCover1K) {
		double speedMs = 1000 / nbrSecondsToCover1K;
		return (long) (speedMs * 1000);
	}
}
