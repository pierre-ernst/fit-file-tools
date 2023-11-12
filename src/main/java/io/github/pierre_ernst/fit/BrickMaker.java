package io.github.pierre_ernst.fit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import com.garmin.fit.FileCreatorMesg;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.GarminProduct;
import com.garmin.fit.Intensity;
import com.garmin.fit.Mesg;
import com.garmin.fit.Sport;
import com.garmin.fit.WktStepDuration;
import com.garmin.fit.WorkoutMesg;
import com.garmin.fit.WorkoutStepMesg;

import io.github.pierre_ernst.fit.format.Describer;
import io.github.pierre_ernst.fit.io.FitFileInput;
import io.github.pierre_ernst.fit.io.FitFileOutput;
import io.github.pierre_ernst.fit.model.FitData;
import io.github.pierre_ernst.fit.model.FitTimestamp;

public class BrickMaker {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: " + BrickMaker.class.getName() + " file1.fit file2.fit");
			System.exit(1);
		}

		try {
			FitData[] input = new FitData[args.length];
			for (int i = 0; i < args.length; i++) {
				FileInputStream in = new FileInputStream(new File(args[i]));
				input[i] = FitFileInput.read(in);
			}

			FitData combined = new FitData();

			// Copy the sections from the 1st input
			for (String section : input[0].keySet()) {
				combined.put(section, new ArrayList<>(input[0].get(section)));
			}

			// Merge the workout names
			String name0 = input[0].getField("workout", WorkoutMesg.WktNameFieldNum).getStringValue();
			String name1 = input[1].getField("workout", WorkoutMesg.WktNameFieldNum).getStringValue();
			combined.getField("workout", WorkoutMesg.WktNameFieldNum).setValue(name0 + " + " + name1);

			// Merge number of steps
			int stepCount0 = input[0].getField("workout", WorkoutMesg.NumValidStepsFieldNum).getIntegerValue();
			int stepCount1 = input[1].getField("workout", WorkoutMesg.NumValidStepsFieldNum).getIntegerValue();
			combined.getField("workout", WorkoutMesg.NumValidStepsFieldNum).setValue(stepCount0 + stepCount1 + 1);

			// Change Garmin Product
			combined.getField("file_id", FileIdMesg.ProductFieldNum).setValue(GarminProduct.FENIX6S);

			// Refresh timestamp
			combined.getField("file_id", FileIdMesg.TimeCreatedFieldNum).setValue(FitTimestamp.encode(Instant.now()));

			// Reset version numbers
			combined.getField("file_creator", FileCreatorMesg.HardwareVersionFieldNum).setValue(0);
			combined.getField("file_creator", FileCreatorMesg.SoftwareVersionFieldNum).setValue(0);

			// Add transition step
			WorkoutStepMesg transition = new WorkoutStepMesg();
			int messageIdx = stepCount0;
			transition.setMessageIndex(messageIdx);
			transition.setDurationType(WktStepDuration.OPEN);
			transition.setIntensity(Intensity.OTHER);
			transition.setNotes("Transition");
			combined.get("workout_step").add(transition);
			messageIdx++;

			// Add steps from file2
			for (Mesg msg : input[1].get("workout_step")) {
				WorkoutStepMesg wsm = (WorkoutStepMesg) msg;
				wsm.setMessageIndex(messageIdx);
				combined.get("workout_step").add(wsm);
				messageIdx++;
			}

			// Prepend sport to step notes
			String[] sports = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				sports[i] = Describer.formatEnumdName(
						Sport.getByValue(input[i].getField("workout", WorkoutMesg.SportFieldNum).getShortValue())
								.toString());
			}
			for (Mesg msg : combined.get("workout_step")) {
				WorkoutStepMesg wsm = (WorkoutStepMesg) msg;
				int sportIdx = 0;
				if (wsm.getMessageIndex() > stepCount0) {
					sportIdx++;
				}
				if (wsm.getNotes() == null) {
					wsm.setNotes(sports[sportIdx]);
				} else if (!"Transition".equals(wsm.getNotes())) {
					wsm.setNotes(sports[sportIdx] + " " + wsm.getNotes());
				}
			}

			// Change Sport
			combined.getField("workout", WorkoutMesg.SportFieldNum).setValue(Sport.GENERIC);

			File out = File.createTempFile("fit", ".fit");
			FitFileOutput.write(out, combined);
			System.out.println("New fit file has been created in " + out.getAbsolutePath());

		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

}
