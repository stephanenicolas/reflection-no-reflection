package org.reflection_no_reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public List<Method> getMethods() {
        return new ArrayList<>(mapNameMethodToMethod.values());
    }

    public Method getMethod(String methodName) throws NoSuchMethodException {
        for (Method method : mapNameMethodToMethod.values()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        throw new NoSuchMethodException(methodName);
    }

    public Object getValue(String methodName) throws NoSuchMethodException {
        Object result = mapMethodToValue.get(methodName);
        if (result == null) {
            throw new NoSuchMethodException(methodName);
        }

        //special cases handling
        if (getMethod(methodName).getReturnType().getName().equals("java.lang.String[]")
            && result instanceof List) {
            List temp = (List) result;
            if (temp.size() == 1) {
                result = ((com.sun.tools.javac.code.Attribute.Constant) temp.get(0)).getValue();
            }
        }
        return result;
    }

    public Class<?> annotationType() {
        return annotationType;
    }
}
