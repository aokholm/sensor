package com.vaavud.sensor;

public abstract class ProcessingSensor extends BaseSensor {

	@Override
	public abstract void setReciever(SensorListener listener);

	@Override
	public abstract void start();

	@Override
	public abstract void stop();
	
	public abstract SensorListener getReciever();
	
	public abstract Sensor.Type[] getSensorTypes();
	

	
}
