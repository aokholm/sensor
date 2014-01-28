package com.vaavud.sensor;

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
	
	public Double getTime() {
	  return timeUs/1000000d;
	}
	
	public Double getX() {
	  return values[0];
	}
	
	public Double getY() {
      return values[1];
    }
	
	public Double getZ() {
      return values[2];
    }
	
	@Override
	public String toString() {
		return "RevSensorEvent [sensor=" + sensor + ", timeUs=" + timeUs
				+ ", values=" + Arrays.toString(values) + "]";
	}
}