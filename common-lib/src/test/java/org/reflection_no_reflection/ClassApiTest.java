package org.reflection_no_reflection;

import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.Test;

/**
 * @author SNI.
 */
public class ClassApiTest {

    private final static boolean DETAIL = false;

    @Test
    public void testClassApi() throws Exception {

        final Class<java.lang.Class> reflectionClass = Class.class;
        final Class<org.reflection_no_reflection.Class> rnrClass = org.reflection_no_reflection.Class.class;

        final long similarityMeasure = getSimilarityMeasure(reflectionClass, rnrClass);
        System.out.println("Api compat score for Class: " + similarityMeasure + "%");
    }

    @Test
    public void testFieldApi() throws Exception {

        final Class<java.lang.reflect.Field> reflectionClass = java.lang.reflect.Field.class;
        final Class<org.reflection_no_reflection.Field> rnrClass = org.reflection_no_reflection.Field.class;

        final long similarityMeasure = getSimilarityMeasure(reflectionClass, rnrClass);
        System.out.println("Api compat score for Field: " + similarityMeasure + "%");
    }

    @Test
    public void testMethodApi() throws Exception {

        final Class<java.lang.reflect.Method> reflectionClass = java.lang.reflect.Method.class;
        final Class<org.reflection_no_reflection.Method> rnrClass = org.reflection_no_reflection.Method.class;

        final long similarityMeasure = getSimilarityMeasure(reflectionClass, rnrClass);
        System.out.println("Api compat score for Method: " + similarityMeasure + "%");
    }

    private long getSimilarityMeasure(Class<?> reflectionClass, Class<?> rnrClass) {
        int equalMethodCount = 0;
        int publicMethodCount = 0;
        for (Method method : reflectionClass.getMethods()) {
            if ((method.getModifiers() & Modifier.PUBLIC) != 0) {
                publicMethodCount++;
                if (hasSimilarMethod(method, rnrClass)) {
                    equalMethodCount++;
                } else {
                    if (DETAIL) {
                        System.out.println(method.getName() + " has no similar methods");
                    }
                }
            }
        }

        return Math.round((100 * equalMethodCount) / (double) publicMethodCount);
    }

    private boolean hasSimilarMethod(Method method, java.lang.Class<?> rnrClass) {
        for (Method rnrMethod : rnrClass.getMethods()) {
            final boolean hasSimilarName = rnrMethod.getName().equals(method.getName());
            final boolean hasSimilarReturnType = rnrMethod.getReturnType().getSimpleName().equals(method.getReturnType().getSimpleName());
            final boolean hasSimilarParamTypes = hasSimilarTypes(rnrMethod.getParameterTypes(), method.getParameterTypes());
            if (hasSimilarName) {
                if (hasSimilarReturnType) {
                    if (hasSimilarParamTypes) {
                        return true;
                    } else {
                        if (DETAIL) {
                            System.out.println("Method of rnrClass : " + rnrClass.getSimpleName() + "#" + method + " has different param types.");
                        }
                    }
                } else {
                    if (DETAIL) {
                        System.out.println("Method of rnrClass : " + rnrClass.getSimpleName() + "#" + method + " has a different return type.");
                    }
                }
            }
        }

        return false;
    }

    private boolean hasSimilarTypes(Class<?>[] parameterTypes, Class<?>[] rnrParameterTypes) {
        if (parameterTypes.length != rnrParameterTypes.length) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (!parameterType.getSimpleName().equals(rnrParameterTypes[i].getSimpleName())) {
                return false;
            }
        }
        return true;
    }
}
