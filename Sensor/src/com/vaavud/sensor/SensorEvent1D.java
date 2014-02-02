package com.vaavud.sensor;

public class SensorEvent1D extends SensorEvent {

    private final double value;
    
    public SensorEvent1D(Sensor sensor, long timeUs, double value) {
        super(sensor, timeUs);
        this.value = value;
    }
    
    public double getValue() {
        return value;
    }
    
    
}
