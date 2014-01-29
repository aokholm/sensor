package com.vaavud.sensor.revolution;

import com.vaavud.sensor.ProcessingSensor;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorListener;
import com.vaavud.sensor.SensorType;
import com.vaavud.sensor.internal.processor.magnetic.MagneticProcessor;
import com.vaavud.sensor.internal.processor.magnetic.MagneticProcessorRef;

public class RevolutionSensor extends ProcessingSensor implements SensorListener{
	
	private SensorListener listener;
	private MagneticProcessor magneticProcessor;
	private MagneticProcessorRef magneticProcessorRef;
	
	
	public RevolutionSensor(RevSensorConfig config) {
	    magneticProcessorRef = new MagneticProcessorRef(config.revSensorUpdateRateUs);
		magneticProcessor = new MagneticProcessor(config.revSensorUpdateRateUs);
	}
	
	@Override
	public void newEvent(SensorEvent event) {
		if (event.sensor == SensorType.TYPE_MAGNETIC_FIELD) {
			SensorEvent newEvent = (magneticProcessor).addMeasurement(event);
			SensorEvent newEventRef = (magneticProcessorRef).addMeasurement(event);
			
			if (newEvent != null) {
				listener.newEvent(newEvent);
			}
	        if (newEventRef != null) {
               listener.newEvent(newEventRef);
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
