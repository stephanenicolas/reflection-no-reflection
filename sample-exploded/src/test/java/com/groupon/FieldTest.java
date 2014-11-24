package com.groupon;

import com.google.inject.blender.Field;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import javax.inject.Inject;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by administrateur on 14-11-23.
 */
public abstract class FieldTest {

    private java.lang.reflect.Field originalFieldB;
    private java.lang.reflect.Field originalFieldC;
    private Field fieldB;
    private Field fieldC;

    @Before
    public void setUp() throws NoSuchFieldException {
        originalFieldB = A.class.getDeclaredField("b");
        fieldB = getInstance(A.class, "b");
        originalFieldC = A.class.getDeclaredField("c");
        fieldC = getInstance(A.class, "c");
    }

    public abstract Field getInstance(Class<?> clazz, String fieldName);

    public java.lang.reflect.Field getOriginalFieldB() {
        return originalFieldB;
    }

    public java.lang.reflect.Field getOriginalFieldC() {
        return originalFieldC;
    }

    @Test
    public void testGetName() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        String fieldName = fieldB.getName();

        //THEN
        assertThat(fieldName, is(originalFieldB.getName()));
        assertThat(fieldName, is("b"));
    }

    @Test
    public void testGetType() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Class fieldType = fieldB.getType();

        //THEN
        assertThat(fieldType, Is.<Class>is(originalFieldB.getType()));
        assertThat(fieldType, Is.<Class>is(B.class));
    }

    @Test
    public void testGetGenericType() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Type type = fieldB.getGenericType();

        //THEN
        assertThat(type, Is.<Type>is(originalFieldB.getType()));
        assertThat(type, Is.<Type>is(B.class));
    }

    @Test
    public void testGetDeclaringClass() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Class fieldType = fieldB.getDeclaringClass();

        //THEN
        assertThat(fieldType, Is.<Class>is(originalFieldB.getDeclaringClass()));
        assertThat(fieldType, Is.<Class>is(A.class));
    }

    @Test
    public void testGetModifiers() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        int fieldModifiers = fieldB.getModifiers();

        //THEN
        assertThat(fieldModifiers, is(originalFieldB.getModifiers()));
        assertThat(fieldModifiers, is(Modifier.PUBLIC));
    }

    @Test
    public void testGetAnnotationInject() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Inject fieldAnnotationInject = fieldB.getAnnotation(Inject.class);

        //THEN
        assertThat(fieldAnnotationInject, Is.<Inject>isA(Inject.class));
    }

    @Test
    public void testGetAnnotations() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Annotation[] annotations = fieldB.getDeclaredAnnotations();

        //THEN
        assertThat(annotations.length, is(1));
        assertThat((Inject) annotations[0], Is.<Inject>isA(Inject.class));
    }

    @Test
    public void testIsAnnotationPresent() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.b = new B();
        boolean isInjectpresent = fieldB.isAnnotationPresent(Inject.class);

        //THEN
        assertThat(isInjectpresent, Is.is(originalFieldB.isAnnotationPresent(Inject.class)));
        assertThat(isInjectpresent, Is.is(true));
    }

    @Test
    public void testSet() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        B newB = new B();
        A a = new A();
        fieldB.set(a, newB);

        //THEN
        assertThat(a.b, Is.<B>is((B) originalFieldB.get(a)));
        assertThat(a.b, Is.<B>is(newB));
    }

    @Test
    public void testGet() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.b = new B();
        B b = (B) fieldB.get(a);

        //THEN
        assertThat(b, Is.<B>is((B) originalFieldB.get(a)));
        assertThat(b, Is.<B>is(a.b));
    }

    @Test
    public void testGetInt() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.c = 5;
        int b = (int) fieldC.getInt(a);

        //THEN
        assertThat(b, Is.is(originalFieldC.getInt(a)));
        assertThat(b, Is.is(a.c));
    }
}
