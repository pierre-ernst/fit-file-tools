package io.github.pierre_ernst.vdot;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.garmin.fit.Sport;
import com.garmin.fit.WktStepTarget;

import io.github.pierre_ernst.fit.model.AbstractCapacity;
import io.github.pierre_ernst.fit.model.CustomZone;
import io.github.pierre_ernst.fit.model.Pace;

public class VdotCapacity extends AbstractCapacity {

	public static class VdotZone extends CustomZone {

		public VdotZone(String pace) {
			super(WktStepTarget.SPEED, Pace.convertSecondsToFitValue(Pace.parseSeconds(pace) + 10),
					Pace.convertSecondsToFitValue(Pace.parseSeconds(pace) - 10));
		}
	}

	public VdotCapacity(int vdot) {
		// TODO populate custom zones according to VDOT tables

		zones = new TreeMap<>();
		Map<Sport, CustomZone> runMap = new HashMap<>();
		runMap.put(Sport.RUNNING, new VdotZone("5:40"));
		zones.put(VdotPace.E.name().toLowerCase(), runMap);
	}

	public CustomZone getCustomZone(VdotPace pace) {
		return super.getCustomZone(pace.name().toLowerCase(), Sport.RUNNING);
	}
}
