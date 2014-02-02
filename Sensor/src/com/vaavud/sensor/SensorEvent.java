package com.vaavud.sensor;


public class SensorEvent {
	private final Sensor sensor;
	private final long timeUs;

	
	public SensorEvent(Sensor sensor, long timeUs) {
		this.sensor = sensor;
		this.timeUs = timeUs;
	}
	
	public Double getTime() {
	  return timeUs/1000000d;
	}
	
	public Sensor getSensor() {
        return sensor;
    }
	
	public long getTimeUs() {
        return timeUs;
    }
	
    @Override
    public String toString() {
        return "SensorEvent [sensor=" + sensor + ", timeUs=" + timeUs + "]";
    }
	
	
}