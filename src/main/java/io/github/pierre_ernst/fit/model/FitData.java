package io.github.pierre_ernst.fit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.garmin.fit.Field;
import com.garmin.fit.Mesg;
import com.garmin.fit.WorkoutStepMesg;

import io.github.pierre_ernst.fit.format.Describer;

public class FitData extends TreeMap<String, List<Mesg>> {

	private static final long serialVersionUID = -129959739095749962L;

	public void add(Mesg msg) {
		if (!containsKey(msg.getName())) {
			put(msg.getName(), new ArrayList<>());
		}
		get(msg.getName()).add(msg);
	}

	public Field getField(String section, int fieldNum) {
		return getField(section, 0, fieldNum);
	}

	public Field getField(String section, int messageIndex, int fieldNum) {
		Mesg msg = getMesg(section, messageIndex);
		if (msg != null) {
			return msg.getField(fieldNum);
		}
		return null;
	}

	public void addField(String section, int fieldNum, Object value) {
		addField(section, 0, fieldNum, value);
	}

	public void addField(String section, int messageIndex, int fieldNum, Object value) {
		Mesg msg = getMesg(section, messageIndex);
		if (msg != null) {
			Field f = msg.getField(fieldNum);
			if (f == null) {
				f = new Field(FitField.allFields.get(section).get(fieldNum));
				msg.addField(f);
			}
			f.setValue(value);
		}
	}

	public Mesg getMesg(String section, int messageIndex) {
		if (containsKey(section)) {
			List<Mesg> msgs = get(section);
			if (messageIndex >= 0 && messageIndex < msgs.size()) {
				return msgs.get(messageIndex);
			}
		}
		return null;
	}

	public List<Mesg> getMesgList(String section) {
		return get(section);
	}

	/**
	 * Adds a short description of the workout as a message on the first step.
	 * Over-write the first step message if it was already set.
	 * 
	 * @param capacity
	 * @return the full description
	 */
	public String addDescription(AbstractCapacity capacity) {
		String description = Describer.format(this, capacity);
		String shortDescription = description.split("\n")[2];

		addField("workout_step", 0, WorkoutStepMesg.NotesFieldNum, shortDescription);

		return description;
	}
}
