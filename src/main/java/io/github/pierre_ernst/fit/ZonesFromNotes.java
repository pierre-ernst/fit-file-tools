package io.github.pierre_ernst.fit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;

import com.garmin.fit.Field;
import com.garmin.fit.FileCreatorMesg;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.GarminProduct;
import com.garmin.fit.Mesg;
import com.garmin.fit.WorkoutMesg;
import com.garmin.fit.WorkoutStepMesg;
import com.garmin.fit.util.DateTimeConverter;

import io.github.pierre_ernst.fit.format.Describer;
import io.github.pierre_ernst.fit.io.Downloader;
import io.github.pierre_ernst.fit.io.FitFileInput;
import io.github.pierre_ernst.fit.io.FitFileOutput;
import io.github.pierre_ernst.fit.model.ConfigCapacity;
import io.github.pierre_ernst.fit.model.CustomZone;
import io.github.pierre_ernst.fit.model.FitData;
import io.github.pierre_ernst.fit.model.FitField;
import io.github.pierre_ernst.fit.model.FitTimestamp;

import com.garmin.fit.Sport;
import org.jsoup.select.*;
import org.jsoup.nodes.*;

public class ZonesFromNotes {

	private static void editFitFile(ConfigCapacity capabilities, InputStream in, File out) throws IOException {
		FitData data = FitFileInput.read(in);
		String description = Describer.format(data, capabilities);
		String[] lines = description.split("\n");
		if (lines.length == 3) {
			// Change workout title
			String currentName = data.getField("workout", WorkoutMesg.WktNameFieldNum).getStringValue();
			String newName = currentName + " - " + lines[2] + " - " + lines[1];
			if (newName.length() > 255) {
				newName = newName.substring(0, 254 - 3) + "...";
			}
			data.getField("workout", WorkoutMesg.WktNameFieldNum).setValue(newName);
		}

		// Change Garmin Product
		data.getField("file_id", FileIdMesg.ProductFieldNum).setValue(GarminProduct.FENIX6S);

		// Refresh timestamp
		data.getField("file_id", FileIdMesg.TimeCreatedFieldNum).setValue(FitTimestamp.encode(Instant.now()));

		// Reset version numbers
		data.getField("file_creator", FileCreatorMesg.HardwareVersionFieldNum).setValue(0);
		data.getField("file_creator", FileCreatorMesg.SoftwareVersionFieldNum).setValue(0);

		Sport sport = Sport.getByValue(data.getField("workout", WorkoutMesg.SportFieldNum).getShortValue());
		int stepCount = data.getMesgList("workout_step").size();
		for (int i = 0; i < stepCount; i++) {
			String note = data.getField("workout_step", i, WorkoutStepMesg.NotesFieldNum).getStringValue();
			CustomZone customZone = capabilities.getCustomZone(note.toLowerCase(), sport);
			if (customZone != null) {
				data.addField("workout_step", i, WorkoutStepMesg.TargetTypeFieldNum, customZone.getType().getValue());
				data.addField("workout_step", i, WorkoutStepMesg.CustomTargetValueLowFieldNum, customZone.getMin());
				data.addField("workout_step", i, WorkoutStepMesg.CustomTargetValueHighFieldNum, customZone.getMax());
			}
		}

		FitFileOutput.write(out, data);
		in.close();
		System.out.println("New fit file has been created in " + out.getAbsolutePath());

	}

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Usage: " + ZonesFromNotes.class.getName() + " output-folder");
			System.exit(1);
		}

		try {

			Document doc = org.jsoup.Jsoup.connect("https://www.8020endurance.com/8020-workout-library/").get();
			Elements tables = doc.select(".tablepress > tbody");

			for (int j = 0; j < tables.size(); j++) {

				Elements rows = tables.get(j).select("tr");

				for (int i = 0; i < rows.size(); i++) {
					Element row = rows.get(i);
					Elements cols = row.select("td");
					Elements hrefs = cols.get(0).select("td > a");
					if (hrefs != null && hrefs.size() == 1) {
						File outputFile = new File(args[0], hrefs.get(0).text() + ".fit");
						editFitFile(ConfigCapacity.loadFromConfig(),
								Downloader
										.download(new URL("https://www.8020endurance.com" + hrefs.get(0).attr("href"))),
								outputFile);
					}
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}
}