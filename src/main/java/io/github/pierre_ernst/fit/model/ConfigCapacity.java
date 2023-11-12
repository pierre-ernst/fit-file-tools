package io.github.pierre_ernst.fit.model;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.json.JSONObject;

import com.garmin.fit.Sport;
import com.garmin.fit.WktStepTarget;

public class ConfigCapacity extends AbstractCapacity {

	private ConfigCapacity() {

	}

	public static ConfigCapacity loadFromConfig() {
		InputStream in = ConfigCapacity.class.getClassLoader().getResourceAsStream("endurance80-20.json");
		return loadFromConfig(in);
	}

	public static ConfigCapacity loadFromConfig(InputStream in) {
		Scanner s = new Scanner(in).useDelimiter("\\A");
		String json = s.hasNext() ? s.next() : "";
		return loadFromConfig(json);
	}

	public static ConfigCapacity loadFromConfig(String json) {
		ConfigCapacity capabilities = new ConfigCapacity();
		JSONObject capabilitiesJson = new JSONObject(json);
		JSONObject zones = capabilitiesJson.getJSONObject("zones");
		capabilities.zones = new TreeMap<>();
		for (Iterator<String> zoneIterator = zones.keys(); zoneIterator.hasNext();) {
			String zoneName = zoneIterator.next();
			JSONObject zoneValue = zones.getJSONObject(zoneName);
			Map<Sport, CustomZone> sportMap = new HashMap<>();
			for (Iterator<String> sportIterator = zoneValue.keys(); sportIterator.hasNext();) {
				String sportName = sportIterator.next();
				JSONObject sportValue = zoneValue.getJSONObject(sportName);
				Sport sport = Sport.valueOf(sportName.toUpperCase());
				capabilities.zones.put(zoneName.toLowerCase(), sportMap);
				WktStepTarget customTargetType = WktStepTarget.valueOf(sportValue.getString("type").toUpperCase());
				Float avgSpeed = null;
				if (sportValue.has("avgSpeed")) {
					avgSpeed = sportValue.getFloat("avgSpeed");
				}

				long min, max;
				min = max = -1;
				if (sportValue.has("min") && sportValue.has("max")) {
					min = CustomZone.parse(customTargetType, sportValue.get("min"));
					max = CustomZone.parse(customTargetType, sportValue.get("max"));
				} else {
					if (WktStepTarget.SPEED == customTargetType) {
						double nbrSecondsToCover1K = Pace.parseSeconds((String) sportValue.get("val"));
						min = Pace.convertSecondsToFitValue(nbrSecondsToCover1K - 10);
						max = Pace.convertSecondsToFitValue(nbrSecondsToCover1K + 10);
					}
					if (WktStepTarget.POWER == customTargetType) {
						int watts = (int) sportValue.get("val");
						min = Power.convertWattsToFitValue(watts - 10);
						max = Power.convertWattsToFitValue(watts + 10);
					}
				}

				CustomZone customZone = new CustomZone(customTargetType, min, max, avgSpeed);
				sportMap.put(sport, customZone);
			}
		}
		return capabilities;
	}
}
