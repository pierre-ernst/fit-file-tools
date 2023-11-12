package io.github.pierre_ernst.fit.format;

import com.garmin.fit.WktStepTarget;
import com.garmin.fit.WorkoutStepMesg;

import io.github.pierre_ernst.fit.model.AbstractCapacity;
import io.github.pierre_ernst.fit.model.Power;

public class MrcComputrainer extends Computrainer {

	private int ftp;

	public MrcComputrainer(AbstractCapacity capacity, int ftp) {
		super(capacity);
		this.ftp = ftp;
	}

	@Override
	public String getUnit() {
		return "percent";
	}

	@Override
	public String getfileNameExtension() {
		return "mrc";
	}

	private int getPercentFromAbsolutePower(int power) {
		float ratio = power / (float) ftp;
		int percent = (int) (ratio * 100);
		return percent;
	}

	@Override
	public String getStepTarget(WorkoutStepMesg wsm) {
		if (WktStepTarget.POWER.equals(wsm.getTargetType())) {
			int median = ( Power.convertFitValueToWatts(wsm.getCustomTargetValueLow())
					+ Power.convertFitValueToWatts(wsm.getCustomTargetValueHigh())) / 2;
			return String.valueOf(getPercentFromAbsolutePower(median));
		}
		return null;
	}

}
