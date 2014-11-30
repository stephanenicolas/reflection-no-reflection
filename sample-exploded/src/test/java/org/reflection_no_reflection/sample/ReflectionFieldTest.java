package org.reflection_no_reflection.sample;

import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.reflection.ReflectionField;

/**
 * A proof of concept to retrieve core java reflection fields
 * and test them via the RNR framework.
 * @author SNI
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
