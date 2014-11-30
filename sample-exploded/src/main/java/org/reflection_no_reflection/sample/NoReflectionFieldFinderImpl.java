package org.reflection_no_reflection.sample;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.no_reflection.AnnotationDatabase;
import org.reflection_no_reflection.no_reflection.AnnotationDatabaseFinder;
import org.reflection_no_reflection.no_reflection.FieldGetterAndSetter;
import org.reflection_no_reflection.no_reflection.NoReflectionFieldFinder;

/**
 * A Proof of Concept for finding fields based on RNR.
 * @author SNI
 */
public class NoReflectionFieldFinderImpl extends NoReflectionFieldFinder {
    static {
        fieldGetterAndSetter = new FieldGetterAndSetterImpl();
    }

    public NoReflectionFieldFinderImpl(AnnotationDatabaseFinder annotationDatabaseFinder) {
        super(annotationDatabaseFinder);
    }

    public NoReflectionFieldFinderImpl(String[] annotationDatabasePackages) {
        super(annotationDatabasePackages);
    }

    @Override
    public List<Field> getAllFields(Class<? extends Annotation> annotationClass, Class clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        if (clazz == A.class) {
            List annotationList = new ArrayList<>();
            annotationList.add(new NoReflectionInjectAnnotation());
            Field fieldB = new AnnotationDatabase.LocalNoReflectionField(0, "b", A.class.getName(), B.class.getName(), Modifier.PUBLIC, annotationList);

            annotationList.add(new NoReflectionInjectAnnotation());
            Field fieldC = new AnnotationDatabase.LocalNoReflectionField(1, "c", A.class.getName(), int.class.getName(), 0, annotationList);
            fieldList.add(fieldB);
            fieldList.add(fieldC);
        }
        return fieldList;
    }
}

class NoReflectionInjectAnnotation implements Inject {
    @Override
    public Class<? extends Annotation> annotationType() {
        return Inject.class;
    }
}

class FieldGetterAndSetterImpl implements FieldGetterAndSetter {

    public void set(int index, Object object, Object value) throws IllegalAccessException {
        switch (index) {
            case 0:
                ((A) object).b = (B) value;
                break;
            case 1:
                ((A) object).c = (int) value;
                break;
            default:
                throw new IllegalAccessException("No setter for this index:" + index);
        }
    }

    public void setInt(int index, Object object, int value) throws IllegalAccessException {
        switch (index) {
            case 0:
                throw new IllegalAccessException("Not an int");
            case 1:
                ((A) object).c = value;
                break;
            default:
                throw new IllegalAccessException("No setter for this index:" + index);
        }
    }

    public Object get(int index, Object object) throws IllegalAccessException {
        switch (index) {
            case 0:
                return ((A) object).b;
            case 1:
                return ((A) object).c;
            default:
                throw new IllegalAccessException("Not an Object");
        }
    }

    public int getInt(int index, Object object) throws IllegalAccessException {
        switch (index) {
            case 0:
                throw new IllegalAccessException("Not an int");
            case 1:
                return ((A) object).c;
            default:
                throw new IllegalAccessException("Not an int");
        }
    }
}

