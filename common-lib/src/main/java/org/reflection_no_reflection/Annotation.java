package org.reflection_no_reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class of all the annotation classes inside the RNR framework.
 * This class is a keystone of RNR. It has a little twist and we aim to document it here.
 *
 * The only thing to remember is that: at processing-time (when you use RNR to write an annotation processor),
 * the annotations you manipulation are instances of the present class and they will fault on #annotationType,
 * you probably wanna call #rnrAnnotationType.
 *

 * Thoughts: normally annotations have an annotationType method that returns a Class. But in RNR, they
 * should return RNR classes. At processing time, true classes don't exist and we would not be able
 * to manipulation annotations if we only had this API. We thus added the rnrAnnotationType method.
 *
 * Deeper thoughts: At runtime, we will need to provide true implementations
 * of annotations (for instance to answer the getAnnotation(Class A) method). True annotations means to implement
 * the java.lang.annoation.Annotation interface (yes, it is an interface and yes, you can implement it).
 * This will be achieved via the AnnotationImpl class.
 * The processing-time and runtime annotation APIs cannot be reconciliated. One must return a true class for runtime
 * and the other must return a RNR class.
 *
 * We have tried many patterns to make this clear. But actually, adding this little method rnrAnnotationType for
 * annotation used at processing-time only is the minimum-disturbing API we have found. Yeah, this is a brain-knot lib. :)
 * @author SNI
 */
public class Annotation implements java.lang.annotation.Annotation {

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

    public Class<? extends Annotation> rnrAnnotationType() {
        return annotationType;
    }

    public java.lang.Class<? extends Annotation> annotationType() {
        throw new RuntimeException("This method cannot be called on an RNR annotation during annotation processing. You may want to invoke rnrAnnotationType().");
    }
}
