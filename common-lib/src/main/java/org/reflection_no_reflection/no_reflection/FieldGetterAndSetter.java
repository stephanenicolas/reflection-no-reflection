package org.reflection_no_reflection.no_reflection;

public interface FieldGetterAndSetter {

    public void set(int index, Object object, Object value) throws IllegalAccessException;

    public void setInt(int index, Object object, int value) throws IllegalAccessException;

    public Object get(int index, Object object) throws IllegalAccessException;

    public int getInt(int index, Object object) throws IllegalAccessException;

}
