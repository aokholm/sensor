package com.vaavud.ars;

import java.util.Arrays;

public final class SensorEvent {
	public final SensorType sensor;
	public final long timeUs;
	public final double values[];
	
	public SensorEvent(SensorType sensor, long timeUs, double[] values) {
		this.sensor = sensor;
		this.timeUs = timeUs;
		this.values = values;
	}

	@Override
	public String toString() {
		return "RevSensorEvent [sensor=" + sensor + ", timeUs=" + timeUs
				+ ", values=" + Arrays.toString(values) + "]";
	}
}