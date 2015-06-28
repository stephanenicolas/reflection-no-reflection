package org.reflection_no_reflection.processor;

import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Constructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ConstructorTest extends AbstractRnRTest {

    @Test
    public void mapsSimpleAnnotatedConstructor() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated protected Foo() {}", //
                       "}" //
        );

        setTargetAnnotations(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = getProcessedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getConstructors().size(), is(1));

        final Constructor constructor = (Constructor) aClass.getConstructors().get(0);
        final Constructor expected = new Constructor(aClass, new Class[0], new Class[0], Modifier.PROTECTED);
        assertThat(constructor, is(expected));
        assertThat(constructor.getModifiers(), is(Modifier.PROTECTED));

        final java.lang.annotation.Annotation[] annotations = constructor.getAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(((Annotation) annotations[0]).rnrAnnotationType(), is(deprecatedAnnotationClass));
        assertThat(((Annotation)constructor.getAnnotation(deprecatedAnnotationClass)).rnrAnnotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsSimpleAnnotatedConstructorWithParams() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated protected Foo(String a) {}", //
                       "}" //
        );

        setTargetAnnotations(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = getProcessedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class expectedParamType = Class.forName("java.lang.String");
        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getConstructors().size(), is(1));

        final Constructor constructor = (Constructor) aClass.getConstructors().get(0);
        final Constructor expected = new Constructor(aClass, new Class[] {expectedParamType}, new Class[0], Modifier.PROTECTED);
        assertThat(constructor, is(expected));
        assertThat(constructor.getModifiers(), is(Modifier.PROTECTED));

        final java.lang.annotation.Annotation[] annotations = constructor.getAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(((Annotation) annotations[0]).rnrAnnotationType(), is(deprecatedAnnotationClass));
        assertThat(((Annotation)constructor.getAnnotation(deprecatedAnnotationClass)).rnrAnnotationType(), is(deprecatedAnnotationClass));

        final Class<?>[] paramTypes = constructor.getParameterTypes();
        assertThat(paramTypes.length, is(1));
        assertThat(paramTypes[0], is(expectedParamType));

        assertThat(((Annotation)constructor.getAnnotation(deprecatedAnnotationClass)).rnrAnnotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsSimpleAnnotatedConstructorWithException() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated protected Foo() throws Exception {throw new Exception(); }", //
                       "}" //
        );

        setTargetAnnotations(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = getProcessedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class expectedExceptionType = Class.forName("java.lang.Exception");
        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getConstructors().size(), is(1));

        final Constructor constructor = (Constructor) aClass.getConstructors().get(0);
        final Constructor expected = new Constructor(aClass, new Class[0], new Class[] {expectedExceptionType}, Modifier.PROTECTED);
        assertThat(constructor, is(expected));
        assertThat(constructor.getModifiers(), is(Modifier.PROTECTED));

        final java.lang.annotation.Annotation[] annotations = constructor.getAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(((Annotation) annotations[0]).rnrAnnotationType(), is(deprecatedAnnotationClass));
        assertThat(((Annotation)constructor.getAnnotation(deprecatedAnnotationClass)).rnrAnnotationType(), is(deprecatedAnnotationClass));

        final Class<?>[] exceptionTypes = constructor.getExceptionTypes();
        assertThat(exceptionTypes.length, is(1));
        assertThat(exceptionTypes[0], is(expectedExceptionType));

        assertThat(((Annotation)constructor.getAnnotation(deprecatedAnnotationClass)).rnrAnnotationType(), is(deprecatedAnnotationClass));
    }
}
