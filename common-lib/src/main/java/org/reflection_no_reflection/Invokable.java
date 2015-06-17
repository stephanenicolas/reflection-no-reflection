package org.reflection_no_reflection;

public interface Invokable {
    Class<?>[] getParameterTypes();
    Class<?>[] getExceptionTypes();
}
