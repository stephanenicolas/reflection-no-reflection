package org.reflection_no_reflection.generator.sample;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import javax.tools.JavaFileObject;
import org.junit.Before;
import org.junit.Test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

public class SampleProcessorTest {

    protected javax.annotation.processing.Processor processor;
    protected JavaFileObject javaSourceCode;

    @Before
    public void setup() {
        processor = new SampleProcessor();
        javaSourceCode = null;
    }

    @Test
    public void test() {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@javax.inject.Inject private String s;", //
                       "@javax.inject.Inject private void m() {};", //
                       "@javax.inject.Inject private int n() {return 0;};", //
                       "@javax.inject.Inject private void p(java.util.List<String> s) {};", //
                       "}" //
        );

        assertJavaSourceCompileWithoutError();
    }

    protected void javaSourceCode(String fullyQualifiedName, String... source) {
        javaSourceCode = JavaFileObjects.forSourceString(fullyQualifiedName, Joiner.on('\n').join(source));
    }

    protected Iterable<? extends javax.annotation.processing.Processor> rnrProcessors() {
        return Arrays.asList(processor);
    }

    protected void assertJavaSourceCompileWithoutError() {
        ASSERT.about(javaSource())
            .that(javaSourceCode)
            .processedWith(rnrProcessors())
            .compilesWithoutError();
    }
}
