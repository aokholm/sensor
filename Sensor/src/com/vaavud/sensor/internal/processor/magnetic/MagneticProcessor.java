package com.vaavud.sensor.internal.processor.magnetic;

import java.util.ArrayList;
import java.util.List;

import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.Sensor.Type;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorEvent3D;
import com.vaavud.sensor.SensorListener;
import com.vaavud.sensor.internal.processor.magnetic.FFT.Filter;
import com.vaavud.sensor.internal.processor.magnetic.FFT.Interpolation;
import com.vaavud.sensor.internal.processor.util.FrequencyProcessor;
import com.vaavud.sensor.internal.processor.util.SensorEventList;

public class MagneticProcessor{

	private List<FFT> FFTs;
	private SensorEventList<SensorEvent3D> events;
	private long nextEventUs;
	private long rateUs;
	private boolean initialized;
	private FrequencyProcessor frequencyProcessor;
	private SensorListener listener;
	
	public MagneticProcessor(long rateUs) {
		events = new SensorEventList<SensorEvent3D>();
		this.rateUs = rateUs;
		frequencyProcessor = new FrequencyProcessor(events);
		initialized = false;
	}
	
	public void initialize(Double testSF) {
	    
	  FFTs = new ArrayList<FFT>();  
	    
	  FFTs.add(getFFT(0.5, testSF));
	  FFTs.add(getFFT(1d, testSF));
	  FFTs.add(getFFT(2.0, testSF));
	  
	  initialized = true;
    }
	
	private FFT getFFT(Double timeConstant, Double testSF) {
	      int dataLength = (int) Math.round(testSF*timeConstant);
	      int fftLength = 16;
	      
	      while (dataLength*4 > fftLength) {
	        fftLength = fftLength*2;
	      }
	      
	      Sensor sensor = new Sensor(Type.FREQUENCY, timeConstant.toString().concat(" new"));
	        
	      return new FFT(dataLength, fftLength, Window.HANN, 
	            Interpolation.QUADRATIC_INTERPOLATION, Filter.NO_FILTER, null, sensor, listener); 
	    
	}
	
	public void setListener(SensorListener listener) {
        this.listener = listener;
    }
	
	
	public void addMeasurement(SensorEvent3D event) {
	  events.addEvent(event);
	  
	  if (timeForNewEvent(event)) {
	    if (initialized) {
	      newEvent();
	    }
	    else {
	      if (events.size() > 50) {
            initialize(frequencyProcessor.getStartEndFrequency());  
          }
	    }
	  }
	}
	
	private void newEvent () {
		
	    for(FFT fft : FFTs) {
	        List<SensorEvent3D> eventSet = events.getLastEvents(fft.getDataLength());
	        
	        if (eventSet.size() < fft.getDataLength()) {
	            //System.out.println("not enought points");
	            return;
	        }
	        
	        Double SF = frequencyProcessor.getFrequency((long) 2000000);
	        fft.newSensorEvent(eventSet, SF);
	    }
	}
	
	private boolean timeForNewEvent(SensorEvent event ) {
		
		if (nextEventUs == 0) {
			nextEventUs = event.getTimeUs();
		}
		
		if (event.getTimeUs() >= nextEventUs) {
			
			nextEventUs = nextEventUs + rateUs;
			// set next time
			if (nextEventUs < event.getTimeUs() ) {
				nextEventUs = event.getTimeUs() + rateUs;
			}
			return true;
		}
		return false;
	}
}
