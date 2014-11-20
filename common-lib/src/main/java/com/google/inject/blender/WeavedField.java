package com.google.inject.blender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class WeavedField extends Field {

    private Class declaringClass;
    private Class fieldType;
    private String fieldName;
    private String declaringClassName;
    private String fieldTypeName;

    public WeavedField(String fieldName, String declaringClassName, String fieldTypeName) {
      this.fieldName = fieldName;
      this.declaringClassName = declaringClassName;
      this.fieldTypeName = fieldTypeName;
    }

    @Override
    public Class<?> getDeclaringClass() {
        try {
          return Class.forName(declaringClassName);
        } catch(Exception ex) {
          ex.printStackTrace();
        }
        return null;
    }

    public String getDeclaringClassName() {
        return declaringClassName;
    }

    @Override
    public Type getGenericType() {
        return fieldType;
    }

    @Override
    public Class getType() {
        return fieldType;
    }

    public String getTypeName() {
        return fieldTypeName;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    public String toString() {
        return "Field " + fieldName + " in " + getDeclaringClass();
    }
}
