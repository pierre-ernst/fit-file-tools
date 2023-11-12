package io.github.pierre_ernst.fit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.github.pierre_ernst.fit.format.MrcComputrainer;
import io.github.pierre_ernst.fit.io.FitFileInput;
import io.github.pierre_ernst.fit.model.ConfigCapacity;
import io.github.pierre_ernst.fit.model.FitData;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ComputrainerTest  extends TestCase {
	public ComputrainerTest(  )
    {
        super( ComputrainerTest.class.getSimpleName() );
    }

    public static Test suite()
    {
        return new TestSuite( ComputrainerTest.class );
    }
    
    private FitData readFitData(String fileName) {
		InputStream fitIn = this.getClass().getClassLoader().getResourceAsStream( fileName+".fit");
		FitData data = FitFileInput.read(fitIn);		
		return data;
	}
    
    private String readTextFile(String fileName) {
    	BufferedReader   reader = new BufferedReader ( new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream( fileName)));
    	StringBuilder result = new StringBuilder();
    	try {
			for (String line; (line = reader.readLine()) != null; ) {
			     if (result.length() > 0) {
			         result.append('\n');
			     }
			     result.append(line);
			 }
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
		return result.toString();
	}
    
    private ConfigCapacity readCapacity() {
		InputStream configdIn = this.getClass().getClassLoader().getResourceAsStream("test-capacity.json");
		return ConfigCapacity.loadFromConfig(configdIn);	
	}
    
    public void testMrcFile()
    {
    	// Manually created on tredict.com with: 20min 65%, 3x(20s 200%, 4:40 55%), 3x(10s 220%, 4:50 55%), 10min 40% (FTP=200)
    	FitData input = readFitData("GaleBernhardtMiracleInterval1");
    	
    	String expected = readTextFile("GaleBernhardtMiracleInterval1.mrc");
    	   	
    	assertEquals(expected, new MrcComputrainer(readCapacity(), 200).format(input));   	
    }
}
