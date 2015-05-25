package org.reflection_no_reflection.processor;

import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Class;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class InterfaceTest extends AbstractRnRTest {

    @Test
    public void mapsSimpleAnnotatedClass() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "@Deprecated", //
                       "public interface Foo {}" //
        );

        configureProcessor("java.lang.Deprecated");
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getTargetAnnotatedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));
        assertThat(annotatedClasses.contains(Class.forName("test.Foo")), is(true));
        assertThat(Class.forName("test.Foo").isInterface(), is(true));
    }
}
