package io.github.pierre_ernst.fit.model;

import java.time.Instant;

public class FitTimestamp {
	 private static final long FIT_EPOCH_MS = 631065600000L;
	 
	 public static Instant decode(long timestamp) {
		 return  Instant.ofEpochMilli(timestamp * 1000 + FIT_EPOCH_MS);
	 }
	 
	 public static long encode(Instant i) {
		return i.minusMillis(FIT_EPOCH_MS).getEpochSecond();	 
	 }
}
