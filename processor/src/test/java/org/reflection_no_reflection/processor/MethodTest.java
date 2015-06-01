package org.reflection_no_reflection.processor;

import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MethodTest extends AbstractRnRTest {
    @Test
    public void mapsSimpleAnnotatedMethod() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated protected String s() {return \"foo\"; }", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getMethods().size(), is(1));

        final Method method = (Method) aClass.getMethods().get(0);
        final Method expected = new Method(aClass, "s", new Class[0], Class.forNameSafe("java.lang.String"), new Class[0], Modifier.PROTECTED);
        assertThat(method, is(expected));
        assertThat(method.getModifiers(), is(Modifier.PROTECTED));

        final Annotation[] annotations = method.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(method.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsSimpleAnnotatedMethodWithParams() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated protected String s(String a) {return a; }", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class expectedParamType = Class.forName("java.lang.String");
        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getMethods().size(), is(1));

        final Method method = (Method) aClass.getMethods().get(0);
        final Method expected = new Method(aClass, "s", new Class[] {expectedParamType}, Class.forNameSafe("java.lang.String"), new Class[0], Modifier.PROTECTED);
        assertThat(method, is(expected));
        assertThat(method.getModifiers(), is(Modifier.PROTECTED));

        final Annotation[] annotations = method.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(method.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));

        final Class<?>[] paramTypes = method.getParameterTypes();
        assertThat(paramTypes.length, is(1));
        assertThat(paramTypes[0], is(expectedParamType));

        assertThat(method.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsSimpleAnnotatedMethodWithException() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated protected String s() throws Exception {throw new Exception(); }", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class expectedExceptionType = Class.forName("java.lang.Exception");
        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getMethods().size(), is(1));

        final Method method = (Method) aClass.getMethods().get(0);
        final Method expected = new Method(aClass, "s", new Class[0], Class.forNameSafe("java.lang.String"), new Class[] {expectedExceptionType}, Modifier.PROTECTED);
        assertThat(method, is(expected));
        assertThat(method.getModifiers(), is(Modifier.PROTECTED));

        final Annotation[] annotations = method.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(method.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));

        final Class<?>[] exceptionTypes = method.getExceptionTypes();
        assertThat(exceptionTypes.length, is(1));
        assertThat(exceptionTypes[0], is(expectedExceptionType));

        assertThat(method.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }
}
