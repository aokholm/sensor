package com.vaavud.sensor;

public class SensorEventFreq extends SensorEvent {
    
    private Double freq;
    private Double amp;
    private Double sf;
    private Double sn;
    
    public SensorEventFreq(Sensor sensor, long timeUs, Double freq, Double amp, Double sf, Double sn) {
        super(sensor, timeUs);
        this.freq = freq;
        this.amp = amp;
        this.sf = sf;
        this.sn = sn;
    }
    
    public Double getFreq() {
        return freq;
    }
    
    public Double getAmp() {
        return amp;
    }
    
    public Double getSf() {
        return sf;
    }
    
    public Double getSN() {
        return sn;
    }

    @Override
    public String toString() {
        return "SensorEventFreq [freq=" + freq + ", amp=" + amp + ", sf=" + sf + ", sn=" + sn + ", getSensor()="
                + getSensor() + ", getTimeUs()=" + getTimeUs() + "]";
    }
    
    
}
