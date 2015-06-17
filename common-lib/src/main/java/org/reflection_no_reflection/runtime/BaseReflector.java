package org.reflection_no_reflection.runtime;

import java.lang.reflect.InvocationTargetException;

/**
 * @author SNI.
 */
public class BaseReflector<T> {

    public void setObjectField(Object instance, String fieldName, Object value) {
        throw new UnsupportedOperationException();
    }

    public void setByteField(Object instance, String fieldName, byte value) {
        throw new UnsupportedOperationException();
    }

    public void setShortField(Object instance, String fieldName, short value) {
        throw new UnsupportedOperationException();
    }

    public void setIntField(Object instance, String fieldName, int value) {
        throw new UnsupportedOperationException();
    }

    public void setLongField(Object instance, String fieldName, long value) {
        throw new UnsupportedOperationException();
    }

    public void setFloatField(Object instance, String fieldName, float value) {
        throw new UnsupportedOperationException();
    }

    public void setDoubleField(Object instance, String fieldName, double value) {
        throw new UnsupportedOperationException();
    }

    public void setCharField(Object instance, String fieldName, char value) {
        throw new UnsupportedOperationException();
    }

    public void setBooleanField(Object instance, String fieldName, boolean value) {
        throw new UnsupportedOperationException();
    }

    public Object invokeMethod(Object instance, String methodName, String signature, Object...params) throws InvocationTargetException {
        throw new UnsupportedOperationException();
    }

    public T newInstance(String signature, Object...params) throws InvocationTargetException {
        throw new UnsupportedOperationException();
    }

}
