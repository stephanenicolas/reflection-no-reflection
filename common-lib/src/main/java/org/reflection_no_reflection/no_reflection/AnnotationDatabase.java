package org.reflection_no_reflection.no_reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.no_reflection.FieldGetterAndSetter;

public abstract class AnnotationDatabase {
    protected static FieldGetterAndSetter fieldGetterAndSetter;

    //TODO add the additional packages here and load database impl classes.
    protected AnnotationDatabase() {}

    //TODO rename methods
    public abstract void fillAnnotationClassesAndFieldsNames(HashMap<String, Map<String, Set<Field>>> mapAnnotationToMapClassWithInjectionNameToFieldSet);

    public abstract void fillAnnotationClassesAndMethods(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodSet);

    public abstract void fillAnnotationClassesAndConstructors(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToConstructorSet);

    public abstract void fillClassesContainingInjectionPointSet(HashSet<String> classesContainingInjectionPointsSet);

    public abstract void fillBindableClasses(HashSet<String> injectedClasses);

    public static class LocalNoReflectionField extends NoReflectionField {

        private int modifiers = Modifier.PUBLIC;

        public LocalNoReflectionField(int index, String fieldName, String declaringClassName, String fieldTypeName, int modifiers, List<? extends Annotation> annotationList) {
            super(index, fieldName, declaringClassName, fieldTypeName, modifiers, annotationList);
        }

        @Override
        public void set(Object object, Object value) throws IllegalAccessException {
            fieldGetterAndSetter.set(getIndex(), object, value);
        }

        @Override
        public Object get(Object object) throws IllegalAccessException {
            return fieldGetterAndSetter.get(getIndex(), object);
        }

        @Override
        public int getInt(Object object) throws IllegalAccessException {
            return fieldGetterAndSetter.getInt(getIndex(), object);
        }

        @Override
        public void setInt(Object object, int value) throws IllegalAccessException {
            fieldGetterAndSetter.setInt(getIndex(), object, value);
        }
        @Override
        public byte getByte(Object object) throws IllegalAccessException {
            return fieldGetterAndSetter.getByte(getIndex(), object);
        }

        @Override
        public void setByte(Object object, byte value) throws IllegalAccessException {
            fieldGetterAndSetter.setByte(getIndex(), object, value);
        }
    }
}
