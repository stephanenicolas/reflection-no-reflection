package org.reflection_no_reflection.processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.tools.JavaFileObject;
import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
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
        final Field expected = new Field("s", Class.forName("java.lang.String"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("java.lang.String")));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.SuppressWarnings");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).getMethod("value").getReturnType(), is((Class) Class.forName("java.lang.String[]")));

        final Object value = aField.getAnnotation(deprecatedAnnotationClass).getValue("value");
        assertThat((String) value, is("foo"));
    }

    @Test
    public void mapsSimpleAnnotatedMethod() throws ClassNotFoundException {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foo", Joiner.on('\n').join( //
                                                                                                  "package test;", //
                                                                                                  "public class Foo {",//
                                                                                                  "@Deprecated protected String s() {return \"foo\"; }", //
                                                                                                  "}" //
        ));

        configureProcessor(new String[] {"java.lang.Deprecated"});
        ASSERT.about(javaSource())
            .that(source)
            .processedWith(rnrProcessors())
            .compilesWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClasses();
        assertThat(annotatedClasses.contains(new Class("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getMethods().size(), is(1));

        final Method method = (Method) aClass.getMethods().get(0);
        final Method expected = new Method(aClass, "s", new Class[0], new Class("java.lang.String"), new Class[0], Modifier.PROTECTED);
        assertThat(method, is(expected));
        assertThat(method.getModifiers(), is(Modifier.PROTECTED));

        final Annotation[] annotations = method.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(method.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    private void configureProcessor(String[] annotations) {
        processor.setAnnotatedClasses(new HashSet<>(Arrays.asList(annotations)));
    }

    static Iterable<? extends javax.annotation.processing.Processor> rnrProcessors() {
        return Arrays.asList(processor);
    }
}
