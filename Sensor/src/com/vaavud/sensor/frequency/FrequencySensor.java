package com.vaavud.sensor.frequency;

import com.vaavud.sensor.ProcessingSensor;
import com.vaavud.sensor.Sensor.Type;
import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorEvent1D;
import com.vaavud.sensor.SensorListener;

public class FrequencySensor extends ProcessingSensor implements SensorListener{
    
    SensorListener listener;
    SensorEvent prevEvent;
    Sensor sensor = new Sensor(Type.SAMPLE_FREQUENCY, "MF_SF");

    public FrequencySensor() {
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
    public SensorListener getReciever() {
        return this;
    }

    @Override
    public Type[] getSensorTypes() {
        return new Type[]{Type.MAGNETIC_FIELD};
    }

    @Override
    public void newEvent(SensorEvent event) {
         
        if (prevEvent != null) {
            
            double timeDiff = (double) (event.getTimeUs() - prevEvent.getTimeUs());
            
            double sf = 1/timeDiff * 1_000_000;
            
            listener.newEvent(new SensorEvent1D(sensor, event.getTimeUs(), sf));
        }
        
        prevEvent = event;
    }

}
