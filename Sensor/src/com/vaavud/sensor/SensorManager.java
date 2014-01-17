package com.vaavud.sensor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class SensorManager implements SensorListener{
	
	private EnumMap<SensorType, List<SensorListener>> listenerMap;
	private List<BaseSensor> sensorList;
	
	public SensorManager() {
		listenerMap = new EnumMap<SensorType, List<SensorListener>>(SensorType.class);
		sensorList = new ArrayList<BaseSensor>();
	}
	
	public void addSensor(BaseSensor sensor) {
		sensorList.add(sensor);
		sensor.setReciever(this);
		if (sensor instanceof ProcessingSensor) {
			ProcessingSensor processingSensor = (ProcessingSensor) sensor;
			addListener(processingSensor.getReciever(), processingSensor.getSensorTypes());
		}
	}
	
	
	public void start() {
		for (BaseSensor sensor : sensorList) {
			sensor.start();
		}
	}
	
	public void stop() {
		for (BaseSensor sensor : sensorList) {
			sensor.stop();
		}
	}
	
	
	public void addListener(SensorListener sensorListener, SensorType[] sensorTypes) {
		for (SensorType sensorType : sensorTypes) {
			if (! listenerMap.containsKey(sensorType)) {
				listenerMap.put(sensorType, new ArrayList<SensorListener>());
			}
			
			if (listenerMap.get(sensorType).indexOf(sensorListener) == -1) {
				listenerMap.get(sensorType).add(sensorListener);
			}
		}
	}

	public void removeListener(SensorListener sensorListener, SensorType[] sensorTypes) {
		for (SensorType sensorType : sensorTypes) {
			if (listenerMap.containsKey(sensorType)) {
				listenerMap.get(sensorType).remove(sensorListener);
			}
		}
	}
	
	public void removeListener(SensorListener sensorListener) {
		for (EnumMap.Entry<SensorType, List<SensorListener>> entry : listenerMap.entrySet()) {
			entry.getValue().remove(sensorListener);
		}
	}


	@Override
	public void newEvent(SensorEvent event) {
		List<SensorListener> sensorListeners = listenerMap.get(event.sensor);
		
		for (SensorListener listener : sensorListeners) {
			listener.newEvent(event);
		}

	}
}
