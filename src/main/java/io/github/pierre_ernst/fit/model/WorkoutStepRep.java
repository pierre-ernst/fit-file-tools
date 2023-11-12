package io.github.pierre_ernst.fit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.garmin.fit.Mesg;
import com.garmin.fit.WktStepDuration;
import com.garmin.fit.WorkoutStepMesg;

public class WorkoutStepRep {

	private int repeats;
	private List<WorkoutStepMesg> steps;

	public WorkoutStepRep(WorkoutStepMesg wsm) {
		this(Collections.singletonList(wsm), 1);
	}

	public WorkoutStepRep(List<WorkoutStepMesg> steps, int repeats) {
		this.steps = steps;
		this.repeats = repeats;
	}

	public WorkoutStepRep(int repeats) {
		this.steps = new ArrayList<>();
		this.repeats = repeats;
	}

	public void add(WorkoutStepMesg wsm) {
		this.steps.add(wsm);
	}

	public int getRepeats() {
		return repeats;
	}

	public List<WorkoutStepMesg> getSteps() {
		return steps;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("WorkoutStepRep [repeats=").append(repeats).append(", steps={");
		for (WorkoutStepMesg wsm : steps) {
			sb.append(wsm.getMessageIndex()).append(") ").append(wsm.getNotes());
			sb.append(", ");
		}
		sb.append("}]");
		return sb.toString();
	}

	public static List<WorkoutStepRep> expand(List<Mesg> msgs) {
		List<WorkoutStepRep> reps = new ArrayList<>();

		for (Mesg msg : msgs) {
			WorkoutStepMesg wsm = (WorkoutStepMesg) msg;
			if (WktStepDuration.REPEAT_UNTIL_STEPS_CMPLT.equals(wsm.getDurationType())) {
				int count = wsm.getTargetValue().intValue();
				int from = wsm.getDurationValue().intValue();
				int to = wsm.getMessageIndex().intValue(); // from + count;

				WorkoutStepRep rep = new WorkoutStepRep(count);

				for (int s = from; s < to; s++) {
					rep.add((WorkoutStepMesg) msgs.get(s));
				}
				for (int c = (to - from); c > 0; c--) {
					reps.remove(reps.size() - 1);
				}
				reps.add(rep);
			} else {
				reps.add(new WorkoutStepRep(wsm));
			}
		}
		return reps;
	}
}
