package com.vaavud.sensor.internal.processor.magnetic;

import java.util.List;

import com.vaavud.sensor.Sensor;
import com.vaavud.sensor.Sensor.Type;
import com.vaavud.sensor.SensorEvent;
import com.vaavud.sensor.internal.processor.magnetic.FFT.Filter;
import com.vaavud.sensor.internal.processor.magnetic.FFT.Interpolation;
import com.vaavud.sensor.internal.processor.magnetic.FFT.Window;
import com.vaavud.sensor.internal.processor.magnetic.model.MagneticPoint;
import com.vaavud.sensor.internal.processor.magnetic.model.MeasurementPoint;

public class MagneticProcessorRef {

	private FFT normalFFT;
	private MagneticPointList mPList;
	private long nextEventUs;
	private long rateUs;
	private Sensor sensor;
	public MagneticProcessorRef(long rateUs) {
		this.mPList = new MagneticPointList();
		this.normalFFT = new FFT(70, 128, Window.WELCH_WINDOW, Interpolation.QUADRATIC_INTERPOLATION, Filter.NO_FILTER, null);
		this.rateUs = rateUs;
		this.sensor = new Sensor(Type.FREQUENCY, "Freq_Reference");
	}
	
	public SensorEvent addMeasurement(SensorEvent event) {
		MagneticPoint magneticPoint = new MagneticPoint(event.timeUs, event.values[0], event.values[1], event.values[2]);
		mPList.addMagneticPoint(magneticPoint);
		
		if (timeForNewEvent(magneticPoint)) {
			return newEvent();
		}
		
		return null;
		// update
	}
	
	private SensorEvent newEvent () {
		
		List<MagneticPoint> mPoints = mPList.getLastPoints(normalFFT.getDataLength());
		
		if (mPoints.size() < normalFFT.getDataLength()) {
			System.out.println("not enought points");
			return null;
		}
		
		
		Double sampleF = getSampleFrequency(mPoints);
		MeasurementPoint coreMeasurementPoint = normalFFT.getFreqAndAmp3DFFT(mPoints, sampleF);
		
		if (coreMeasurementPoint == null) {
			System.out.println("coreMeasurementPoint equals null");
			return null;
		}
		
		SensorEvent event = new SensorEvent(sensor, 
				mPList.last().getTimeUs(), new double[]{coreMeasurementPoint.getFrequency(), coreMeasurementPoint.getAmplitude(), sampleF} );	
		return event;
	}
	
	private boolean timeForNewEvent(MagneticPoint mP ) {
		
		if (nextEventUs == 0) {
			nextEventUs = mP.getTimeUs();
		}
		
		if (mP.getTimeUs() >= nextEventUs) {
			
			nextEventUs = nextEventUs + rateUs;
			// set next time
			if (nextEventUs < mP.getTimeUs() ) {
				nextEventUs = mP.getTimeUs() + rateUs;
			}
			return true;
		}
		return false;
	}
	
	
	private Double getSampleFrequency(List<MagneticPoint> mPoints) {
		long timeDiff = mPoints.get(mPoints.size()-1).getTimeUs() - mPoints.get(0).getTimeUs();
		double sampleFrequency = (normalFFT.getDataLength() -1) / (double) timeDiff * 1000000;
		return sampleFrequency;
		
	}


}
