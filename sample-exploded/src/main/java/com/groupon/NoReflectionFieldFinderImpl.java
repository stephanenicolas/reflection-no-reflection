package com.groupon;

import com.google.inject.blender.Field;
import com.google.inject.blender.WeavedField;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 * Created by administrateur on 14-11-23.
 */
public class NoReflectionFieldFinderImpl implements FieldFinder {
    private final FieldGetterAndSetter fieldGetterAndSetter = new FieldGetterAndSetter();

    @Override
    public List<Field> getAllFields(Class<? extends Annotation> annotationClass, Class clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        if (clazz == A.class) {
            HashMap<Class<? extends Annotation>, Annotation> mapAnnotation = new HashMap<Class<? extends Annotation>, Annotation>();
            mapAnnotation.put(Inject.class, new NoReflectionInjectAnnotation());
            Field fieldB = new LocalWeavedField(0, "b", A.class.getName(), B.class.getName(), Modifier.PUBLIC, mapAnnotation);

            mapAnnotation = new HashMap<Class<? extends Annotation>, Annotation>();
            mapAnnotation.put(Inject.class, new NoReflectionInjectAnnotation());
            Field fieldC = new LocalWeavedField(1, "c", A.class.getName(), int.class.getName(), 0, mapAnnotation);
            fieldList.add(fieldB);
            fieldList.add(fieldC);
        }
        return fieldList;
    }

    private class LocalWeavedField extends WeavedField {

        private NoReflectionInjectAnnotation noReflectionInjectAnnotation = new NoReflectionInjectAnnotation();
        private int modifiers = Modifier.PUBLIC;
        private final Map<Class<? extends Annotation>, Annotation> mapAnnotation;

        public LocalWeavedField(int index, String fieldName, String declaringClassName, String fieldTypeName, int modifiers, Map<Class<? extends Annotation>, Annotation> mapAnnotation) {
            super(index, fieldName, declaringClassName, fieldTypeName);
            this.modifiers = modifiers;
            this.mapAnnotation = mapAnnotation;
        }

        @Override
        public <A extends Annotation> A getAnnotation (Class<A> annotationType) {
            return (A) mapAnnotation.get(annotationType);
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return mapAnnotation.values().toArray(new Annotation[mapAnnotation.values().size()]);
        }

        @Override
        public int getModifiers() {
            return modifiers;
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
        public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
            return mapAnnotation.get(annotationType) != null;
        }

        @Override
        public int getInt(Object object) throws IllegalAccessException {
            return fieldGetterAndSetter.getInt(getIndex(), object);
        }

        @Override
        public void setInt(Object object, int value) throws IllegalAccessException {
            fieldGetterAndSetter.setInt(getIndex(), object, value);
        }
    }
}

class NoReflectionInjectAnnotation implements Inject {
    @Override
    public Class<? extends Annotation> annotationType() {
        return Inject.class;
    }
}

class FieldGetterAndSetter {

    public void set(int index, Object object, Object value) throws IllegalAccessException {
        switch (index) {
            case 0: ((A)object).b = (B) value; break;
            case 1: ((A)object).c = (int) value; break;
            default: throw new IllegalAccessException("No setter for this index:" + index);
        }
    }

    public void setInt(int index, Object object, int value) throws IllegalAccessException {
        switch (index) {
            case 0: throw new IllegalAccessException("Not an int");
            case 1: ((A)object).c = value; break;
            default: throw new IllegalAccessException("No setter for this index:" + index);
        }
    }

    public Object get(int index, Object object) throws IllegalAccessException {
        switch (index) {
            case 0: return ((A)object).b;
            case 1: return ((A)object).c;
            default: throw new IllegalAccessException("Not an Object");
        }
    }

    public int getInt(int index, Object object) throws IllegalAccessException {
        switch (index) {
            case 0: throw new IllegalAccessException("Not an int");
            case 1: return ((A)object).c;
            default: throw new IllegalAccessException("Not an int");
        }
    }

}

