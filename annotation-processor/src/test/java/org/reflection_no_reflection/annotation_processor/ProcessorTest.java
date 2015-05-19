package org.reflection_no_reflection.annotation_processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Class;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.truth0.Truth.ASSERT;

public class ProcessorTest {

    private static ReflectionNoReflectionAnnotationProcessor reflectionNoReflectionAnnotationProcessor;

    @Before
    public void setup() {
        reflectionNoReflectionAnnotationProcessor = new ReflectionNoReflectionAnnotationProcessor();
    }

    @Test
    public void mapsSimpleAnnotatedClass() {
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

        final Set<Class> annotatedClasses = reflectionNoReflectionAnnotationProcessor.getAnnotatedClasses();
        assertThat(annotatedClasses.contains(new Class("test.Foo")), is(true));
    }

    private void configureProcessor(String[] annotations) {
        reflectionNoReflectionAnnotationProcessor.setAnnotatedClasses(new HashSet<>(Arrays.asList(annotations)));
    }

    static Iterable<? extends Processor> rnrProcessors() {
        return Arrays.asList(reflectionNoReflectionAnnotationProcessor);
    }
}
