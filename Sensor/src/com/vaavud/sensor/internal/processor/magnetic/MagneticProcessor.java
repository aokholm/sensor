package com.vaavud.sensor.internal.processor.magnetic;

import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorListener;

public abstract class MagneticProcessor implements SensorListener {
    
    protected SensorListener listener;
    
    public void setReciever(SensorListener listener){
        this.listener = listener;
    }
    
    @Override
    abstract public void newEvent(SensorEvent event);

}
