package io.github.pierre_ernst.fit.format;

import com.garmin.fit.WktStepTarget;
import com.garmin.fit.WorkoutStepMesg;

import io.github.pierre_ernst.fit.model.AbstractCapacity;
import io.github.pierre_ernst.fit.model.Power;

public class ErgComputrainer extends Computrainer {

	public ErgComputrainer() {
		super(null);
	}

	public ErgComputrainer(AbstractCapacity capacity) {
		super(capacity);
	}

	@Override
	public String getUnit() {
		return "Watts";
	}

	@Override
	public String getfileNameExtension() {
		return "erg";
	}

	@Override
	public String getStepTarget(WorkoutStepMesg wsm) {
		if (WktStepTarget.POWER.equals(wsm.getTargetType())) {
			int median = (Power.convertFitValueToWatts(wsm.getCustomTargetValueLow())
					+ Power.convertFitValueToWatts(wsm.getCustomTargetValueHigh())) / 2;
			return String.valueOf(median);

		}
		return null;
	}

}
