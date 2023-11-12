package io.github.pierre_ernst.fit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Scanner;

import com.garmin.fit.Sport;
import com.garmin.fit.WktStepDuration;
import com.garmin.fit.WktStepTarget;
import com.garmin.fit.WorkoutMesg;
import com.garmin.fit.WorkoutStepMesg;

import io.github.pierre_ernst.fit.format.Describer;
import io.github.pierre_ernst.fit.io.FitFileInput;
import io.github.pierre_ernst.fit.io.FitFileOutput;
import io.github.pierre_ernst.fit.model.ConfigCapacity;
import io.github.pierre_ernst.fit.model.CustomZone;
import io.github.pierre_ernst.fit.model.FitData;
import io.github.pierre_ernst.fit.model.Pace;
import io.github.pierre_ernst.fit.model.Power;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DescriptionTest extends TestCase {
	public DescriptionTest() {
		super(DescriptionTest.class.getSimpleName());
	}

	public static Test suite() {
		return new TestSuite(DescriptionTest.class);
	}

	public void testFormatTime() {
		assertEquals("2h", Describer.formatDuration(Duration.ofHours(2)));
		assertEquals("2min", Describer.formatDuration(Duration.ofMinutes(2)));
		assertEquals("2:03", Describer.formatDuration(Duration.ofSeconds(3).plusMinutes(2)));
		assertEquals("1:02:03", Describer.formatDuration(Duration.ofSeconds(3).plusMinutes(2).plusHours(1)));
		assertEquals("1:00:03", Describer.formatDuration(Duration.ofSeconds(3).plusHours(1)));
	}
	
	public void testParseTime() {
		assertEquals(60*60, Describer.parseDuration("1h"));
		assertEquals(5*60, Describer.parseDuration("5min"));
		assertEquals(2*60+3, Describer.parseDuration("2:03"));
		assertEquals(60*60+2*60+3, Describer.parseDuration("1:02:03"));
		assertEquals(0, Describer.parseDuration("0:00"));
		assertTrue(Describer.parseDuration("Pierre Ernst")<0);
	}
	
	public void testFormatDistance() {
		assertEquals("900m", Describer.formatDistance(900));
		assertEquals("1km", Describer.formatDistance(1000));
		assertEquals("1.5km", Describer.formatDistance(1500));
	}
	
	public void testParseDistance() {
		assertEquals(900, Describer.parseDistance("900m"));
		assertEquals(1000, Describer.parseDistance("1000m"));
		assertEquals(1000, Describer.parseDistance("1km"));
		assertEquals(1000, Describer.parseDistance("1.0km"));
		assertEquals(1500, Describer.parseDistance("1.5km"));
		assertEquals(1609, Describer.parseDistance("1.60934km"));
		assertEquals(0, Describer.parseDistance("0.0km"));
		assertEquals(0, Describer.parseDistance("0m"));
		assertTrue(Describer.parseDistance("Pierre Ernst")<0);
	}
	
	/**
	 * gets expected description (captured from https://www.8020endurance.com/8020-workout-library/)
	 * @return
	 */
	private String readDescription(String fileName) {
		InputStream expectedIn = this.getClass().getClassLoader().getResourceAsStream(fileName+".txt");
		Scanner s = new Scanner(expectedIn).useDelimiter("\\A");
		String expected = s.hasNext() ? s.next() : "";
		return expected.replaceAll("\r\n", "\n");
	}
	
	private FitData readFitData(String fileName) {
		InputStream fitIn = this.getClass().getClassLoader().getResourceAsStream( fileName+".fit");
		FitData data = FitFileInput.read(fitIn);		
		return data;
	}
	
	private ConfigCapacity readCapacity() {
		InputStream configdIn = this.getClass().getClassLoader().getResourceAsStream("test-capacity.json");
		return ConfigCapacity.loadFromConfig(configdIn);	
	}
	
	public void testFormat() {
		// loads config file
		ConfigCapacity  capacity = readCapacity();
		 
		assertEquals(readDescription("RMI4"),  Describer.format(readFitData("RMI4"), capacity));
		assertEquals(readDescription("RL5"),  Describer.format(readFitData("RL5"), capacity));
	}
	
	public void testParseStep () {
		// loads config file
		ConfigCapacity  capacity = readCapacity();
		
		WorkoutStepMesg step = Describer.parseStep("5min 5:30", capacity, Sport.RUNNING);
		assertEquals(WktStepDuration.TIME, step.getDurationType());
		assertEquals(5*60f, step.getDurationTime());
		assertEquals((Long)(5*60l*1000),step.getField(WorkoutStepMesg.DurationValueFieldNum).getRawValue());
		assertEquals(WktStepTarget.SPEED, step.getTargetType());
		assertEquals(Pace.convertSecondsToFitValue(5*60+40), (long)step.getCustomTargetValueLow());
		assertEquals(Pace.convertSecondsToFitValue(5*60+20), (long)step.getCustomTargetValueHigh());
		
		
		// Expected values taken from creating a FIT file with tredict.com and exporting the values with FitFileViewer.com
		assertEquals((Long)2941l, (Long)step.getField(WorkoutStepMesg.CustomTargetValueLowFieldNum).getRawValue());
		assertEquals((Long)3125l, (Long)step.getField(WorkoutStepMesg.CustomTargetValueHighFieldNum).getRawValue());
		
		WorkoutStepMesg step2 = Describer.parseStep("1:30 Zone1", capacity, Sport.RUNNING);
		assertEquals(90f, step2.getDurationTime());
		assertEquals(WktStepTarget.SPEED, step2.getTargetType());
		assertEquals(Pace.convertSecondsToFitValue(7*60+36), (long)step2.getCustomTargetValueLow());
		assertEquals(Pace.convertSecondsToFitValue(6*60), (long)step2.getCustomTargetValueHigh());
		
		WorkoutStepMesg step3 = Describer.parseStep("5min PierreErnst", capacity, Sport.RUNNING);
		assertEquals(null, step3.getTargetType());
		assertEquals(null, step3.getCustomTargetValueLow());
		
		WorkoutStepMesg step4 = Describer.parseStep("10km 150w", capacity, Sport.CYCLING); 
		assertEquals(WktStepDuration.DISTANCE, step4.getDurationType());
		assertEquals(10*1000f, step4.getDurationDistance());
		assertEquals(10*1000*100l,(long)step4.getField(WorkoutStepMesg.DurationValueFieldNum).getRawValue());
		assertEquals(WktStepTarget.POWER, step4.getTargetType());
		assertEquals(Power.convertWattsToFitValue(140), (long)step4.getCustomTargetValueLow());
		assertEquals(Power.convertWattsToFitValue(160), (long)step4.getCustomTargetValueHigh());
		
		WorkoutStepMesg step5 = Describer.parseStep("10km Zone5", capacity, Sport.CYCLING); 
		assertEquals(WktStepTarget.POWER, step5.getTargetType());
		assertEquals(Power.convertWattsToFitValue(232), (long)step5.getCustomTargetValueLow());
		assertEquals(Power.convertWattsToFitValue(300), (long)step5.getCustomTargetValueHigh());
		
		WorkoutStepMesg step6 = Describer.parseStep("5min PierreErnst", capacity, Sport.CYCLING);
		assertEquals(WktStepDuration.TIME, step6.getDurationType());
		assertEquals(5*60f, step6.getDurationTime());
		assertEquals(null, step6.getTargetType());
		assertEquals(null, step6.getCustomTargetValueLow());
	}
	
	public void testParsePower() {
		assertEquals(200, Power.parseWatts(" 200 w "));	
		assertEquals(200, Power.parseWatts("200W"));
		assertTrue( Power.parseWatts("200 m") < 0 );
		assertTrue( Power.parseWatts("Pierre W") < 0 );
		assertTrue( Power.parseWatts("W 4") < 0 );
		assertTrue( Power.parseWatts("Pierre Ernst") < 0 );
	}
	
	private static long getTimeFromDistance(ConfigCapacity  capacity, String zoneName, int distanceMeters) {
		CustomZone z = capacity.getCustomZone(zoneName, Sport.RUNNING);
		float avgFitValue = (z.getMin() + z.getMax()) / 2;
		float speedMs = (float) avgFitValue / 1000;
		double nbrSecondsToCoverDistance = distanceMeters / speedMs;
		return (long)(nbrSecondsToCoverDistance*1000);
	}
	
	public void testParse() {	
				
		// loads config file
		ConfigCapacity  capacity = readCapacity();
				
		String workout = "200m Zone1, 1:30 Zone2";
		FitData fitData = Describer.parse("Running\nMy Workout\n"+workout, capacity);
		assertEquals(Sport.RUNNING, Sport.getByValue(fitData.getField("workout", WorkoutMesg.SportFieldNum).getShortValue()));
		assertEquals("My Workout", ((WorkoutMesg)fitData.get("workout").get(0)).getWktName());
		WorkoutStepMesg step0 = (WorkoutStepMesg)(fitData.get("workout_step").get(0));
		assertEquals(200*100, (long)step0.getDurationValue());		
		assertEquals(workout,Describer.format(fitData, capacity).split("\n")[2]	);
	
		String rmi4Description = readDescription("RMI4");	
		 FitData  rmi4Data = Describer.parse(rmi4Description,capacity);	
		 assertEquals(rmi4Description.split("\n")[2], Describer.format(rmi4Data,capacity).split("\n")[2]);
		
		 FitData twoLapse = Describer.parse("Running\nMy Workout\n400m Zone1, 400m Zone2", capacity);
		 assertEquals("800m", Describer.format(twoLapse,capacity).split("\n")[3].split(" / ")[0]);
		 
		 FitData twoMinute = Describer.parse("Running\nMy Workout\n1:00 Zone1, 60sec Zone2", capacity);
		 assertEquals("2min", Describer.format(twoMinute,capacity).split("\n")[3].split(" / ")[1]);
		  		 
		 assertEquals( Describer.formatDuration(Duration.ofMillis(
				 getTimeFromDistance(  capacity, "Zone1", 400)
				 + getTimeFromDistance(  capacity, "Zone2", 400)
				 )), Describer.format(twoLapse,capacity).split("\n")[3].split(" / ")[1]);
		 
		 /*
		 try {
		 File out = File.createTempFile("fit", ".fit");
		 FitFileOutput.write(out, rmi4Data);
			System.out.println("New fit file has been created in " + out.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			*/
			
		//assertEquals(readFitData("RMI4"), rmi4Data);   
	}
}
