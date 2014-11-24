package com.groupon;

import com.google.inject.blender.ReflectionField;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by administrateur on 14-11-23.
 */
public class ReflectionFieldTest extends FieldTest{

    @Override
    public com.google.inject.blender.Field getInstance(Class<?> clazz, String fieldName) {
        switch (fieldName) {
            case "b" : return new ReflectionField(getOriginalFieldB());
            case "c" : return new ReflectionField(getOriginalFieldC());
        }
        throw new RuntimeException("No such field:"  + fieldName);
    }

}
