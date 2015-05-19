package org.reflection_no_reflection;

/**
 * @author SNI.
 */
public class Constructor<T> {
    private String name;
    private Class<?>[] parameterTypes;

    public String getName() {
        return name;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
}
