package org.reflection_no_reflection.no_reflection;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Field;

public class NoReflectionField extends Field {

    private Class declaringClass;
    private Class fieldType;
    private int index;
    private String fieldName;
    private String declaringClassName;
    private String fieldTypeName;
    private int modifiers;
    private List<? extends java.lang.annotation.Annotation> annotationList;
    private Map<String, java.lang.annotation.Annotation> mapAnnotation;


    public NoReflectionField(String fieldName, String declaringClassName, String fieldTypeName, List<? extends java.lang.annotation.Annotation> annotationList) {
        this.fieldName = fieldName;
        this.declaringClassName = declaringClassName;
        this.fieldTypeName = fieldTypeName;
        this.annotationList = annotationList;
        mapAnnotation = new HashMap<>();
        for(java.lang.annotation.Annotation annotation : annotationList) {
            mapAnnotation.put(annotation.annotationType().getName(), annotation);
        }

    }

    public NoReflectionField(int index, String fieldName, String declaringClassName, String fieldTypeName, int modifiers, List<? extends java.lang.annotation.Annotation> annotationList) {
        this.index = index;
        this.fieldName = fieldName;
        this.declaringClassName = declaringClassName;
        this.fieldTypeName = fieldTypeName;
        this.modifiers = modifiers;
        this.annotationList = annotationList;
        mapAnnotation = new HashMap<>();
        for(java.lang.annotation.Annotation annotation : annotationList) {
            mapAnnotation.put(annotation.annotationType().getName(), annotation);
        }

    }

    @Override
    public Class<?> getDeclaringClass() {
        try {
            return Class.forName(declaringClassName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getDeclaringClassName() {
        return declaringClassName;
    }

    @Override
    public Type getGenericType() {
        try {
            return Class.forName(fieldTypeName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Class getType() {
        if (fieldTypeName.equals("int")) {
            return int.class;
        }

        try {
            return Class.forName(fieldTypeName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getTypeName() {
        return fieldTypeName;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return "Field " + fieldName + " in " + getDeclaringClass();
    }

    public List<? extends java.lang.annotation.Annotation> getAnnotationList() {
        return annotationList;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> annotationType) {
        return (A) mapAnnotation.get(annotationType.getName());
    }

    @Override
    public java.lang.annotation.Annotation[] getDeclaredAnnotations() {
        return mapAnnotation.values().toArray(new java.lang.annotation.Annotation[mapAnnotation.values().size()]);
    }
    @Override
    public boolean isAnnotationPresent(Class<? extends java.lang.annotation.Annotation> annotationType) {
        return mapAnnotation.get(annotationType.getName()) != null;
    }

}
