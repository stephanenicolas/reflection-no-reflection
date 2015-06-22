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

        final Set<Class> annotatedClasses = getProcessedClasses();
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

        final Set<Class> annotatedClasses = getProcessedClasses();
        final Class clazz = Class.forName("test.Foo");
        assertThat(annotatedClasses.contains(clazz), is(true));
        assertThat(annotatedClasses.contains(Class.forName("test.Foo")), is(true));
        final Class deprecatedAnnotationClass = Class.forNameSafe("java.lang.Deprecated");
        assertThat(Class.forName("test.Foo").getAnnotation(deprecatedAnnotationClass), notNullValue());
        assertThat(((Annotation) Class.forName("test.Foo").getAnnotation(deprecatedAnnotationClass)).rnrAnnotationType().isAnnotation(), is(true));

        final java.lang.annotation.Annotation[] annotations = clazz.getAnnotations();
        assertThat(annotations.length, is(2));

        final Class suppressWarningsAnnotationClass = Class.forName("java.lang.SuppressWarnings");
        assertThat(((Annotation) clazz.getAnnotation(suppressWarningsAnnotationClass)).rnrAnnotationType(), is(suppressWarningsAnnotationClass));
        assertThat(((Annotation) clazz.getAnnotation(suppressWarningsAnnotationClass)).getMethod("value").getReturnType(), is((Class) Class.forName("java.lang.String[]")));
        assertThat(((Annotation) clazz.getAnnotation(suppressWarningsAnnotationClass)).rnrAnnotationType().isAnnotation(), is(true));

        final Object value = ((Annotation) clazz.getAnnotation(suppressWarningsAnnotationClass)).getValue("value");
        assertThat((String) value, is("foo"));
    }
}
