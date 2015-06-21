package org.reflection_no_reflection.processor;

import java.lang.reflect.Modifier;
import java.util.Set;
import org.junit.Test;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Class;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.GenericDeclaration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class GenericsTest extends AbstractRnRTest {

    @Test
    public void mapsSimpleAnnotatedGenericField() throws ClassNotFoundException {
        javaSourceCode("test.Foo", //
                       "package test;", //
                       "import java.util.List;", //
                       "public class Foo {",//
                       "@Deprecated private List<String> s;", //
                       "}" //
        );

        configureProcessor(new String[] {"java.lang.Deprecated"});
        assertJavaSourceCompileWithoutError();

        final Set<Class> annotatedClasses = getProcessedClasses();
        assertThat(annotatedClasses.contains(Class.forNameSafe("test.Foo")), is(true));

        final Class aClass = Class.forName("test.Foo");
        assertThat(aClass.getFields().length, is(1));

        final Field aField = aClass.getFields()[0];
        final Field expected = new Field("s", Class.forName("java.util.List"), aClass, Modifier.PRIVATE, null);
        assertThat(aField, is(expected));
        assertThat(aField.getType(), is((Class) Class.forName("java.util.List")));
        final GenericDeclaration genericInfo = aField.getType().getGenericInfo();
        assertThat(genericInfo, notNullValue());
        assertThat(genericInfo.getTypeParameters().length, is(1));
        assertThat(genericInfo.getTypeParameters()[0].getName(), is("java.lang.String"));

        assertThat(aField.getModifiers(), is(Modifier.PRIVATE));

        final Annotation[] annotations = aField.getAnnotations();
        assertThat(annotations.length, is(1));

        final Class deprecatedAnnotationClass = Class.forName("java.lang.Deprecated");
        assertThat(annotations[0].annotationType(), is(deprecatedAnnotationClass));
        assertThat(aField.getAnnotation(deprecatedAnnotationClass).annotationType(), is(deprecatedAnnotationClass));
    }
}
