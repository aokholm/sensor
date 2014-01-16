package com.vaavud.sensor;

public abstract class BaseSensor{
	
	abstract public void setReciever(SensorListener listener);

	abstract public void start();
	
	abstract public void stop();
	
}
