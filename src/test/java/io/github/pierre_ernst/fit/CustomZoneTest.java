package io.github.pierre_ernst.fit;

import com.garmin.fit.WktStepTarget;

import io.github.pierre_ernst.fit.model.CustomZone;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CustomZoneTest   extends TestCase{
	public CustomZoneTest(  )
    {
        super( CustomZoneTest.class.getSimpleName() );
    }

    public static Test suite()
    {
        return new TestSuite( CustomZoneTest.class );
    }

    public void testCustomTarget()
    {
    	long encodedPower = 1110;
    	String displayPower = "110W";
    	int power = 110;
    	
    	assertEquals(displayPower, CustomZone.format(WktStepTarget.POWER, encodedPower)); 
    	assertEquals(encodedPower, CustomZone.parse(WktStepTarget.POWER, power));
    	
    	long encodedSpeed = 3030;
    	String displaySpeed = "5:30";
    	
    	assertEquals(displaySpeed, CustomZone.format(WktStepTarget.SPEED, encodedSpeed)); 
    	assertEquals(encodedSpeed, CustomZone.parse(WktStepTarget.SPEED, displaySpeed));
    	
    	// TODO exception case tests
    	
    }
}
