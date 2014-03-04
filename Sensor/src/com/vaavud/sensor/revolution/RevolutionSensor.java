package com.vaavud.sensor.revolution;

import com.vaavud.sensor.ProcessingSensor;
import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorListener;
import com.vaavud.sensor.internal.processor.magnetic.MagneticProcessorRev1;
import com.vaavud.sensor.internal.processor.magnetic.MagneticProcessorRev2;
import com.vaavud.sensor.internal.processor.magnetic.MagneticProcessorWindTunnel;

public class RevolutionSensor extends ProcessingSensor implements SensorListener{
	
	private MagneticProcessorRev2 magneticProcessorTest;
	private MagneticProcessorRev1 magneticProcessor;
	private MagneticProcessorWindTunnel magneticProcessorWindTunnel;
	private SensorListener listener;
	private RevSensorConfig config;
	
	
	public RevolutionSensor(RevSensorConfig config) {
	    this.config = config;

	}
	
	public RevolutionSensor() {
	    this(new RevSensorConfig());
	}
	
	@Override
	public void newEvent(SensorEvent event) {
		if (event.getSensor().getType() == Sensor.Type.MAGNETIC_FIELD) {
			//magneticProcessorTest.addMeasurement((SensorEvent3D) event);
//			magneticProcessor.newEvent(event);
			magneticProcessorWindTunnel.newEvent(event);
		}
		// TODO implement more sensor types...
	}

	@Override
	public void setReciever(SensorListener listener) {
	    this.listener = listener;

	}

	@Override
	public void start() {
        magneticProcessor = new MagneticProcessorRev1(config);
        magneticProcessorTest = new MagneticProcessorRev2(config);
        magneticProcessorWindTunnel = new MagneticProcessorWindTunnel(config);
        magneticProcessor.setReciever(listener);
        magneticProcessorTest.setListener(listener);
        magneticProcessorWindTunnel.setReciever(listener);
	}

	@Override
	public void stop() {
	    magneticProcessor = null;
	    magneticProcessorTest = null;
	    magneticProcessorWindTunnel = null;
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
