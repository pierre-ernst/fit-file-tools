package io.github.pierre_ernst.fit.io;

import java.io.File;

import com.garmin.fit.FileEncoder;
import com.garmin.fit.Fit.ProtocolVersion;

import io.github.pierre_ernst.fit.model.FitData;

public class FitFileOutput {

	public static void write(File file, FitData data) {
		FileEncoder fileEncoder = new FileEncoder(file, ProtocolVersion.V2_0);
		for (String header : data.keySet()) {
			fileEncoder.write(data.get(header));
		}
		fileEncoder.close();
	}
}
