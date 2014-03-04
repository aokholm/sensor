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
import com.vaavud.sensor.revolution.RevSensorConfig;

public class MagneticProcessorRev2{

	private List<FFT> FFTs;
	private SensorEventList<SensorEvent3D> events;
	private long nextEventUs;
	private long rateUs;
	private boolean initialized;
	private FrequencyProcessor frequencyProcessor;
	private SensorListener listener;
	private RevSensorConfig config;
	
	public MagneticProcessorRev2(RevSensorConfig config) {
	    this.config = config;
		events = new SensorEventList<SensorEvent3D>();
		this.rateUs = config.getRevSensorRateUs();
		frequencyProcessor = new FrequencyProcessor(events);
		initialized = false;
	}
	
	public void initialize(Double testSF) {
	  
	  FFTs = new ArrayList<FFT>();
	  
      FFTs.add(getFFT(0.5, testSF, Window.BLACK_MAN, 100d, null));
      FFTs.add(getFFT(1.0, testSF, Window.BLACK_MAN, null, null));
      FFTs.add(getFFT(2.0, testSF, Window.BLACK_MAN, null, null));
	  initialized = true;
	  
    }
	
	private FFT getFFT(Double timeConstant, Double testSF, Window window, Double lowPass, Double highPass) {
	      int dataLength = (int) Math.round(testSF*timeConstant);
	      int fftLength = 16;
	      
	      while (dataLength*4 > fftLength) {
	        fftLength = fftLength*2;
	      }
	      
	      if (lowPass == null) {
	          lowPass = 1/timeConstant*9;
	      }
	      if (highPass == null) {
	          highPass = 1/timeConstant*2;
	      }
	     
	      Sensor sensor = new Sensor(Type.FREQUENCY, timeConstant.toString().concat(" ").concat(window.toString()).concat(" new"));
	        
	      return new FFT(dataLength, fftLength, window, 
	            Interpolation.QUADRATIC_INTERPOLATION, Filter.NO_FILTER, 
	            config.getMovAvg(), sensor, listener, highPass, lowPass); 
	    
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
	            return;
	        }
	        
	        Double SF = frequencyProcessor.getFrequency((long) 5_000_000);
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
