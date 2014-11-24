package org.reflection_no_reflection.sample;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.no_reflection.FieldGetterAndSetter;
import org.reflection_no_reflection.no_reflection.NoReflectionFieldFinder;

/**
 * Created by administrateur on 14-11-23.
 */
public class NoReflectionFieldFinderImpl extends NoReflectionFieldFinder {
    static {
        fieldGetterAndSetter = new FieldGetterAndSetterImpl();
    }

    @Override
    public List<Field> getAllFields(Class<? extends Annotation> annotationClass, Class clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        if (clazz == A.class) {
            HashMap<Class<? extends Annotation>, Annotation> mapAnnotation = new HashMap<Class<? extends Annotation>, Annotation>();
            mapAnnotation.put(Inject.class, new NoReflectionInjectAnnotation());
            Field fieldB = new LocalNoReflectionField(0, "b", A.class.getName(), B.class.getName(), Modifier.PUBLIC, mapAnnotation);

            mapAnnotation = new HashMap<Class<? extends Annotation>, Annotation>();
            mapAnnotation.put(Inject.class, new NoReflectionInjectAnnotation());
            Field fieldC = new LocalNoReflectionField(1, "c", A.class.getName(), int.class.getName(), 0, mapAnnotation);
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

