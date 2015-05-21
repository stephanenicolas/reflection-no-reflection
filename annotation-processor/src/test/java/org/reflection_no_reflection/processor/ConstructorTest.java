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
import org.reflection_no_reflection.Constructor;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.truth0.Truth.ASSERT;

public class ConstructorTest {

    private static Processor processor;

    @Before
    public void setup() {
        processor = new Processor();
    }

    @Test
    public void mapsSimpleAnnotatedConstructor() throws ClassNotFoundException {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foo", Joiner.on('\n').join( //
                                                                                                  "package test;", //
                                                                                                  "public class Foo {",//
                                                                                                  "@Deprecated protected Foo() {}", //
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
        assertThat(aClass.getConstructors().size(), is(1));

        final Constructor Constructor = (Constructor) aClass.getConstructors().get(0);
        final Constructor expected = new Constructor(aClass, new Class[0], new Class[0], Modifier.PROTECTED);
        assertThat(Constructor, is(expected));
        assertThat(Constructor.getModifiers(), is(Modifier.PROTECTED));

        final Annotation[] annotations = Constructor.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(Constructor.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsSimpleAnnotatedConstructorWithParams() throws ClassNotFoundException {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foo", Joiner.on('\n').join( //
                                                                                                  "package test;", //
                                                                                                  "public class Foo {",//
                                                                                                  "@Deprecated protected Foo(String a) {}", //
                                                                                                  "}" //
        ));

        configureProcessor(new String[] {"java.lang.Deprecated"});
        ASSERT.about(javaSource())
            .that(source)
            .processedWith(rnrProcessors())
            .compilesWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClasses();
        assertThat(annotatedClasses.contains(new Class("test.Foo")), is(true));

        final Class expectedParamType = Class.forName("java.lang.String");
        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getConstructors().size(), is(1));

        final Constructor Constructor = (Constructor) aClass.getConstructors().get(0);
        final Constructor expected = new Constructor(aClass, new Class[] {expectedParamType}, new Class[0], Modifier.PROTECTED);
        assertThat(Constructor, is(expected));
        assertThat(Constructor.getModifiers(), is(Modifier.PROTECTED));

        final Annotation[] annotations = Constructor.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(Constructor.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));

        final Class<?>[] paramTypes = Constructor.getParameterTypes();
        assertThat(paramTypes.length, is(1));
        assertThat(paramTypes[0], is(expectedParamType));

        assertThat(Constructor.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsSimpleAnnotatedConstructorWithException() throws ClassNotFoundException {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foo", Joiner.on('\n').join( //
                                                                                                  "package test;", //
                                                                                                  "public class Foo {",//
                                                                                                  "@Deprecated protected Foo() throws Exception {throw new Exception(); }", //
                                                                                                  "}" //
        ));

        configureProcessor(new String[] {"java.lang.Deprecated"});
        ASSERT.about(javaSource())
            .that(source)
            .processedWith(rnrProcessors())
            .compilesWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClasses();
        assertThat(annotatedClasses.contains(new Class("test.Foo")), is(true));

        final Class expectedExceptionType = Class.forName("java.lang.Exception");
        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getConstructors().size(), is(1));

        final Constructor Constructor = (Constructor) aClass.getConstructors().get(0);
        final Constructor expected = new Constructor(aClass, new Class[0], new Class[] {expectedExceptionType}, Modifier.PROTECTED);
        assertThat(Constructor, is(expected));
        assertThat(Constructor.getModifiers(), is(Modifier.PROTECTED));

        final Annotation[] annotations = Constructor.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(Constructor.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));

        final Class<?>[] exceptionTypes = Constructor.getExceptionTypes();
        assertThat(exceptionTypes.length, is(1));
        assertThat(exceptionTypes[0], is(expectedExceptionType));

        assertThat(Constructor.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    private void configureProcessor(String[] annotations) {
        processor.setAnnotatedClasses(new HashSet<>(Arrays.asList(annotations)));
    }

    static Iterable<? extends javax.annotation.processing.Processor> rnrProcessors() {
        return Arrays.asList(processor);
    }
}
