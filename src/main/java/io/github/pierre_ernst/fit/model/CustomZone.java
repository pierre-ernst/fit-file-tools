package io.github.pierre_ernst.fit.model;

import java.util.Objects;

import com.garmin.fit.WktStepTarget;

public class CustomZone {

	private WktStepTarget type;
	private long min;
	private long max;
	private Float avgSpeed;

	public CustomZone(WktStepTarget type, long min, long max) {
		this(type, min, max, null);
	}

	public CustomZone(WktStepTarget type, long min, long max, Float avgSpeed) {
		super();
		this.type = type;
		this.min = min;
		this.max = max;
		this.avgSpeed = avgSpeed;

		if ((this.avgSpeed == null) && (this.type.equals(WktStepTarget.SPEED))) {
			// calculate average speed based on pace
			long median = (min + max) / 2;
			float speedMs = (float) median / 1000;
			this.avgSpeed = speedMs * 60 * 60 / 1000;
		}
	}

	public WktStepTarget getType() {
		return type;
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	/**
	 * Average Speed in km/h
	 * 
	 * @return
	 */
	public Float getAvgSpeed() {
		return avgSpeed;
	}

	public static String format(WktStepTarget targetType, long value) {

		if (WktStepTarget.SPEED == targetType) {
			return Pace.format(value);
		} else if (WktStepTarget.POWER == targetType) {
			return Power.format(value);
		}
		return null;
	}

	public String format(long value) {
		return format(this.type, value);
	}

	public static long parse(WktStepTarget targetType, Object value) {

		if (WktStepTarget.SPEED == targetType) {

			if (value instanceof String) {
				return Pace.parseFitValue((String) value);
			} else {
				throw new IllegalArgumentException(
						"Value " + value + " has an unsupported data type (" + value.getClass().getName()
								+ ") for custom target type: " + WktStepTarget.getStringFromValue(targetType));
			}

		} else if (WktStepTarget.POWER == targetType) {

			if (value instanceof Integer) {
				return Power.convertWattsToFitValue((int) value);
			} else {
				throw new IllegalArgumentException(
						"Value " + value + " has an unsupported data type (" + value.getClass().getName()
								+ ") for custom target type: " + WktStepTarget.getStringFromValue(targetType));
			}

		}
		throw new IllegalArgumentException("Unsupported target tyype: " + WktStepTarget.getStringFromValue(targetType));
	}

	@Override
	public String toString() {
		return "CustomZone [type=" + WktStepTarget.getStringFromValue(type).toLowerCase() + ", " + format(min) + " - "
				+ format(max) + ", " + avgSpeed + "km/h ]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(max, min, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomZone other = (CustomZone) obj;
		return max == other.max && min == other.min && type == other.type;
	}
}
