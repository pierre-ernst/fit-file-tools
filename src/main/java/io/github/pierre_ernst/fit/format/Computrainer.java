package io.github.pierre_ernst.fit.format;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.garmin.fit.Field;
import com.garmin.fit.File;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.Sport;
import com.garmin.fit.WorkoutMesg;
import com.garmin.fit.WorkoutStepMesg;

import io.github.pierre_ernst.fit.model.AbstractCapacity;
import io.github.pierre_ernst.fit.model.FitData;
import io.github.pierre_ernst.fit.model.WorkoutStepRep;

public abstract class Computrainer {

	private static class Message {
		private int timeStampSecond;
		private String text;
		private int durationSeconds;

		public Message(int timestampSecond, String text, int durationSeconds) {
			this.timeStampSecond = timestampSecond;
			this.text = text;
			this.durationSeconds = durationSeconds;
		}
	}

	private static final String FIELD_SEPARATOR = "\t";
	private static final String LINE_SEPARATOR = "\n";

	protected AbstractCapacity capacity;
	private List<Message> messages;

	protected Computrainer(AbstractCapacity capacity) {
		this.capacity = capacity;
		this.messages = new ArrayList<>();
	}

	public abstract String getUnit();

	public abstract String getfileNameExtension();

	public abstract String getStepTarget(WorkoutStepMesg wsm);

	private void appendLine(StringBuffer sb, float timeStamp, String target) {
		String formatted = String.format("%.2f", timeStamp);
		if (formatted.endsWith(".00")) {
			formatted = formatted.substring(0, formatted.indexOf('.'));
		}
		sb.append(formatted).append(FIELD_SEPARATOR).append(target).append(LINE_SEPARATOR);
	}

	public String format(FitData data) {
		return format(data, null);
	}

	public String format(FitData data, String description) {
		StringBuffer sb = new StringBuffer();

		Field name = data.getField("workout", WorkoutMesg.WktNameFieldNum);
		Sport sport = Sport.getByValue(data.getField("workout", WorkoutMesg.SportFieldNum).getShortValue());
		File fileType = File.getByValue(data.getField("file_id", FileIdMesg.TypeFieldNum).getShortValue());

		if (Sport.CYCLING.equals(sport) && File.WORKOUT.equals(fileType)) {
			sb.append("[COURSE HEADER]").append(LINE_SEPARATOR);
			sb.append("VERSION = 2").append(LINE_SEPARATOR);
			sb.append("UNITS = ENGLISH").append(LINE_SEPARATOR);
			sb.append("DESCRIPTION = ")
					.append((description != null) ? description : Describer.format(data, capacity, false))
					.append(LINE_SEPARATOR);
			sb.append("FILE NAME = ").append((name == null) ? "file" : name.getStringValue()).append('.')
					.append(getfileNameExtension().toLowerCase()).append(LINE_SEPARATOR);
			sb.append("MINUTES ").append(getUnit().toUpperCase()).append(LINE_SEPARATOR);
			sb.append("[END COURSE HEADER]").append(LINE_SEPARATOR);
			sb.append("[COURSE DATA]").append(LINE_SEPARATOR);

			float timeStampMinutes = 0f;
			for (WorkoutStepRep rep : WorkoutStepRep.expand(data.getMesgList("workout_step"))) {
				for (int repeat = 0; repeat < rep.getRepeats(); repeat++) {
					for (WorkoutStepMesg wsm : rep.getSteps()) {
						float duration = wsm.getDurationTime() / 60f;
						String target = getStepTarget(wsm);

						appendLine(sb, timeStampMinutes, target);

						if (wsm.getNotes() != null && !wsm.getNotes().trim().isEmpty()) {
							messages.add(new Message((int) (timeStampMinutes * 60f),
									Describer.formatDuration(Duration.ofMillis(wsm.getDurationValue())) + ": "
											+ wsm.getNotes().trim()
											+ ((rep.getRepeats() > 1)
													? " (" + (repeat + 1) + " of " + rep.getRepeats() + ")"
													: ""),
									wsm.getDurationTime().intValue() - 1));
						}

						timeStampMinutes += duration;
						appendLine(sb, timeStampMinutes, target);
					}
				}
			}

			sb.append("[END COURSE DATA]").append(LINE_SEPARATOR);

			if (!messages.isEmpty()) {
				sb.append("[COURSE TEXT]").append(LINE_SEPARATOR);
				for (Message m : messages) {
					sb.append(m.timeStampSecond).append(FIELD_SEPARATOR).append(m.text).append(FIELD_SEPARATOR)
							.append(m.durationSeconds).append(LINE_SEPARATOR);
				}
				sb.append("[END COURSE TEXT]").append(LINE_SEPARATOR);
			}
		}

		return sb.toString();
	}
}
