package org.reflection_no_reflection.processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.tools.JavaFileObject;
import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.truth0.Truth.ASSERT;

public class ProcessorTest {

    private static Processor processor;

    @Before
    public void setup() {
        processor = new Processor();
    }

    @Test
    public void mapsSimpleAnnotatedClass() throws ClassNotFoundException {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foo", Joiner.on('\n').join( //
                                                                                                  "package test;", //
                                                                                                  "@Deprecated", //
                                                                                                  "public class Foo {}" //
        ));

        configureProcessor(new String[] {"java.lang.Deprecated"});
        ASSERT.about(javaSource())
            .that(source)
            .processedWith(rnrProcessors())
            .compilesWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClasses();
        assertThat(annotatedClasses.contains(new Class("test.Foo")), is(true));
        assertThat(annotatedClasses.contains(Class.forName("test.Foo")), is(true));
    }

    @Test
    public void mapsSimpleAnnotatedField() throws ClassNotFoundException {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foo", Joiner.on('\n').join( //
                                                                                                  "package test;", //
                                                                                                  "public class Foo {",//
                                                                                                  "@Deprecated private String s;", //
                                                                                                  "}" //
        ));

        configureProcessor(new String[] {"java.lang.Deprecated"});
        ASSERT.about(javaSource())
            .that(source)
            .processedWith(rnrProcessors())
            .compilesWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClasses();
        assertThat(annotatedClasses.contains(new Class("test.Foo")), is(true));
        final Class<?> aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));
        final Field aField = aClass.getFields()[0];
        assertThat(aField.getName(), is("s"));
        assertThat(aField.getType(), is((Class) Class.forName("java.lang.String")));
        assertThat(aField.getDeclaringClass(), is((Class) aClass));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));
        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));
        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    @SuppressWarnings("foo")
    public void mapsAnnotatedFieldWithParams() throws ClassNotFoundException {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foo", Joiner.on('\n').join( //
                                                                                                  "package test;", //
                                                                                                  "public class Foo {",//
                                                                                                  "@SuppressWarnings(\"foo\") private String s;", //
                                                                                                  "}" //
        ));

        configureProcessor(new String[] {"java.lang.SuppressWarnings"});
        ASSERT.about(javaSource())
            .that(source)
            .processedWith(rnrProcessors())
            .compilesWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClasses();
        assertThat(annotatedClasses.contains(new Class("test.Foo")), is(true));
        final Class<?> aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));
        final Field aField = aClass.getFields()[0];
        assertThat(aField.getName(), is("s"));
        assertThat(aField.getType(), is((Class) Class.forName("java.lang.String")));
        assertThat(aField.getDeclaringClass(), is((Class) aClass));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));
        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));
        final Class deprecatedAnnotationClass = Class.forName("java.lang.SuppressWarnings");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).getMapNameMethodToMethod().get("value"), notNullValue());
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).getMapNameMethodToMethod().get("value").getReturnType(), is((Class) Class.forName("java.lang.String[]")));
        assertThat((String) ((com.sun.tools.javac.code.Attribute.Constant) ((List) aField.getAnnotation(deprecatedAnnotationClass).getMapMethodToValue().get("value")).get(0)).getValue(), is("foo"));
    }

    private void configureProcessor(String[] annotations) {
        processor.setAnnotatedClasses(new HashSet<>(Arrays.asList(annotations)));
    }

    static Iterable<? extends javax.annotation.processing.Processor> rnrProcessors() {
        return Arrays.asList(processor);
    }
}
