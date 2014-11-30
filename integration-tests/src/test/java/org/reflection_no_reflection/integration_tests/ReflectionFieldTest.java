package org.reflection_no_reflection.integration_tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.no_reflection.NoReflectionFieldFinder;
import org.reflection_no_reflection.reflection.ReflectionField;

/**
 * Test for core java reflection fields.
 * Allows to get a benchmark for testing all the reflection field API.
 * @author SNI
 */
public class ReflectionFieldTest extends FieldTest {

    @Override
    public Field getField(Class<?> clazz, String fieldName) {
        List<java.lang.reflect.Field> allFields = Arrays.asList(clazz.getFields());
        for (java.lang.reflect.Field field : allFields) {
            if( field.getName().equals(fieldName)) {
                return new ReflectionField(field);
            }
        }
        throw new RuntimeException("No such field " + fieldName);
    }
}
