package io.github.pierre_ernst.fit.format;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garmin.fit.DateTime;
import com.garmin.fit.File;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.GarminProduct;
import com.garmin.fit.Intensity;
import com.garmin.fit.Manufacturer;
import com.garmin.fit.Sport;
import com.garmin.fit.WktStepDuration;
import com.garmin.fit.WktStepTarget;
import com.garmin.fit.WorkoutMesg;
import com.garmin.fit.WorkoutStepMesg;

import io.github.pierre_ernst.fit.model.AbstractCapacity;
import io.github.pierre_ernst.fit.model.CustomZone;
import io.github.pierre_ernst.fit.model.FitData;
import io.github.pierre_ernst.fit.model.FitTimestamp;
import io.github.pierre_ernst.fit.model.Pace;
import io.github.pierre_ernst.fit.model.Power;
import io.github.pierre_ernst.fit.model.WorkoutStepRep;

public class Describer {

	public static String formatDuration(Duration d) {

		if (d.toHours() == 0 && d.toMinutesPart() == 0) {
			return d.toSecondsPart() + "s";
		}
		if (d.toHours() == 0 && d.toSecondsPart() == 0) {
			return d.toMinutesPart() + "min";
		}
		if (d.toHours() == 0) {
			return String.format("%d:%02d", d.toMinutesPart(), d.toSecondsPart());
		}
		if (d.toMinutesPart() == 0 && d.toSecondsPart() == 0) {
			return d.toHours() + "h";
		}
		return String.format("%d:%02d:%02d", d.toHours(), d.toMinutesPart(), d.toSecondsPart());
	}

	/**
	 * 
	 * @param duration
	 * @return number of seconds
	 */
	public static int parseDuration(String duration) {
		Matcher hourMinuteSecondMatcher = Pattern.compile("([0-9]+):([0-9]+):([0-9]+)").matcher(duration);
		if (hourMinuteSecondMatcher.find()) {
			return 60 * 60 * Integer.parseInt(hourMinuteSecondMatcher.group(1))
					+ 60 * Integer.parseInt(hourMinuteSecondMatcher.group(2))
					+ Integer.parseInt(hourMinuteSecondMatcher.group(3));
		}
		Matcher minuteSecondMatcher = Pattern.compile("([0-9]+):([0-9]+)").matcher(duration);
		if (minuteSecondMatcher.find()) {
			return 60 * Integer.parseInt(minuteSecondMatcher.group(1)) + Integer.parseInt(minuteSecondMatcher.group(2));
		}
		Matcher secondMatcher = Pattern.compile("([0-9]+)(?:s|sec)").matcher(duration);
		if (secondMatcher.find()) {
			return Integer.parseInt(secondMatcher.group(1));
		}
		Matcher minuteMatcher = Pattern.compile("([0-9]+)min").matcher(duration);
		if (minuteMatcher.find()) {
			return 60 * Integer.parseInt(minuteMatcher.group(1));
		}
		Matcher hourMatcher = Pattern.compile("([0-9]+)h").matcher(duration);
		if (hourMatcher.find()) {
			return 60 * 60 * Integer.parseInt(hourMatcher.group(1));
		}
		return -1;
	}

	public static String formatDistance(int durationValue) {
		float distanceM = durationValue;
		if (distanceM < 1000) {
			return String.format("%.0fm", distanceM);
		} else {
			float distanceKM = distanceM / 1000f;
			if (distanceKM % 1.0 != 0) {
				return String.format("%.1fkm", distanceKM);
			} else {
				return String.format("%.0fkm", distanceKM);
			}
		}
	}

	/**
	 * 
	 * @param distance
	 * @return number of meters
	 */
	public static int parseDistance(String distance) {
		Matcher kmMatcher = Pattern.compile("([0-9\\.]+)km").matcher(distance);
		if (kmMatcher.find()) {
			return (int) (1000 * Float.parseFloat(kmMatcher.group(1)));
		}
		Matcher mMatcher = Pattern.compile("([0-9]+)m").matcher(distance);
		if (mMatcher.find()) {
			return Integer.parseInt(mMatcher.group(1));
		}
		return -1;
	}

