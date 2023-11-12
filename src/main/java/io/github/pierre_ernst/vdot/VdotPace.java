package io.github.pierre_ernst.vdot;

public enum VdotPace {

	E("Easy Km"),
	M("M Km"),
	T400("T 400m"),
	T1000("T Km"),
	T1609(""),
	I400(""),
	I1000(""),
	I1200(""),
	I1609(""),
	R200(""),
	R400(""),
	R800("")
	;
	
	public final String csvHeader;

	private VdotPace(String csvHeader) {
		this.csvHeader = csvHeader;
	}
}
