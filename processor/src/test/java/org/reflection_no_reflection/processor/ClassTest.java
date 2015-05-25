package org.reflection_no_reflection.processor;

import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ClassTest extends AbstractRnRTest {

    @Test
    public void mapsSimpleAnnotatedClass() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "@Deprecated", //
                       "public class Foo {}" //
        );

        configureProcessor("java.lang.Deprecated");
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getTargetAnnotatedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));
        assertThat(annotatedClasses.contains(Class.forName("test.Foo")), is(true));
        final Class deprecatedAnnotationClass = Class.forNameSafe("java.lang.Deprecated");
        assertThat(Class.forName("test.Foo").getAnnotation(deprecatedAnnotationClass), notNullValue());
    }

    @Test
    public void mapsAnnotatedClassWithOtherAnnotations() throws ClassNotFoundException, NoSuchMethodException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "@SuppressWarnings(\"foo\")", //
                       "@Deprecated", //
                       "public class Foo {}" //
        );

        configureProcessor("java.lang.Deprecated");
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getTargetAnnotatedClasses();
        final Class clazz = Class.forName("test.Foo");
        assertThat(annotatedClasses.contains(clazz), is(true));
        assertThat(annotatedClasses.contains(Class.forName("test.Foo")), is(true));
        final Class deprecatedAnnotationClass = Class.forNameSafe("java.lang.Deprecated");
        assertThat(Class.forName("test.Foo").getAnnotation(deprecatedAnnotationClass), notNullValue());

        final Annotation[] annotations = clazz.getAnnotations();
        assertThat(annotations.length, is(2));

        final Class suppressWarningsAnnotationClass = Class.forName("java.lang.SuppressWarnings");
        assertThat(clazz.getAnnotation(suppressWarningsAnnotationClass).annotationType(), is(suppressWarningsAnnotationClass));
        assertThat(clazz.getAnnotation(suppressWarningsAnnotationClass).getMethod("value").getReturnType(), is((Class) Class.forName("java.lang.String[]")));

        final Object value = clazz.getAnnotation(suppressWarningsAnnotationClass).getValue("value");
        assertThat((String) value, is("foo"));
    }
}
