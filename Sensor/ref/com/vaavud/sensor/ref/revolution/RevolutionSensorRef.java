package com.vaavud.sensor.ref.revolution;

import com.vaavud.sensor.ProcessingSensor;
import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorEvent3D;
import com.vaavud.sensor.SensorListener;
import com.vaavud.sensor.ref.internal.processor.magnetic.MagneticProcessorRef;
import com.vaavud.sensor.revolution.RevSensorConfig;

public class RevolutionSensorRef extends ProcessingSensor implements SensorListener{

    private SensorListener listener;
    private MagneticProcessorRef magneticProcessorRef;
    
    public RevolutionSensorRef(RevSensorConfig config) {
        magneticProcessorRef = new MagneticProcessorRef(config);
    }
    
    public RevolutionSensorRef() {
        this(new RevSensorConfig());
    }
    
    @Override
    public void newEvent(SensorEvent event) {
        if (event.getSensor().getType() == Sensor.Type.MAGNETIC_FIELD) {

            SensorEvent newEventRef = (magneticProcessorRef).addMeasurement((SensorEvent3D) event);
            if (newEventRef != null) {
               listener.newEvent(newEventRef);
            }
        }
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