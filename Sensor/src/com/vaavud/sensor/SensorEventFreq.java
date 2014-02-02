package com.vaavud.sensor;

public class SensorEventFreq extends SensorEvent {
    
    private double freq;
    private double amp;
    private double sf;
    
    public SensorEventFreq(Sensor sensor, long timeUs, double freq, double amp, double sf) {
        super(sensor, timeUs);
        this.freq = freq;
        this.amp = amp;
        this.sf = sf;
        // TODO Auto-generated constructor stub
    }
    
    public double getFreq() {
        return freq;
    }
    
    public double getAmp() {
        return amp;
    }
    
    public double getSf() {
        return sf;
    }

    @Override
    public String toString() {
        return "SensorEventFreq [freq=" + freq + ", amp=" + amp + ", sf=" + sf
                + ", getSensor()=" + getSensor() + ", getTimeUs()="
                + getTimeUs() + "]";
    }
    
    
}
