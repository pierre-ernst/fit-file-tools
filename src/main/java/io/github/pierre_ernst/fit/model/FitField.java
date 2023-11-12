package io.github.pierre_ernst.fit.model;

import java.util.HashMap;
import java.util.Map;

import com.garmin.fit.Field;
import com.garmin.fit.Mesg;
import com.garmin.fit.Profile;
import com.garmin.fit.WorkoutStepMesg;

import io.github.pierre_ernst.fit.format.Describer;

public class FitField {

	public static final Map<String, Map<Integer, Field>> allFields = new HashMap<>();

	static {
		Map<Integer, Field> map = new HashMap<>();
		try {
			java.lang.reflect.Field javaField = WorkoutStepMesg.class.getDeclaredField("workoutStepMesg");
			javaField.setAccessible(true);
			Mesg workoutStepMesg = (Mesg) javaField.get(null);
			allFields.put(workoutStepMesg.getName(), map);
			for (Field f : workoutStepMesg.getFields()) {
				map.put(f.getNum(), f);
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}

	public static Object getValue(Field field) {
		if (field.getProfileType().equals(Profile.Type.DATE_TIME)) {
			return FitTimestamp.decode(field.getLongValue());
		}
		if (field.getValue() instanceof Number) {
			String enumName = Profile.enumValueName(field.getProfileType(), field.getLongValue());
			// revert back to number in case enumValueName() didn't find an enum
			try {
				Long.parseLong(enumName);
				return field.getLongValue();
			} catch (NumberFormatException ex) {
				return Describer.formatEnumdName(enumName);
			}

		}
		return field.getValue();
	}
}
