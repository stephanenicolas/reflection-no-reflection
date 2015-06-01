package org.reflection_no_reflection.processor;

import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Class;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class InnerClassTest extends AbstractRnRTest {

    @Test
    public void mapsSimpleAnnotatedInnerClass() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {", //
                       "@Deprecated", //
                       "public class Bar {}", //
                       "}" //
        );

        configureProcessor("java.lang.Deprecated");
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo.Bar")), is(true));
        assertThat(annotatedClasses.contains(Class.forName("test.Foo.Bar")), is(true));
        final Class deprecatedAnnotationClass = Class.forNameSafe("java.lang.Deprecated");
        assertThat(Class.forName("test.Foo.Bar").getAnnotation(deprecatedAnnotationClass), notNullValue());
    }

    @Test
    public void mapsStaticAnnotatedInnerClass() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {", //
                       "@Deprecated", //
                       "public static class Bar {}", //
                       "}" //
        );

        configureProcessor("java.lang.Deprecated");
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo.Bar")), is(true));
        assertThat(annotatedClasses.contains(Class.forName("test.Foo.Bar")), is(true));
        final Class deprecatedAnnotationClass = Class.forNameSafe("java.lang.Deprecated");
        assertThat(Class.forName("test.Foo.Bar").getAnnotation(deprecatedAnnotationClass), notNullValue());
    }

    @Test
    public void mapsPrivateAnnotatedInnerClass() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {", //
                       "@Deprecated", //
                       "private class Bar {}", //
                       "}" //
        );

        configureProcessor("java.lang.Deprecated");
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo.Bar")), is(true));
        assertThat(annotatedClasses.contains(Class.forName("test.Foo.Bar")), is(true));
        final Class deprecatedAnnotationClass = Class.forNameSafe("java.lang.Deprecated");
        assertThat(Class.forName("test.Foo.Bar").getAnnotation(deprecatedAnnotationClass), notNullValue());
    }

}
