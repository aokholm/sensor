package com.vaavud.sensor.internal.processor.util;

import java.util.List;

import com.vaavud.sensor.SensorEvent;


public class FrequencyProcessor {
  SensorEventList list;
  private Double lastFrequency;
  
  public FrequencyProcessor(SensorEventList list) {
    this.list = list;
  }
  
  public Double getStartEndFrequency() {
    Long start = list.first().timeUs;
    Long end = list.last().timeUs;
    Integer N = list.size();
    
    return getFrequency(start, end, N);
  }
  
  public Double getFrequencyPoints(int nPoints, long Us) {
    if (nPoints < 50) {
      return lastFrequency;
    }
    
    List<SensorEvent> events= list.getLastEvents(nPoints);
    Long start = events.get(0).timeUs;
    Long end = events.get(events.size() -1).timeUs;
    if (end-start > Us) {
      System.out.println("Divide N: " + nPoints + " start: " + start + "end: " + end);
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