	public static String formatEnumdName(String s) {
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toUpperCase(s.charAt(0)));
		sb.append(s.substring(1).toLowerCase());
		return sb.toString();
	}

	public static String format(FitData data) {
		return format(data, null, true);
	}

	public static String format(FitData data, AbstractCapacity capacity) {
		return format(data, capacity, true);
	}

	public static String format(FitData data, AbstractCapacity capacity, boolean longForm) {
		StringBuffer sb = new StringBuffer();
		Sport sport = Sport.getByValue(data.getField("workout", WorkoutMesg.SportFieldNum).getShortValue());
		File fileType = File.getByValue(data.getField("file_id", FileIdMesg.TypeFieldNum).getShortValue());
		String name = data.getField("workout", WorkoutMesg.WktNameFieldNum).getStringValue();

		if (longForm) {
			sb.append(formatEnumdName(sport.toString())).append('\n');
			sb.append(name).append('\n');
		}

		if (File.WORKOUT.equals(fileType)) {
			int distance = 0;
			Duration duration = Duration.ofMillis(0);
			boolean firstRep = true;
			for (WorkoutStepRep rep : WorkoutStepRep.expand(data.getMesgList("workout_step"))) {
				if (!firstRep) {
					sb.append(", ");
				}
				firstRep = false;
				if (rep.getRepeats() > 1) {
					sb.append(rep.getRepeats()).append("x(");
				}
				boolean firstStep = true;
				for (WorkoutStepMesg wsm : rep.getSteps()) {
					if (!firstStep) {
						sb.append(" / ");
					}
					firstStep = false;

					if (WktStepDuration.TIME.equals(wsm.getDurationType())) {
						sb.append(formatDuration(Duration.ofMillis(wsm.getDurationValue())));
						duration = duration.plus(Duration.ofMillis(rep.getRepeats() * wsm.getDurationValue()));
						if ( (capacity != null) && (wsm.getNotes() != null) ) {
							CustomZone zone = capacity.getCustomZone(wsm.getNotes().toLowerCase(), sport);
							if (zone != null) {
								distance += rep.getRepeats() * wsm.getDurationTime() / 60 / 60 * zone.getAvgSpeed()
										* 1000;
							}
						}
					} else if (WktStepDuration.DISTANCE.equals(wsm.getDurationType())) {
						sb.append(formatDistance((int) (wsm.getDurationValue() / 100)));
						distance += rep.getRepeats() * (wsm.getDurationValue() / 100);
						if ( (capacity != null) && (wsm.getNotes() != null) ) {
							CustomZone zone = capacity.getCustomZone(wsm.getNotes().toLowerCase(), sport);
							if (zone != null) {
								long avgFitValue = (zone.getMin() + zone.getMax()) / 2;
								float speedMeterPerSeconds = (float) avgFitValue / 1000;
								float distanceMeter = wsm.getDurationValue() / 100;
								long nbrMilliSecondsToCoverDistance = (long) (distanceMeter / speedMeterPerSeconds
										* 1000);
								duration = duration
										.plus(Duration.ofMillis(rep.getRepeats() * nbrMilliSecondsToCoverDistance));
							}
						}
					}
					if (wsm.getNotes() != null) {
						sb.append(' ').append(wsm.getNotes());
					} /*
						 * else if (Sport.RUNNING.equals(sport) &&
						 * WktStepTarget.SPEED.equals(wsm.getTargetType())) { sb.append('
						 * ').append(Pace.format((long)(wsm.getCustomTargetSpeedLow()*1000))); }
						 */
				}
				if (rep.getRepeats() > 1) {
					sb.append(')');
				}
			}
			if (longForm) {
				sb.append('\n').append(formatDistance(distance)).append(" / ").append(formatDuration(duration));
			}
		} else {
			sb.append(formatEnumdName(fileType.toString()));
		}
		return sb.toString();
	}

	public static WorkoutStepMesg parseStep(String step, AbstractCapacity capacity, Sport sport) {
		WorkoutStepMesg workoutStepMesg = new WorkoutStepMesg();
		workoutStepMesg.setIntensity(Intensity.ACTIVE);

		String[] items = step.split("\\s+");
		if (items.length > 1) {
			int duration = parseDuration(items[0]);
			if (duration > 0) {
				workoutStepMesg.setDurationType(WktStepDuration.TIME);
				workoutStepMesg.setDurationValue(duration * 1000l);
			} else {
				int distance = parseDistance(items[0]);
				if (distance > 0) {
					workoutStepMesg.setDurationType(WktStepDuration.DISTANCE);
					workoutStepMesg.setDurationValue(distance * 100l);
				}
			}
			workoutStepMesg.setNotes(items[1]);

			if (Sport.RUNNING.equals(sport)) {
				double nbrSecondsToCover1K = Pace.parseSeconds(items[1]);
				if (nbrSecondsToCover1K > 0) {
					workoutStepMesg.setTargetType(WktStepTarget.SPEED);
					workoutStepMesg.setCustomTargetCadenceLow(Pace.convertSecondsToFitValue(nbrSecondsToCover1K + 10));
					workoutStepMesg.setCustomTargetCadenceHigh(Pace.convertSecondsToFitValue(nbrSecondsToCover1K - 10));
				} else {
					CustomZone customZone = capacity.getCustomZone(items[1], Sport.RUNNING);
					if (customZone != null) {
						workoutStepMesg.setTargetType(customZone.getType());
						workoutStepMesg.setCustomTargetCadenceLow(customZone.getMin());
						workoutStepMesg.setCustomTargetCadenceHigh(customZone.getMax());
					}
				}
			} else if (Sport.CYCLING.equals(sport)) {
				int watts = Power.parseWatts(items[1]);
				if (watts > 0) {
					workoutStepMesg.setTargetType(WktStepTarget.POWER);
					workoutStepMesg.setCustomTargetPowerLow(Power.convertWattsToFitValue(watts - 10));
					workoutStepMesg.setCustomTargetPowerHigh(Power.convertWattsToFitValue(watts + 10));
				} else {
					CustomZone customZone = capacity.getCustomZone(items[1], Sport.CYCLING);
					if (customZone != null) {
						workoutStepMesg.setTargetType(customZone.getType());
						workoutStepMesg.setCustomTargetPowerLow(customZone.getMin());
						workoutStepMesg.setCustomTargetPowerHigh(customZone.getMax());
					}
				}
			}
		}
		return workoutStepMesg;
	}

	public static FitData parse(String description, AbstractCapacity capacity) {
		FitData fitData = new FitData();

		FileIdMesg fileIdMesg = new FileIdMesg();
		fileIdMesg.setTimeCreated(new DateTime(FitTimestamp.encode(Instant.now())));
		fileIdMesg.setManufacturer(Manufacturer.GARMIN);
		fileIdMesg.setGarminProduct(GarminProduct.FENIX6S);
		fileIdMesg.setType(File.WORKOUT);
		fitData.add(fileIdMesg);

		String[] lines = description.split("\n");
		if (lines.length > 2) {
			Sport sport = Sport.valueOf(lines[0].trim().toUpperCase());
			String title = lines[1].trim();
			WorkoutMesg workoutMesg = new WorkoutMesg();
			workoutMesg.setSport(sport);
			workoutMesg.setWktName(title);
			fitData.add(workoutMesg);

			int index = 0;
			for (String step : lines[2].split(",")) {
				step = step.trim();
				Matcher repMatcher = Pattern.compile("(\\d+)\\s*[xX*]\\s*\\(([^\\)]+)\\)").matcher(step);
				if (repMatcher.matches()) {
					String rep = repMatcher.group(2).trim();
					int repCount = Integer.parseInt(repMatcher.group(1));
					String[] repSteps = rep.split("/");
					for (String repStep : repSteps) {
						WorkoutStepMesg workoutStepMesg = parseStep(repStep.trim(), capacity, sport);
						workoutStepMesg.setMessageIndex(index++);
						fitData.add(workoutStepMesg);
					}
					WorkoutStepMesg repeat = new WorkoutStepMesg();
					repeat.setDurationType(WktStepDuration.REPEAT_UNTIL_STEPS_CMPLT);
					repeat.setTargetValue((long) repCount);
					repeat.setDurationValue((long) (index - repSteps.length));
					repeat.setMessageIndex(index++);
					fitData.add(repeat);
				} else {
					WorkoutStepMesg workoutStepMesg = parseStep(step, capacity, sport);
					workoutStepMesg.setMessageIndex(index++);
					fitData.add(workoutStepMesg);
				}
			}
			workoutMesg.setNumValidSteps(index);
		}
		return fitData;
	}

	public static FitData parse(String description) {
		return parse(description, null);
	}
}
