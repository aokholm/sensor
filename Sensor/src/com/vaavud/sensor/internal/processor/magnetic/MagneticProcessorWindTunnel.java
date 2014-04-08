package com.vaavud.sensor.internal.processor.magnetic;

import java.util.ArrayList;
import java.util.List;

import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.Sensor.Type;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorEvent3D;
import com.vaavud.sensor.internal.processor.magnetic.FFT.Filter;
import com.vaavud.sensor.internal.processor.magnetic.FFT.Interpolation;
import com.vaavud.sensor.internal.processor.util.FrequencyProcessor;
import com.vaavud.sensor.internal.processor.util.SensorEventList;
import com.vaavud.sensor.revolution.RevSensorConfig;

public class MagneticProcessorWindTunnel extends MagneticProcessor{

	private List<FFT> FFTs;
	private SensorEventList<SensorEvent3D> events;
	private long nextEventUs;
	private long rateUs;
	private boolean initialized;
	private FrequencyProcessor frequencyProcessor;
	private RevSensorConfig config;
	
	public MagneticProcessorWindTunnel(RevSensorConfig config) {
	    this.config = config;
		events = new SensorEventList<SensorEvent3D>();
		this.rateUs = config.getRevSensorRateUs();
		frequencyProcessor = new FrequencyProcessor(events);
		initialized = false;
	}
	
	public void initialize(Double testSF) {
	  
	  FFTs = new ArrayList<FFT>();
	  
	  
	  if (config.isLiveTest()) {
	      FFTs.add(getFFT(3.0, testSF, Window.BLACK_MAN, null, null));
	  } else {
	      FFTs.add(getFFT(1.0, testSF, Window.BLACK_MAN, null, null));
	      FFTs.add(getFFT(3.0, testSF, Window.BLACK_MAN, null, null));
	      FFTs.add(getFFT(5.0, testSF, Window.BLACK_MAN, null, null)); 
	  }
	  initialized = true;
	  
    }
	
	private FFT getFFT(Double timeConstant, Double testSF, Window window, Double lowPass, Double highPass) {
	      int dataLength = (int) Math.round(testSF*timeConstant);
	      int fftLength = 16;
	      
	      while (dataLength*4 > fftLength) {
	        fftLength = fftLength*2;
	      }
	      
	      Sensor sensor = new Sensor(Type.FREQUENCY, timeConstant.toString().concat(" ").concat(window.toString()).concat(" new"));
	        
	      return new FFT(dataLength, fftLength, window, 
	            Interpolation.QUADRATIC_INTERPOLATION, Filter.NO_FILTER, 
	            config.getMovAvg(), sensor, listener, highPass, lowPass); 
	    
	}
	
    @Override
    public void newEvent(SensorEvent event) {
        events.addEvent((SensorEvent3D) event);
        
        if (timeForNewEvent(event)) {
          if (initialized) {
            newEvent();
          }
          else {
            if (events.size() > 100) {
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
	        
	        Double SF = frequencyProcessor.getFrequency((long) 3_000_000);
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
