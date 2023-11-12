package io.github.pierre_ernst.fit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.github.pierre_ernst.fit.format.Describer;
import io.github.pierre_ernst.fit.io.FitFileInput;
import io.github.pierre_ernst.fit.model.ConfigCapacity;
import io.github.pierre_ernst.fit.model.FitData;

public class Describe {

	public static void main(String[] args) {
		try {
			if (args.length!=1) {
				System.err.println("Usage: "+Describe.class.getName()+" file.fit");
				System.exit(1);
			}
			FileInputStream in = new FileInputStream(new File(args[0]));
			FitData data = FitFileInput.read(in);
			System.out.println(Describer.format(data, ConfigCapacity.loadFromConfig()));
			
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		} 
	}

}
