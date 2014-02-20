package com.vaavud.sensor.internal.processor.util;

import java.util.List;

import com.vaavud.sensor.SensorEvent;


public class FrequencyProcessor {
  SensorEventList<? extends SensorEvent> list;
  private Double lastFrequency;
  
  public FrequencyProcessor(SensorEventList<? extends SensorEvent> list) {
    this.list = list;
  }
  
  public Double getStartEndFrequency() {
    Long start = list.first().getTimeUs();
    Long end = list.last().getTimeUs();
    Integer N = list.size();
    
    return getFrequency(start, end, N);
  }
  
  public Double getFrequencyPoints(int nPoints, long Us) {
    if (nPoints < 50) {
      return lastFrequency;
    }
    
    List<? extends SensorEvent> events= list.getLastEvents(nPoints);
    Long start = events.get(0).getTimeUs();
    Long end = events.get(events.size() -1).getTimeUs();
    if (end-start > Us) {
//      System.out.println("Divide N: " + nPoints + " start: " + start + "end: " + end);
      return getFrequencyPoints(nPoints/2, Us);
    }
    
    Integer N = events.size();
    
    return getFrequency(start,  end, N);
  }
  
  public Double getFrequency(Long Us) {
    int nPoints = (int) (Us*lastFrequency / 1000000);
    return getFrequencyPoints(nPoints, (long) (Us*1.1f));
  }
  
  private Double getFrequency(long start, long end, long N) {
    double SF = ((double) (N-1) *1000000) / (end-start);
    lastFrequency = SF;
    return SF;
  }
  
}
