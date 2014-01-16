package com.vaavud.ars.internal.revolution;

import com.vaavud.ars.ProcessingSensor;
import com.vaavud.ars.SensorType;
import com.vaavud.ars.SensorConfig;
import com.vaavud.ars.SensorEvent;
import com.vaavud.ars.SensorListener;
import com.vaavud.ars.internal.processor.magnetic.MagneticProcessor;

public class RevolutionSensor extends ProcessingSensor implements SensorListener{
	
	private SensorListener listener;
	private MagneticProcessor magneticProcessor;
	
	
	
	public RevolutionSensor(SensorConfig config) {
		magneticProcessor = new MagneticProcessor(config.revSensorUpdateRateUs);
	}
	
	@Override
	public void newEvent(SensorEvent event) {
		
		if (event.sensor == SensorType.TYPE_MAGNETIC_FIELD) {
			
			SensorEvent newEvent = magneticProcessor.addMeasurement(event);
			
			if (newEvent != null) {
				listener.newEvent(newEvent);
			}
		}
		
		// Should implement more sensor types...
	}

	@Override
	public void setReciever(SensorListener listener) {
		this.listener = listener;
		
	}

	@Override
	public void start() {
		// do nothing
	}

	@Override
	public void stop() {
		// do nothing
	}

	@Override
	public SensorType[] getSensorTypes() {
		SensorType[] sensorTypes = new SensorType[]{
				SensorType.TYPE_ACCELEROMETER,
				SensorType.TYPE_GYROSCOPE,
				SensorType.TYPE_MAGNETIC_FIELD};
		
		return sensorTypes;
	}

	@Override
	public SensorListener getReciever() {
		return this;
	}
	
}
