package org.reflection_no_reflection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by administrateur on 14-11-29.
 */
public class Annotation implements java.lang.annotation.Annotation {

    private String annotationTypeName;
    private Map<String,Object> mapMethodToValue = new HashMap<>();
    private Map<String,String> mapMethodToReturnType = new HashMap<>();

    public Annotation(String annotationTypeName, Map<String, Object> mapMethodToValue,Map<String,String> mapMethodToReturnType) {
        this.annotationTypeName = annotationTypeName;
        this.mapMethodToValue = mapMethodToValue;
        this.mapMethodToReturnType = mapMethodToReturnType;
    }

    public String getAnnotationTypeName() {
        return annotationTypeName;
    }

    public Map<String, Object> getMapMethodToValue() {
        return mapMethodToValue;
    }

    public Map<String, String> getMapMethodToReturnType() {
        return mapMethodToReturnType;
    }

    @Override public Class<? extends java.lang.annotation.Annotation> annotationType() {
        try {
            return (Class<? extends java.lang.annotation.Annotation>) Class.forName(annotationTypeName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
