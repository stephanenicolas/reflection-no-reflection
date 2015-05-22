package org.reflection_no_reflection.processor;

import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MethodParameterTest extends AbstractRnRTest {

    @Test
    public void mapsMethodWithAnnotatedParams() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "protected String s(@Deprecated String a) {return a; }", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class expectedParamType = Class.forName("java.lang.String");
        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getMethods().size(), is(1));

        final Method method = (Method) aClass.getMethods().get(0);
        final Method expected = new Method(aClass, "s", new Class[] {expectedParamType}, Class.forNameSafe("java.lang.String"), new Class[0], Modifier.PROTECTED);
        assertThat(method, is(expected));
        assertThat(method.getModifiers(), is(Modifier.PROTECTED));

        //TODO test annotations of params
    }
}
