package org.reflection_no_reflection.processor;

import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FieldTest extends AbstractRnRTest {

    @Test
    public void mapsSimpleAnnotatedField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private String s;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = getProcessedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("s", Class.forName("java.lang.String"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("java.lang.String")));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    @SuppressWarnings("foo")
    public void mapsAnnotatedFieldWithParams() throws ClassNotFoundException, NoSuchMethodException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@SuppressWarnings(\"foo\") private String s;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.SuppressWarnings"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = getProcessedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class<?> aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("s", Class.forName("java.lang.String"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("java.lang.String")));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class suppressWarningsAnnotationClass = Class.forName("java.lang.SuppressWarnings");
        assertThat(annotations[0].annotationType(), is(suppressWarningsAnnotationClass));
        assertThat(aField.getAnnotation(suppressWarningsAnnotationClass).annotationType(), is(suppressWarningsAnnotationClass));
        assertThat(aField.getAnnotation(suppressWarningsAnnotationClass).getMethod("value").getReturnType(), is((Class) Class.forName("java.lang.String[]")));

        final Object value = aField.getAnnotation(suppressWarningsAnnotationClass).getValue("value");
        assertThat((String) value, is("foo"));
    }
}
