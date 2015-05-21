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
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Constructor;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.truth0.Truth.ASSERT;

public class ConstructorParameterTest {

    private static Processor processor;

    @Before
    public void setup() {
        processor = new Processor();
    }

    @Test
    public void mapsConstructorWithAnnotatedParams() throws ClassNotFoundException {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foo", Joiner.on('\n').join( //
                                                                                                  "package test;", //
                                                                                                  "public class Foo {",//
                                                                                                  "protected Foo(@Deprecated String a) {}", //
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

        //TODO test annotations of params
    }

    private void configureProcessor(String[] annotations) {
        processor.setAnnotatedClasses(new HashSet<>(Arrays.asList(annotations)));
    }

    static Iterable<? extends javax.annotation.processing.Processor> rnrProcessors() {
        return Arrays.asList(processor);
    }
}
