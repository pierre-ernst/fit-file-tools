package io.github.pierre_ernst.vdot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

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
import com.garmin.fit.DateTime;

import io.github.pierre_ernst.fit.format.Describer;
import io.github.pierre_ernst.fit.io.FitFileInput;
import io.github.pierre_ernst.fit.io.FitFileOutput;
import io.github.pierre_ernst.fit.model.ConfigCapacity;
import io.github.pierre_ernst.fit.model.FitData;
import io.github.pierre_ernst.fit.model.FitTimestamp;
import io.github.pierre_ernst.fit.model.Pace;

public class TrainingPlan {

	
	public static void main(String[] args) {
		try {
			VdotCapacity capacity = new VdotCapacity(42);
			FitData fitData = Describer.parse("Running\nE pace base run\n1km E", capacity);
						
			java.io.File out = java.io.File.createTempFile("fit", ".fit");
			FitFileOutput.write(out, fitData);
			System.out.println("New fit file has been created in " + out.getAbsolutePath());
		
	} catch (Exception ex) {
		ex.printStackTrace(System.err);
	}

	}

}
