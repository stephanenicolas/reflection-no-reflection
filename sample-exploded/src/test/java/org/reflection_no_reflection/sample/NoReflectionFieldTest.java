package org.reflection_no_reflection.sample;

import javax.inject.Inject;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.no_reflection.AnnotationDatabaseFinder;

/**
 * Proof of Concept of RNR field testings.
 * @author SNI
 */
public class NoReflectionFieldTest extends FieldTest {

    @Override
    public Field getInstance(Class<?> clazz, String fieldName) {
        switch (fieldName) {
            case "b":
                return new NoReflectionFieldFinderImpl((AnnotationDatabaseFinder)null).getAllFields(Inject.class, A.class).get(0);
            case "c":
                return new NoReflectionFieldFinderImpl((AnnotationDatabaseFinder)null).getAllFields(Inject.class, A.class).get(1);
        }
        throw new RuntimeException("No such field " + fieldName);
    }
}
