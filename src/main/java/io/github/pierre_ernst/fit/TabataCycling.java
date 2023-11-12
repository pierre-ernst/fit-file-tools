package io.github.pierre_ernst.fit;

import io.github.pierre_ernst.fit.format.Describer;
import io.github.pierre_ernst.fit.format.ErgComputrainer;
import io.github.pierre_ernst.fit.model.FitData;

public class TabataCycling {

	private int vdot;
	private int weight;
	private int ftp;

	private TabataCycling(int vdot, int weight, int ftp) {
		this.vdot = vdot;
		this.weight = weight;
		this.ftp = ftp;
	}

	private int calculateVdotPower() {
		// https://fellrnr.com/wiki/Cycling_HIIT_For_Runners#Estimating_Power_At_V.CC.87O2max_From_Running_Performance
		return (int) (((float) (vdot - 7) / 10.791f) * weight);
	}

	private String generateWorkout() {
		int hiitPower = (int) (1.7f * calculateVdotPower() / 10) * 10;
		int warmupPower = (int) (0.6f * ftp / 10) * 10;
		return "15min " + warmupPower + "W, 8x(20s " + hiitPower + "W / 10s " + warmupPower + "W), 15min " + warmupPower
				+ "W";
	}

	private String toErg() {
		String description = generateWorkout();
		FitData fitData = Describer.parse("Cycling\n" + "tabata-" + vdot + "-" + weight + "\n" + description);

		return new ErgComputrainer().format(fitData, "(vdot:" + vdot + ", weight:" + weight + "kg) " + description);
	}

	public static void main(String... args) {
		try {
			System.out.println(new TabataCycling(46, 67, 210).toErg());
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}

}
