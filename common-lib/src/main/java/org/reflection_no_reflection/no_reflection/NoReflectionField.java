package org.reflection_no_reflection.no_reflection;

import java.lang.reflect.Type;
import org.reflection_no_reflection.Field;

public class NoReflectionField extends Field {

    private Class declaringClass;
    private Class fieldType;
    private int index;
    private String fieldName;
    private String declaringClassName;
    private String fieldTypeName;

    public NoReflectionField(String fieldName, String declaringClassName, String fieldTypeName) {
        this.fieldName = fieldName;
        this.declaringClassName = declaringClassName;
        this.fieldTypeName = fieldTypeName;
    }

    public NoReflectionField(int index, String fieldName, String declaringClassName, String fieldTypeName) {
        this.index = index;
        this.fieldName = fieldName;
        this.declaringClassName = declaringClassName;
        this.fieldTypeName = fieldTypeName;
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
}
