package com.vaavud.sensor;

public class SensorEvent3D extends SensorEvent {

    private final double values[];

    public SensorEvent3D(Sensor sensor, long timeUs, double[] values) {
        super(sensor, timeUs);
        if (values.length != 3)
            throw new RuntimeException(
                    "values argument does not contain 3 values");

        this.values = values;
    }

    public Double getX() {
        return values[0];
    }

    public Double getY() {
        return values[1];
    }

    public Double getZ() {
        return values[2];
    }

    @Override
    public String toString() {
        return "SensorEvent3D [sensor=" + getSensor() + ", timeUs=" + getTimeUs()
                + ", getX()=" + getX() + ", getY()=" + getY() + ", getZ()="
                + getZ() + "]";
    }

}
