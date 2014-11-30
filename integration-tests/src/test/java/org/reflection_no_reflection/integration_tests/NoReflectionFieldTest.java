package org.reflection_no_reflection.integration_tests;

import java.util.Collection;
import javax.inject.Inject;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.no_reflection.NoReflectionFieldFinder;

/**
 * Test for RNR fields.
 * @author SNI
 */
public class NoReflectionFieldTest extends FieldTest {

    @Override
    public Field getField(Class<?> clazz, String fieldName) {
        Collection<Field> allFields = new NoReflectionFieldFinder(new String[] {""}).getAllFields(Inject.class, A.class);
        for (Field field : allFields) {
            if( field.getName().equals(fieldName)) {
                return field;
            }
        }
        throw new RuntimeException("No such field " + fieldName);
    }
}
