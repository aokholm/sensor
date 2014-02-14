package com.vaavud.sensor.revolution;

import com.vaavud.sensor.ProcessingSensor;
import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorEvent3D;
import com.vaavud.sensor.SensorListener;
import com.vaavud.sensor.internal.processor.magnetic.MagneticProcessor;

public class RevolutionSensor extends ProcessingSensor implements SensorListener{
	
	private MagneticProcessor magneticProcessor;
	
	public RevolutionSensor(RevSensorConfig config) {
		magneticProcessor = new MagneticProcessor(config);
	}
	
	public RevolutionSensor() {
	    this(new RevSensorConfig());
	}
	
	@Override
	public void newEvent(SensorEvent event) {
		if (event.getSensor().getType() == Sensor.Type.MAGNETIC_FIELD) {
			magneticProcessor.addMeasurement((SensorEvent3D) event);
		}
		// TODO implement more sensor types...
	}

	@Override
	public void setReciever(SensorListener listener) {
		magneticProcessor.setListener(listener);
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
	public Sensor.Type[] getSensorTypes() {
	    Sensor.Type[] sensorTypes = new Sensor.Type[]{
	            Sensor.Type.ACCELEROMETER,
	            Sensor.Type.GYROSCOPE,
	            Sensor.Type.MAGNETIC_FIELD};
		
		return sensorTypes;
	}

	@Override
	public SensorListener getReciever() {
		return this;
	}
	
}
