package org.reflection_no_reflection.processor;

import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FieldPrimitiveTest extends AbstractRnRTest {

    @Test
    public void mapsAnnotatedPrimitiveIntField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private int a;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("a", Class.forName("int"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("int")));
        assertThat(aField.getType().isPrimitive(), is(true));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsAnnotatedPrimitiveShortField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private short a;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("a", Class.forName("short"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("short")));
        assertThat(aField.getType().isPrimitive(), is(true));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsAnnotatedPrimitiveByteField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private byte a;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("a", Class.forName("byte"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("byte")));
        assertThat(aField.getType().isPrimitive(), is(true));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsAnnotatedPrimitiveLongField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private long a;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("a", Class.forName("long"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("long")));
        assertThat(aField.getType().isPrimitive(), is(true));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsAnnotatedPrimitiveBooleanField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private boolean a;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("a", Class.forName("boolean"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("boolean")));
        assertThat(aField.getType().isPrimitive(), is(true));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsAnnotatedPrimitiveCharField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private char a;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("a", Class.forName("char"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("char")));
        assertThat(aField.getType().isPrimitive(), is(true));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsAnnotatedPrimitiveDoubleField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private double a;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("a", Class.forName("double"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("double")));
        assertThat(aField.getType().isPrimitive(), is(true));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }

    @Test
    public void mapsAnnotatedPrimitiveFloatField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "public class Foo {",//
                       "@Deprecated private float a;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = processor.getAnnotatedClassSet();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("a", Class.forName("float"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("float")));
        assertThat(aField.getType().isPrimitive(), is(true));
        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getDeclaredAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }
}
