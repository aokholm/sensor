package com.vaavud.sensor.revolution;

public class RevSensorConfig {
	public double stationaryTimeout;
	public double maxTiltAngle;
	public double maxAcceleration;
	public double windspeedCoef0;
	public double windspeedCoef1;
	public long revSensorUpdateRateUs;
	
	public RevSensorConfig() {
		// set default values
		this.stationaryTimeout = 8d;
		this.maxTiltAngle = 30; // deg
		this.maxAcceleration = 0.5; // (m/s2)
		this.windspeedCoef0 = 0.1; // (m/s)
		this.windspeedCoef1 = 1.08; // (m/s)
		this.revSensorUpdateRateUs = 100000; // 0.1 s
	}
}
