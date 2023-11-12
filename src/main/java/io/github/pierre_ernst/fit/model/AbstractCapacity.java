package io.github.pierre_ernst.fit.model;

import java.util.Map;

import com.garmin.fit.Sport;

public abstract class AbstractCapacity {
	
	protected Map<String, Map<Sport, CustomZone>> zones;
		
	public CustomZone getCustomZone(String zoneKey, Sport sport) {
		if (zoneKey != null) {
			if (zones.containsKey(zoneKey.toLowerCase())) {
				Map<Sport, CustomZone> sportMap = zones.get(zoneKey.toLowerCase());
				if (sportMap.containsKey(sport)) {
					return sportMap.get(sport);
				}
			}
		}
		return null;
	}
}
