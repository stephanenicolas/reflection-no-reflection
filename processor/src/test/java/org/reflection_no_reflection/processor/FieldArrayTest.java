package org.reflection_no_reflection.processor;

import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FieldArrayTest extends AbstractRnRTest {

    @Test
    public void mapsAnnotatedObjectArrayField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private String[] s;", //
                       "}" //
        );

        setTargetAnnotations(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = getProcessedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("s", Class.forName("java.lang.String[]"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("java.lang.String[]")));
        assertThat(aField.getType().isArray(), is(true));
        assertThat(aField.getType().getComponentType(), is((Class) Class.forName("java.lang.String")));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = (Annotation[]) aField.getAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].rnrAnnotationType(), is(deprecatedAnnotationClass));
        assertThat(((Annotation) aField.getAnnotation(deprecatedAnnotationClass)).rnrAnnotationType(), is(deprecatedAnnotationClass));

    }

    @Test
    public void mapsAnnotatedPrimitiveArrayField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private int[] s;", //
                       "}" //
        );

        setTargetAnnotations(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = getProcessedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("s", Class.forName("int[]"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("int[]")));
        assertThat(aField.getType().isArray(), is(true));
        assertThat(aField.getType().getComponentType(), is((Class) Class.forName("int")));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = (Annotation[]) aField.getAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].rnrAnnotationType(), is(deprecatedAnnotationClass));
        assertThat(((Annotation) aField.getAnnotation(deprecatedAnnotationClass)).rnrAnnotationType(), is(deprecatedAnnotationClass));
    }
}
