package org.reflection_no_reflection;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class of all the annotation classes inside the RNR framework.
 * @author SNI
 */
public class Annotation {

    /** The class name of this annotation type. */
    private Class<?> annotationType;
    /** Maps method names to their return values. The methods are methods of this annotation. */
    private Map<String, Object> mapMethodToValue = new HashMap<>();
    /** Maps method names to the methods. The methods are methods of this annotation. */
    private Map<String, Method> mapNameMethodToMethod = new HashMap<>();

    /**
     * Creates a new annotation.
     *
     * @param annotationType class of this annotation type.
     * @param mapMethodToValue maps method names to their return values.
     * @param mapNameMethodToMethod maps method names to the methods.
     */
    public Annotation(Class annotationType, Map<String, Object> mapMethodToValue, Map<String, Method> mapNameMethodToMethod) {
        this.annotationType = annotationType;
        this.mapMethodToValue = mapMethodToValue;
        this.mapNameMethodToMethod = mapNameMethodToMethod;
    }

    public String getAnnotationTypeName() {
        return annotationType.getName();
    }

    public Map<String, Object> getMapMethodToValue() {
        return mapMethodToValue;
    }

    public Map<String, Method> getMapNameMethodToMethod() {
        return mapNameMethodToMethod;
    }

    public Class<?> annotationType() {
        return annotationType;
    }
}
