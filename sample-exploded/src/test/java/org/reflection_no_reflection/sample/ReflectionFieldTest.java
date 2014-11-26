package org.reflection_no_reflection.sample;

import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.reflection.ReflectionField;

/**
 * Created by administrateur on 14-11-23.
 */
public class ReflectionFieldTest extends FieldTest {

    @Override
    public Field getInstance(Class<?> clazz, String fieldName) {
        switch (fieldName) {
            case "b":
                return new ReflectionField(getOriginalFieldB());
            case "c":
                return new ReflectionField(getOriginalFieldC());
        }
        throw new RuntimeException("No such field:" + fieldName);
    }
}
