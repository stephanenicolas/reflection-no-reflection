package org.reflection_no_reflection.no_reflection;

public interface FieldGetterAndSetter {
    public Object get(int index, Object object) throws IllegalAccessException;
    public void set(int index, Object object, Object value) throws IllegalAccessException;

    public int getInt(int index, Object object) throws IllegalAccessException;
    public void setInt(int index, Object object, int value) throws IllegalAccessException;

    public byte getByte(int index, Object object) throws IllegalAccessException;
    public void setByte(int index, Object object, byte value) throws IllegalAccessException;

    public short getShort(int index, Object object) throws IllegalAccessException;
    public void setShort(int index, Object object, short value) throws IllegalAccessException;

    public long getLong(int index, Object object) throws IllegalAccessException;
    public void setLong(int index, Object object, long value) throws IllegalAccessException;

    public float getFloat(int index, Object object) throws IllegalAccessException;
    public void setFloat(int index, Object object, float value) throws IllegalAccessException;

    public double getDouble(int index, Object object) throws IllegalAccessException;
    public void setDouble(int index, Object object, double value) throws IllegalAccessException;

    public boolean getBoolean(int index, Object object) throws IllegalAccessException;
    public void setBoolean(int index, Object object, boolean value) throws IllegalAccessException;

    public char getChar(int index, Object object) throws IllegalAccessException;
    public void setChar(int index, Object object, char value) throws IllegalAccessException;
}
