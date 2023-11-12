package io.github.pierre_ernst.fit.FIRST;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garmin.fit.WorkoutMesg;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import io.github.pierre_ernst.fit.format.Describer;
import io.github.pierre_ernst.fit.io.FitFileOutput;
import io.github.pierre_ernst.fit.model.ConfigCapacity;
import io.github.pierre_ernst.fit.model.FitData;

public class TrainingPlanGenerator {

	private static final String WARMUP = "10min RP";
	private static final String COOLDOWN = "10min RP";

	private File outputFolder;
	private ConfigCapacity capacity;
	private File planCsv;
	private int nbrOfweeks;

	public TrainingPlanGenerator(File planCsv, int nbrOfweeks, File outputFolder) {

		this.outputFolder = outputFolder;
		this.planCsv = planCsv;
		this.nbrOfweeks = nbrOfweeks;
		this.capacity = ConfigCapacity
				.loadFromConfig(this.getClass().getClassLoader().getResourceAsStream("FIRST.json"));
	}

	private void build() throws CsvValidationException, IOException {
		Reader reader = new InputStreamReader(new FileInputStream(planCsv));
		CSVReader csvReader = new CSVReader(reader);
		String[] nextRow;
		List<String> headers = null;

		while ((nextRow = csvReader.readNext()) != null) {
			if (headers == null) {
				// Parse first line
				System.out.print("week,");
				headers = new ArrayList<>();
				for (String header : nextRow) {
					Matcher headerMatcher = Pattern.compile("KEY RUN #(\\d+) \\(([A-Z ]+)\\)").matcher(header);
					if (headerMatcher.find()) {
						System.out.print("\"" + header + "\",");
						int index = Integer.parseInt(headerMatcher.group(1));
						String key = headerMatcher.group(2).trim().toLowerCase().replace(' ', '-');
						headers.add(index - 1, key);
					}
				}
				System.out.println("\"distance (km)\",duration");
			} else {
				// Parse rest of the file
				int week = nbrOfweeks - Integer.parseInt(nextRow[0]) + 1;
				System.out.print(week + ",");
				int distance = 0;
				Duration duration = Duration.ofMillis(0);
				for (int i = 1; i < nextRow.length; i++) {
					if (!nextRow[i].trim().isEmpty()) {
						String workoutName = "w" + week + "-" + headers.get(i - 1);
						FitData fitData = generateWorkout(workoutName, nextRow[i]);
						String description = fitData.addDescription(capacity);

						String[] totals = description.split("\n")[3].split(" / ");
						int currentDistanceInMeters = Describer.parseDistance(totals[0]);
						distance += currentDistanceInMeters;
						int currentDurationInSeconds = Describer.parseDuration(totals[1]);
						duration = duration.plus(Duration.ofSeconds(currentDurationInSeconds));

						workoutName = workoutName + "-" + Math.round((float) (currentDistanceInMeters / 1000f)) + "km-"
								+ Math.round((float) (currentDurationInSeconds / 60f)) + "min";
						System.out.print(workoutName);
						// Update with the new workout name
						fitData.addField("workout", WorkoutMesg.WktNameFieldNum, workoutName);

						File output = new File(outputFolder, workoutName + ".fit");
						FitFileOutput.write(output, fitData);

					}
					System.out.print(",");
				}
				System.out.println(
						String.format("%.2f", (float) (distance / 1000f)) + ",=TIME(0;0;" + duration.toSeconds() + ")");

			}
		}
		csvReader.close();
	}

	private FitData generateWorkout(String name, String description) {
		FitData fitData = Describer.parse("Running\n" + name + "\n" + WARMUP + ", " + description + ", " + COOLDOWN,
				capacity);
		return fitData;
	}

	public static void main(String... args) {
		try {
			if (args.length != 2) {
				System.err.println("Usage: java " + TrainingPlanGenerator.class.getName() + " plan.csv output-folder");
				System.exit(1);
			}

			File planCsv = new File(args[0]);
			if (!planCsv.isFile() || !planCsv.canRead()) {
				System.err.println(planCsv.getAbsolutePath() + " is not readable.");
			}

			File outputFolder = new File(args[1]);
			if (!outputFolder.isDirectory()) {
				System.err.println(outputFolder.getAbsolutePath() + " is not a folder.");
			}

			new TrainingPlanGenerator(planCsv, 12, outputFolder).build();

		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
}
