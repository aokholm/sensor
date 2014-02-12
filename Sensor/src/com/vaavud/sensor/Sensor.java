package com.vaavud.sensor;

public final class Sensor {
  public enum Type{
    MAGNETIC_FIELD,
    ACCELEROMETER,
    GYROSCOPE,
    FREQUENCY,
    WINDSPEED,
    TEMPERATURE,
    PRESSURE
  }
  
  private final Type type;
  private final String name;
  private Object descriptor = null;
  
  
  public Sensor(Type type, String name) {
    this.type = type;
    this.name = name;
  }

  public void setDescriptor(Object descriptor) {
    this.descriptor = descriptor;
}
  
  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
public String toString() {
    return "Sensor [type=" + type + ", name=" + name + ", descriptor=" + descriptor + "]";
}
  
}
