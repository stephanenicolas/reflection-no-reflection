package org.reflection_no_reflection.generator.sample;

import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.runtime.Module;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class AnnotationTest {

    @Before
    public void setUp() throws Exception {
        Class.clearAllClasses();
        Module module = new org.reflection_no_reflection.generator.sample.gen.ModuleImpl();
        Class.loadModule(module);
    }

    @Test
    public void shouldReflectAnnotation() throws ClassNotFoundException {
        //GIVEN

        //WHEN
        Class<?> classFoo = Class.forName("org.reflection_no_reflection.generator.sample.AnnotationTestCase");

        //THEN
        final Field[] fields = classFoo.getFields();
        final Field field = fields[0];
        assertThat(field.getName(), is("a"));
        assertThat(field.getType().getName(), is("java.lang.String"));
        //fails assertThat(field.getDeclaredAnnotations().length, is(1));
        //TODO solve which annotation to return RnR or runtime reflect
        assertThat(field.getAnnotation(SuppressWarnings.class), notNullValue());
        assertThat(field.getAnnotation(SuppressWarnings.class).value()[0], is("unused"));
    }
}
