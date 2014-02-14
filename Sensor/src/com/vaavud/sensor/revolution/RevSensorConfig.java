package com.vaavud.sensor.revolution;

public class RevSensorConfig {
    
//	public double stationaryTimeout;
//	public double maxTiltAngle;
//	public double maxAcceleration;
//	public double windspeedCoef0;
//	public double windspeedCoef1;
	private int revSensorRateUs;
	private Integer movAvg;
	
	
	public RevSensorConfig() {
		// set default values
//		this.stationaryTimeout = 8d;
//		this.maxTiltAngle = 30; // deg
//		this.maxAcceleration = 0.5; // (m/s2)
//		this.windspeedCoef0 = 0.1; // (m/s)
//		this.windspeedCoef1 = 1.08; // (m/s)
		this.revSensorRateUs = 100_000; // 0.1 s
		this.movAvg = null;
	}
	
	public int getRevSensorRateUs() {
        return revSensorRateUs;
    }
	
	public void setRevSensorRateUs(int revSensorRateUs) {
        this.revSensorRateUs = revSensorRateUs;
    }
	
	public Integer getMovAvg() {
        return movAvg;
    }
	
	public void setMovAvg(Integer movAvg) {
        this.movAvg = movAvg;
    }
}
