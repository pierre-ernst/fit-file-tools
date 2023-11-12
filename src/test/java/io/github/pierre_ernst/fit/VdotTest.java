package io.github.pierre_ernst.fit;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;

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

import io.github.pierre_ernst.fit.format.Describer;
import io.github.pierre_ernst.fit.io.FitFileInput;
import io.github.pierre_ernst.fit.model.FitData;
import io.github.pierre_ernst.fit.model.FitTimestamp;
import io.github.pierre_ernst.fit.model.Pace;
import io.github.pierre_ernst.vdot.VdotCapacity;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class VdotTest extends TestCase {

	public VdotTest() {
		super(VdotTest.class.getSimpleName());
	}

	public static Test suite() {
		return new TestSuite(VdotTest.class);
	}
	
	public void testParsedAgainstBuilt() {
			
		FitData fitDataBuilt = new FitData();
		
		FileIdMesg fileIdMesg = new FileIdMesg();			
		fileIdMesg.setManufacturer(Manufacturer.GARMIN);
		fileIdMesg.setGarminProduct(GarminProduct.FENIX6S);
		fileIdMesg.setType(File.WORKOUT);
		fileIdMesg.setTimeCreated(new DateTime(FitTimestamp.encode(Instant.now())));		
		fitDataBuilt.add(fileIdMesg);
		
		WorkoutMesg workoutMesg = new WorkoutMesg();
		workoutMesg.setSport(Sport.RUNNING);
		workoutMesg.setNumValidSteps(1);
		workoutMesg.setWktName("E pace base run");
		fitDataBuilt.add(workoutMesg);
		
		WorkoutStepMesg workoutStepMesg = new WorkoutStepMesg();
		workoutStepMesg.setMessageIndex(0);
		workoutStepMesg.setIntensity(Intensity.ACTIVE);
		workoutStepMesg.setDurationType(WktStepDuration.DISTANCE);
		workoutStepMesg.setDurationValue(1000*100l);  // 1000m
		workoutStepMesg.setTargetType(WktStepTarget.SPEED);
		workoutStepMesg.setCustomTargetCadenceLow(Pace.parseFitValue("5:50"));
		workoutStepMesg.setCustomTargetCadenceHigh(Pace.parseFitValue("5:30"));
		fitDataBuilt.add(workoutStepMesg);
		
		FitData fitDataParsed = Describer.parse("Running\nE pace base run\n1km E", new VdotCapacity(42));
		
		assertEquals(fitDataBuilt.getField("file_id", FileIdMesg.ManufacturerFieldNum).getLongValue(), fitDataParsed.getField("file_id", FileIdMesg.ManufacturerFieldNum).getLongValue());
		assertEquals(fitDataBuilt.getField("file_id", FileIdMesg.ProductFieldNum).getLongValue(), fitDataParsed.getField("file_id", FileIdMesg.ProductFieldNum).getLongValue());
		assertEquals(fitDataBuilt.getField("file_id", FileIdMesg.TypeFieldNum).getLongValue(), fitDataParsed.getField("file_id", FileIdMesg.TypeFieldNum).getLongValue());
		assertTrue(fitDataBuilt.getField("file_id", FileIdMesg.TimeCreatedFieldNum).getLongValue() <= fitDataParsed.getField("file_id", FileIdMesg.TimeCreatedFieldNum).getLongValue());

		assertEquals(fitDataBuilt.getField("workout", WorkoutMesg.SportFieldNum).getLongValue(), fitDataParsed.getField("workout", WorkoutMesg.SportFieldNum).getLongValue());
		assertEquals(fitDataBuilt.getField("workout", WorkoutMesg.NumValidStepsFieldNum).getLongValue(), fitDataParsed.getField("workout", WorkoutMesg.NumValidStepsFieldNum).getLongValue());
		assertEquals(fitDataBuilt.getField("workout", WorkoutMesg.WktNameFieldNum).getStringValue(), fitDataParsed.getField("workout", WorkoutMesg.WktNameFieldNum).getStringValue());

		assertEquals(fitDataBuilt.getField("workout_step", WorkoutStepMesg.MessageIndexFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.MessageIndexFieldNum).getLongValue());
		assertEquals(fitDataBuilt.getField("workout_step", WorkoutStepMesg.IntensityFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.IntensityFieldNum).getLongValue());	
		assertEquals(fitDataBuilt.getField("workout_step", WorkoutStepMesg.DurationTypeFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.DurationTypeFieldNum).getLongValue());
		assertEquals(fitDataBuilt.getField("workout_step", WorkoutStepMesg.DurationValueFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.DurationValueFieldNum).getLongValue());
		assertEquals(fitDataBuilt.getField("workout_step", WorkoutStepMesg.TargetTypeFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.TargetTypeFieldNum).getLongValue());
		assertEquals(fitDataBuilt.getField("workout_step", WorkoutStepMesg.CustomTargetValueLowFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.CustomTargetValueLowFieldNum).getLongValue());
		assertEquals(fitDataBuilt.getField("workout_step", WorkoutStepMesg.CustomTargetValueHighFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.CustomTargetValueHighFieldNum).getLongValue());
	}
	
	
	public void testParsedAgainstExistingFile() {
		
		FitData fitDataFile = FitFileInput.read(this.getClass().getClassLoader().getResourceAsStream( "tredict-1km E Pace.fit"));		
		
		FitData fitDataParsed = Describer.parse("Running\n1km E Pace\n1km E", new VdotCapacity(42));
		
		assertEquals(fitDataFile.getField("file_id", FileIdMesg.TypeFieldNum).getLongValue(), fitDataParsed.getField("file_id", FileIdMesg.TypeFieldNum).getLongValue());
		assertTrue(fitDataFile.getField("file_id", FileIdMesg.TimeCreatedFieldNum).getLongValue() <= fitDataParsed.getField("file_id", FileIdMesg.TimeCreatedFieldNum).getLongValue());

		assertEquals(fitDataFile.getField("workout", WorkoutMesg.SportFieldNum).getLongValue(), fitDataParsed.getField("workout", WorkoutMesg.SportFieldNum).getLongValue());
		assertEquals(fitDataFile.getField("workout", WorkoutMesg.NumValidStepsFieldNum).getLongValue(), fitDataParsed.getField("workout", WorkoutMesg.NumValidStepsFieldNum).getLongValue());
		assertEquals(fitDataFile.getField("workout", WorkoutMesg.WktNameFieldNum).getStringValue(), fitDataParsed.getField("workout", WorkoutMesg.WktNameFieldNum).getStringValue());

		assertEquals(fitDataFile.getField("workout_step", WorkoutStepMesg.MessageIndexFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.MessageIndexFieldNum).getLongValue());
		assertEquals(fitDataFile.getField("workout_step", WorkoutStepMesg.IntensityFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.IntensityFieldNum).getLongValue());	
		assertEquals(fitDataFile.getField("workout_step", WorkoutStepMesg.DurationTypeFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.DurationTypeFieldNum).getLongValue());
		assertEquals(fitDataFile.getField("workout_step", WorkoutStepMesg.DurationValueFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.DurationValueFieldNum).getLongValue());
		assertEquals(fitDataFile.getField("workout_step", WorkoutStepMesg.TargetTypeFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.TargetTypeFieldNum).getLongValue());
		assertEquals(fitDataFile.getField("workout_step", WorkoutStepMesg.CustomTargetValueLowFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.CustomTargetValueLowFieldNum).getLongValue());
		assertEquals(fitDataFile.getField("workout_step", WorkoutStepMesg.CustomTargetValueHighFieldNum).getLongValue(), fitDataParsed.getField("workout_step", WorkoutStepMesg.CustomTargetValueHighFieldNum).getLongValue());
	}
}
