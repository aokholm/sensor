package com.vaavud.sensor.internal.processor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SensorEventList<E> {

    private List<E> sensorEventList;
    
    public SensorEventList() {
        sensorEventList = new ArrayList<E>();     
    }
    
    public void addEvent(E event) {
        sensorEventList.add(event);
    }
    
    public E last() {
        if (sensorEventList.size() != 0) {
            return sensorEventList.get(sensorEventList.size() -1);
        }
        else {
            return null;
        }
    }
    
    public E first() {
      if (sensorEventList.size() != 0) {
          return sensorEventList.get(0);
      }
      else {
          return null;
      }
  }
    
    public List<E> getEventsAtIndex(Integer index, Integer nPoints) {
        
        List<E> mPointSubList;
        
        if ((sensorEventList.size() - index) > nPoints) {
            mPointSubList = Collections.unmodifiableList(sensorEventList.subList(index , index + nPoints));
        }
        else {
            mPointSubList = Collections.unmodifiableList(sensorEventList.subList(index, sensorEventList.size()-index)); 
        }
        
        return mPointSubList;
    }
    
    
    public List<E> getLastEvents(Integer nPoints) {
                
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
