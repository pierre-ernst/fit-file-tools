package io.github.pierre_ernst.vdot;

public enum RaceDistances {
	M1500("1500m"), MILE1("1600m"), KM3("3km"), MILE2("3200m"), KM5("5km"), KM10("10km"), KM15("15km"), HALF("HM"),
	MARATHON("M");

	public final String csvHeader;

	private RaceDistances(String csvHeader) {
		this.csvHeader = csvHeader;
	}
}
