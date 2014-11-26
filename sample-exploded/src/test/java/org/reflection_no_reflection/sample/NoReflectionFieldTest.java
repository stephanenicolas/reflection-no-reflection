package org.reflection_no_reflection.sample;

import javax.inject.Inject;
import org.reflection_no_reflection.Field;

/**
 * Created by administrateur on 14-11-23.
 */
public class NoReflectionFieldTest extends FieldTest {

    @Override
    public Field getInstance(Class<?> clazz, String fieldName) {
        switch (fieldName) {
            case "b":
                return new NoReflectionFieldFinderImpl().getAllFields(Inject.class, A.class).get(0);
            case "c":
                return new NoReflectionFieldFinderImpl().getAllFields(Inject.class, A.class).get(1);
        }
        throw new RuntimeException("No such field " + fieldName);
    }
}
