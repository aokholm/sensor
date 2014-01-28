package com.vaavud.sensor.internal.processor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaavud.sensor.SensorEvent;

public class SensorEventList {

    private List<SensorEvent> sensorEventList;
    
    public SensorEventList() {
        sensorEventList = new ArrayList<SensorEvent>();     
    }
    
    public void addEvent(SensorEvent event) {
        sensorEventList.add(event);
    }
    
    public SensorEvent last() {
        if (sensorEventList.size() != 0) {
            return sensorEventList.get(sensorEventList.size() -1);
        }
        else {
            return null;
        }
    }
    
    public SensorEvent first() {
      if (sensorEventList.size() != 0) {
          return sensorEventList.get(0);
      }
      else {
          return null;
      }
  }
    
    public List<SensorEvent> getEventsAtIndex(Integer index, Integer nPoints) {
        
        List<SensorEvent> mPointSubList;
        
        if ((sensorEventList.size() - index) > nPoints) {
            mPointSubList = Collections.unmodifiableList(sensorEventList.subList(index , index + nPoints));
        }
        else {
            mPointSubList = Collections.unmodifiableList(sensorEventList.subList(index, sensorEventList.size()-index)); 
        }
        
        return mPointSubList;
    }
    
    
    public List<SensorEvent> getLastEvents(Integer nPoints) {
                
        if (nPoints < sensorEventList.size()) {
            return Collections.unmodifiableList(sensorEventList.subList(sensorEventList.size()-nPoints, sensorEventList.size()));
        }
        else {
            return Collections.unmodifiableList(sensorEventList);
        }
    }
    
    public Integer size() {
        return sensorEventList.size();
    }
 }
