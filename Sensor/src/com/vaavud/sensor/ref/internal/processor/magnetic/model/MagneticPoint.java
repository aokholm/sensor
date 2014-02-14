package com.vaavud.sensor.ref.internal.processor.magnetic.model;

public final class MagneticPoint {
	private final long timeUs;
	private final double x;
	private final double y;
	private final double z;
	
	public MagneticPoint(long timeUs, double x, double y, double z) {
		this.timeUs = timeUs;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public long getTimeUs() {
		return timeUs;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
	
	@Override
	public String toString() {
		return "CoreMagneticPoint [timeUs=" + timeUs + ", x=" + x + ", y=" + y
				+ ", z=" + z + "]";
	}
	
}
