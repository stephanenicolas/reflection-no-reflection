package org.reflection_no_reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class Field {

  public Object get (Object object) throws IllegalAccessException {
    return null; //not implemented
  }

  public <A extends Annotation> A getAnnotation (Class<A> annotationType) {
    return null; //not implemented
  }

  public boolean getBoolean (Object object) throws IllegalAccessException {
    return false; //not implemented
  }

  public byte getByte (Object object) throws IllegalAccessException {
    return 0; //not implemented
  }

  public char getChar (Object object) throws IllegalAccessException {
    return '\u0000'; //not implemented
  }

  public Annotation[] getDeclaredAnnotations () {
    return null; //not implemented
  }

  public Class<?> getDeclaringClass () {
    return null; //not implemented
  }

  public double getDouble (Object object) throws IllegalAccessException {
    return 0; //not implemented
  }

  public float getFloat (Object object) throws IllegalAccessException {
    return 0; //not implemented
  }

  public Type getGenericType () {
    return null; //not implemented
  }

  public int getInt (Object object) throws IllegalAccessException {
    return 0; //not implemented
  }

  public long getLong (Object object) throws IllegalAccessException {
    return 0; //not implemented
  }

  public int getModifiers () {
    return 0; //not implemented
  }

  public String getName () {
    return null; //not implemented
  }

  public short getShort (Object object) throws IllegalAccessException {
    return 0; //not implemented
  }

  public Class<?> getType () {
    return null; //not implemented
  }

  public int hashCode () {
    return 0; //not implemented
  }

  public boolean isAnnotationPresent (Class<? extends Annotation> annotationType) {
    return false; //not implemented
  }

  public boolean isEnumConstant () {
    return false; //not implemented
  }

  public boolean isSynthetic () {
    return false; //not implemented
  }

  public void set (Object object, Object value) throws IllegalAccessException {
    //not implemented
  }

  public void setBoolean (Object object, boolean value) throws IllegalAccessException {
    //not implemented
  }

  public void setByte (Object object, byte value) throws IllegalAccessException {
    //not implemented
  }

  public void setChar (Object object, char value) throws IllegalAccessException {
    //not implemented
  }

  public void setDouble (Object object, double value) throws IllegalAccessException {
    //not implemented
  }

  public void setFloat (Object object, float value) throws IllegalAccessException {
    //not implemented
  }

  public void setInt (Object object, int value) throws IllegalAccessException {
    //not implemented
  }

  public void setLong (Object object, long value) throws IllegalAccessException {
    //not implemented
  }

  public void setShort (Object object, short value) throws IllegalAccessException {
    //not implemented
  }

  public String toGenericString () {
    return null; //not implemented
  }

  public String toString () {
    return null; //not implemented
  }

}
