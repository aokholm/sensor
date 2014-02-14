package com.vaavud.sensor.ref.internal.processor.magnetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaavud.sensor.ref.internal.processor.magnetic.model.MagneticPoint;

public class MagneticPointList {
	
	private List<MagneticPoint> magneticPointList;
	
	public MagneticPointList() {
		magneticPointList = new ArrayList<MagneticPoint>();		
	}

	
	public void addMagneticPoints(List<MagneticPoint> magneticfieldMeasurements) {
		this.magneticPointList = magneticfieldMeasurements;
	}
	
	public void addMagneticPoint(MagneticPoint magReading) {
		magneticPointList.add(magReading);
	}
	
	
	public MagneticPoint last() {
		if (magneticPointList.size() != 0) {
			return magneticPointList.get(magneticPointList.size() -1);
		}
		else {
			return null;
		}
	}
	
	public List<MagneticPoint> getMagneticPointsAtIndex(Integer index, Integer nPoints) {
		
		List<MagneticPoint> mPointSubList;
		
		if ((magneticPointList.size() - index) > nPoints) {
			mPointSubList = Collections.unmodifiableList(magneticPointList.subList(index , index + nPoints));
		}
		else {
			mPointSubList = Collections.unmodifiableList(magneticPointList.subList(index, magneticPointList.size()-index));	
		}
		
		return mPointSubList;
	}
	
	
	public List<MagneticPoint> getLastPoints(Integer nPoints) {
				
		if (nPoints < magneticPointList.size()) {
			return Collections.unmodifiableList(magneticPointList.subList(magneticPointList.size()-nPoints, magneticPointList.size()));
		}
		else {
			return Collections.unmodifiableList(magneticPointList);
		}
	}
	
	public Integer size() {
		return magneticPointList.size();
	}
	
//	public List<CoreMagneticPoint> getMagneticfieldMeasurements() {
//		return magneticPointList;
//	}
	
//	public void clearData() {
//		magneticPointList = new ArrayList<CoreMagneticPoint>();
//	}
	
//	public Double getTimeSinceStart() {
//		if (magneticPointList.size() >= 1) {
//			return magneticPointList.get(magneticPointList.size() -1).getTime();
//		}
//		else {
//			return 0d;
//		}
//	}
	

}
