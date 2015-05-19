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
    private Map<String, Object> mapMethodToValue = new HashMap<String, Object>();
    /** Maps method names to their return types. The methods are methods of this annotation. */
    private Map<String, String> mapMethodToReturnType = new HashMap<String, String>();

    /**
     * Creates a new annotation.
     *
     * @param annotationType class of this annotation type.
     * @param mapMethodToValue maps method names to their return values.
     * @param mapMethodToReturnType maps method names to their return types.
     */
    public Annotation(Class annotationType, Map<String, Object> mapMethodToValue, Map<String, String> mapMethodToReturnType) {
        this.annotationType = annotationType;
        this.mapMethodToValue = mapMethodToValue;
        this.mapMethodToReturnType = mapMethodToReturnType;
    }

    public String getAnnotationTypeName() {
        return annotationType.getName();
    }

    public Map<String, Object> getMapMethodToValue() {
        return mapMethodToValue;
    }

    public Map<String, String> getMapMethodToReturnType() {
        return mapMethodToReturnType;
    }

    public Class<?> annotationType() {
        return annotationType;
    }
}
