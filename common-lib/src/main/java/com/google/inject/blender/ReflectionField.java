package com.google.inject.blender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by administrateur on 14-11-23.
 */
public class ReflectionField extends Field {
    private java.lang.reflect.Field field;

    public ReflectionField(java.lang.reflect.Field field) {
        this.field = field;
        field.setAccessible(true);
    }

    @Override public Object get(Object object) throws IllegalAccessException {
        return field.get(object);
    }

    @Override public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return field.getAnnotation(annotationType);
    }

    @Override public boolean getBoolean(Object object) throws IllegalAccessException {
        return field.getBoolean(object);
    }

    @Override public byte getByte(Object object) throws IllegalAccessException {
        return field.getByte(object);
    }

    @Override public char getChar(Object object) throws IllegalAccessException {
        return field.getChar(object);
    }

    @Override public Annotation[] getDeclaredAnnotations() {
        return field.getDeclaredAnnotations();
    }

    @Override public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override public double getDouble(Object object) throws IllegalAccessException {
        return field.getDouble(object);
    }

    @Override public float getFloat(Object object) throws IllegalAccessException {
        return field.getFloat(object);
    }

    @Override public Type getGenericType() {
        return field.getGenericType();
    }

    @Override public int getInt(Object object) throws IllegalAccessException {
        return field.getInt(object);
    }

    @Override public long getLong(Object object) throws IllegalAccessException {
        return field.getLong(object);
    }

    @Override public int getModifiers() {
        return field.getModifiers();
    }

    @Override public String getName() {
        return field.getName();
    }

    @Override public short getShort(Object object) throws IllegalAccessException {
        return field.getShort(object);
    }

    @Override public Class<?> getType() {
        return field.getType();
    }

    @Override public int hashCode() {
        return field.hashCode();
    }

    @Override public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }

    @Override public boolean isEnumConstant() {
        return field.isEnumConstant();
    }

    @Override public boolean isSynthetic() {
        return field.isSynthetic();
    }

    @Override public void set(Object object, Object value) throws IllegalAccessException {
        field.set(object, value);
    }

    @Override public void setBoolean(Object object, boolean value) throws IllegalAccessException {
        field.setBoolean(object, value);
    }

    @Override public void setByte(Object object, byte value) throws IllegalAccessException {
        field.setByte(object, value);
    }

    @Override public void setChar(Object object, char value) throws IllegalAccessException {
        field.setChar(object, value);
    }

    @Override public void setDouble(Object object, double value) throws IllegalAccessException {
        field.setDouble(object, value);
    }

    @Override public void setFloat(Object object, float value) throws IllegalAccessException {
        field.setFloat(object, value);
    }

    @Override public void setInt(Object object, int value) throws IllegalAccessException {
        field.setInt(object, value);
    }

    @Override public void setLong(Object object, long value) throws IllegalAccessException {
        field.setLong(object, value);
    }

    @Override public void setShort(Object object, short value) throws IllegalAccessException {
        field.setShort(object, value);
    }

    @Override public String toGenericString() {
        return field.toGenericString();
    }

    @Override public String toString() {
        return field.toString();
    }
}
