package com.vaavud.sensor.internal.processor.magnetic;

import java.util.List;

import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.internal.processor.util.FrequencyProcessor;
import com.vaavud.sensor.internal.processor.util.SensorEventList;

public class MagneticProcessor{

	private FFT normalFFT;
	private SensorEventList events;
	private long nextEventUs;
	private long rateUs;
	private boolean initialized;
	private FrequencyProcessor frequencyProcessor;
	
	public MagneticProcessor(long rateUs) {
		events = new SensorEventList();
		this.rateUs = rateUs;
		frequencyProcessor = new FrequencyProcessor(events);
	}
	
	public void initialize(Double testSF) {
	  this.normalFFT = new FFT(70, 128, FFT.WELCH_WINDOW, FFT.QUADRATIC_INTERPOLATION);
	  initialized = true;
    }
	
	public SensorEvent addMeasurement(SensorEvent event) {
	  events.addEvent(event);
	  
	  if (timeForNewEvent(event)) {
	    if (initialized) {
	      return newEvent();
	    }
	    else {
	      if (event.timeUs > 500000) {
            initialize(frequencyProcessor.getStartEndFrequency());  
          }
	    }
	  }
		
	  return null;
	}
	
	private SensorEvent newEvent () {
		
		List<SensorEvent> eventSet = events.getLastEvents(normalFFT.getDataLength());
		
		if (eventSet.size() < normalFFT.getDataLength()) {
			//System.out.println("not enought points");
			return null;
		}
		
		
		Double SF = frequencyProcessor.getFrequency((long) 5000000);
		SensorEvent event = normalFFT.getSensorEvent(eventSet, SF);
		
		if (event == null) {
			//System.out.println("freq equals null");
			return null;
		}
		
		return event;
	}
	
	private boolean timeForNewEvent(SensorEvent event ) {
		
		if (nextEventUs == 0) {
			nextEventUs = event.timeUs;
		}
		
		if (event.timeUs >= nextEventUs) {
			
			nextEventUs = nextEventUs + rateUs;
			// set next time
			if (nextEventUs < event.timeUs ) {
				nextEventUs = event.timeUs + rateUs;
			}
			return true;
		}
		return false;
	}
}
