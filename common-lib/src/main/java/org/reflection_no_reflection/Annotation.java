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
    private Class<? extends Annotation> annotationType;
    private Map<Method, Object> mapMethodToValue = new HashMap<>();

    /**
     * Creates a new annotation.
     *
     * @param annotationType class of this annotation type.
     */
    public Annotation(Class annotationType, Map<Method, Object> mapMethodToValue) {
        this.annotationType = annotationType;
        this.mapMethodToValue = mapMethodToValue;
    }

    public String getAnnotationTypeName() {
        return annotationType.getName();
    }

    public List<Method> getMethods() {
        return new ArrayList<>(mapMethodToValue.keySet());
    }

    public Method getMethod(String methodName) throws NoSuchMethodException {
        for (Method method : mapMethodToValue.keySet()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        throw new NoSuchMethodException(methodName);
    }

    public Object getValue(String methodName) throws NoSuchMethodException {
        final Method method = getMethod(methodName);
        Object result = mapMethodToValue.get(method);

        //special cases handling
        if (method.getReturnType().getName().equals("java.lang.String[]")
            && result instanceof List) {
            List temp = (List) result;
            if (temp.size() == 1) {
                result = ((com.sun.tools.javac.code.Attribute.Constant) temp.get(0)).getValue();
            }
        }
        return result;
    }

    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }
}
