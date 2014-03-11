package com.vaavud.sensor;

import java.util.ArrayList;
import java.util.List;

import com.vaavud.sensor.revolution.RevSensorConfig;

public class MeasurementAnalyzer implements SensorListener {
    private List<SensorEvent> events;
    private SensorManager sensorManager;

    public MeasurementAnalyzer(RevSensorConfig config, Sensor.Type ... sensorTypes) {

        events = new ArrayList<SensorEvent>();
        sensorManager = new SensorManager();
        sensorManager.addListener(this, sensorTypes);
    }
    
    public MeasurementAnalyzer(Sensor.Type ... sensorTypes) {
        this(new RevSensorConfig(), sensorTypes);
    }
    
    public void addSensor(BaseSensor sensor) {
        sensorManager.addSensor(sensor);
    }
    
    private void start() {
        try {
            sensorManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newEvent(SensorEvent event) {
        events.add(event);
    }

    public List<SensorEvent> getEvents() {
        if (events.size() == 0) {
            start();
        }
        return events;
    }
}
