package org.reflection_no_reflection.integration_tests;

import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.reflection.ReflectionField;

/**
 * Test for core java reflection fields.
 * Allows to get a benchmark for testing all the reflection field API.
 * @author SNI
 */
public class ReflectionFieldTest extends FieldTest {

    @Override
    public Field getField(Class<?> clazz, String fieldName) {
        switch (fieldName) {
            case "publicObjectField":
                return new ReflectionField(getTrueReflectionFieldPublicObjectField());
            case "publicPrimitiveIntField":
                return new ReflectionField(getTrueReflectionFieldPublicPrimitiveIntField());
            case "publicPrimitiveByteField":
                return new ReflectionField(getTrueReflectionFieldPublicPrimitiveByteField());
            case "publicPrimitiveShortField":
                return new ReflectionField(getTrueReflectionFieldPublicPrimitiveShortField());
            case "publicPrimitiveLongField":
                return new ReflectionField(getTrueReflectionFieldPublicPrimitiveLongField());
        }
        throw new RuntimeException("No such field:" + fieldName);
    }
}
