package org.reflection_no_reflection.processor;

import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Constructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ConstructorParameterTest extends AbstractRnRTest {

    @Test
    public void mapsConstructorWithAnnotatedParams() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "protected Foo(@Deprecated String a) {}", //
                       "}" //
        );

        setTargetAnnotations(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = getProcessedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class expectedParamType = Class.forName("java.lang.String");
        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getConstructors().size(), is(1));

        final Constructor Constructor = (Constructor) aClass.getConstructors().get(0);
        final Constructor expected = new Constructor(aClass, new Class[] {expectedParamType}, new Class[0], Modifier.PROTECTED);
        assertThat(Constructor, is(expected));
        assertThat(Constructor.getModifiers(), is(Modifier.PROTECTED));

        //TODO test annotations of params
    }
}
