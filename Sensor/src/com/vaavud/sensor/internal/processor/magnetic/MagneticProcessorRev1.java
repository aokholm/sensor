package com.vaavud.sensor.internal.processor.magnetic;

import java.util.ArrayList;
import java.util.List;

import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.Sensor.Type;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.SensorEvent3D;
import com.vaavud.sensor.SensorEventFreq;
import com.vaavud.sensor.internal.processor.magnetic.FFT.Filter;
import com.vaavud.sensor.internal.processor.magnetic.FFT.FreqAmp;
import com.vaavud.sensor.internal.processor.magnetic.FFT.Interpolation;
import com.vaavud.sensor.internal.processor.util.FrequencyProcessor;
import com.vaavud.sensor.internal.processor.util.SensorEventList;
import com.vaavud.sensor.revolution.RevSensorConfig;

public class MagneticProcessorRev1 extends MagneticProcessor{

    public class FFTconfig {
        private final FFT fft;
        private final double timeConstant;
        private final Double transitionFreq;
        private final Double transitionLength;

        public FFTconfig(FFT fft, double timeConstant, Double transitionFreq, Double transitionLength) {
            this.fft = fft;
            this.timeConstant = timeConstant;
            this.transitionFreq = transitionFreq;
            this.transitionLength = transitionLength;
        }

        public FFT getFft() {
            return fft;
        }
        
        public double getTimeConstant() {
            return timeConstant;
        }
        
        public Double getTransitionFreq() {
            return transitionFreq;
        }
        
        public Double getTransitionLength() {
            return transitionLength;
        }
        
    }
    
	private List<FFTconfig> FFTconfigs;
	private SensorEventList<SensorEvent3D> events;
	private long nextEventUs;
	private long rateUs;
	private boolean initialized;
	private FrequencyProcessor frequencyProcessor;
	private RevSensorConfig config;
	private Sensor sensor;
	
	public MagneticProcessorRev1(RevSensorConfig config) {
	    this.config = config;
		events = new SensorEventList<SensorEvent3D>();
		this.rateUs = config.getRevSensorRateUs();
		frequencyProcessor = new FrequencyProcessor(events);
		initialized = false;
		sensor = new Sensor(Type.FREQUENCY, "Prototype");
	}
	
	public void initialize(Double testSF) {
	  
	  FFTconfigs = new ArrayList<FFTconfig>();
	  
	  FFTconfigs.add(new FFTconfig(getFFT(0.5, testSF, Window.BLACK_MAN), 0.5, 7.0, 0.0));
	  FFTconfigs.add(new FFTconfig(getFFT(1.2, testSF, Window.BLACK_MAN), 1.2, 4.0, 0.0));
	  FFTconfigs.add(new FFTconfig(getFFT(2.5, testSF, Window.BLACK_MAN), 2.5, 2.0, 0.0));
	  
	  initialized = true;
	  
    }
	
	private FFT getFFT(Double timeConstant, Double testSF, Window window) {
	      int dataLength = (int) Math.round(testSF*timeConstant);
	      int fftLength = 16;
	      
	      while (dataLength*4 > fftLength) {
	        fftLength = fftLength*2;
	      }
	      
	      return new FFT(dataLength, fftLength, window, 
	            Interpolation.QUADRATIC_INTERPOLATION, Filter.NO_FILTER, 
	            config.getMovAvg(), sensor, listener); 
	    
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
		
	    for (FFTconfig fftConfig :FFTconfigs) {
	        List<SensorEvent3D> eventSet = events.getLastEvents(fftConfig.getFft().getDataLength());
	        
	        if (eventSet.size() < fftConfig.getFft().getDataLength()) {
                return;
            }
	        
	        Double SF = frequencyProcessor.getFrequency((long) 2_000_000);
	        
	        FreqAmp freqAmp = fftConfig.getFft().getNewSensorEvent(eventSet, SF);
	        
	        if (freqAmp.frequency > fftConfig.transitionFreq) {
	            SensorEvent event = new SensorEventFreq(sensor, freqAmp.timeUs, freqAmp.frequency, freqAmp.amplitude, SF, freqAmp.noise);
	            listener.newEvent(event);
	            return;
	        }
	        
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
