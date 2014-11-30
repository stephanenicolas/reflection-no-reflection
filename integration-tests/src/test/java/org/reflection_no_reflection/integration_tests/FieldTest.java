package org.reflection_no_reflection.integration_tests;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import javax.inject.Inject;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.reflection_no_reflection.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Base class of both all RNR fields implementations.
 * @author SNI.
 */
public abstract class FieldTest {

    private java.lang.reflect.Field trueReflectionFieldPublicObjectField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveIntField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveByteField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveShortField;
    private java.lang.reflect.Field trueReflectionFieldPublicPrimitiveLongField;
    private Field publicObjectField;
    private Field publicPrimitiveIntField;
    private Field publicPrimitiveByteField;
    private Field publicPrimitiveShortField;
    private Field publicPrimitiveLongField;

    @Before
    public void setUp() throws NoSuchFieldException {
        trueReflectionFieldPublicObjectField = A.class.getDeclaredField("publicObjectField");
        trueReflectionFieldPublicPrimitiveIntField = A.class.getDeclaredField("publicPrimitiveIntField");
        trueReflectionFieldPublicPrimitiveByteField = A.class.getDeclaredField("publicPrimitiveByteField");
        trueReflectionFieldPublicPrimitiveShortField = A.class.getDeclaredField("publicPrimitiveShortField");
        trueReflectionFieldPublicPrimitiveLongField = A.class.getDeclaredField("publicPrimitiveLongField");
        publicObjectField = getField(A.class, "publicObjectField");
        publicPrimitiveIntField = getField(A.class, "publicPrimitiveIntField");
        publicPrimitiveByteField = getField(A.class, "publicPrimitiveByteField");
        publicPrimitiveShortField = getField(A.class, "publicPrimitiveShortField");
        publicPrimitiveLongField = getField(A.class, "publicPrimitiveLongField");
    }

    public abstract Field getField(Class<?> clazz, String fieldName);

    public java.lang.reflect.Field getTrueReflectionFieldPublicObjectField() {
        return trueReflectionFieldPublicObjectField;
    }

    public java.lang.reflect.Field getTrueReflectionFieldPublicPrimitiveIntField() {
        return trueReflectionFieldPublicPrimitiveIntField;
    }

    public java.lang.reflect.Field getTrueReflectionFieldPublicPrimitiveByteField() {
        return trueReflectionFieldPublicPrimitiveByteField;
    }

    public java.lang.reflect.Field getTrueReflectionFieldPublicPrimitiveShortField() {
        return trueReflectionFieldPublicPrimitiveShortField;
    }

    public java.lang.reflect.Field getTrueReflectionFieldPublicPrimitiveLongField() {
        return trueReflectionFieldPublicPrimitiveLongField;
    }

    @Test
    public void testGetName() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        String fieldName = publicObjectField.getName();

        //THEN
        assertThat(fieldName, is(trueReflectionFieldPublicObjectField.getName()));
        assertThat(fieldName, is("publicObjectField"));
    }

    @Test
    public void testGetType() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Class fieldType = publicObjectField.getType();

        //THEN
        assertThat(fieldType, Is.<Class>is(trueReflectionFieldPublicObjectField.getType()));
        assertThat(fieldType, Is.<Class>is(B.class));
    }

    @Test
    public void testGetGenericType() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Type type = publicObjectField.getGenericType();

        //THEN
        assertThat(type, Is.<Type>is(trueReflectionFieldPublicObjectField.getType()));
        assertThat(type, Is.<Type>is(B.class));
    }

    @Test
    public void testGetDeclaringClass() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Class fieldType = publicObjectField.getDeclaringClass();

        //THEN
        assertThat(fieldType, Is.<Class>is(trueReflectionFieldPublicObjectField.getDeclaringClass()));
        assertThat(fieldType, Is.<Class>is(A.class));
    }

    @Test
    public void testGetModifiers() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        int fieldModifiers = publicObjectField.getModifiers();

        //THEN
        assertThat(fieldModifiers, is(trueReflectionFieldPublicObjectField.getModifiers()));
        assertThat(fieldModifiers, is(Modifier.PUBLIC));
    }

    @Test
    public void testGetAnnotationInject() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Inject fieldAnnotationInject = publicObjectField.getAnnotation(Inject.class);

        //THEN
        assertThat(fieldAnnotationInject, Is.<Inject>isA(Inject.class));
    }

    @Test
    public void testGetAnnotations() throws NoSuchFieldException {
        //GIVEN

        //WHEN
        Annotation[] annotations = publicObjectField.getDeclaredAnnotations();

        //THEN
        assertThat(annotations.length, is(1));
        assertThat((Inject) annotations[0], Is.<Inject>isA(Inject.class));
    }

    @Test
    public void testIsAnnotationPresent() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicObjectField = new B();
        boolean isInjectpresent = publicObjectField.isAnnotationPresent(Inject.class);

        //THEN
        assertThat(isInjectpresent, Is.is(trueReflectionFieldPublicObjectField.isAnnotationPresent(Inject.class)));
        assertThat(isInjectpresent, Is.is(true));
    }

    @Test
    public void testSet() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        B newB = new B();
        A a = new A();
        publicObjectField.set(a, newB);

        //THEN
        assertThat(a.publicObjectField, Is.<B>is((B) trueReflectionFieldPublicObjectField.get(a)));
        assertThat(a.publicObjectField, Is.<B>is(newB));
    }

    @Test
    public void testGet() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicObjectField = new B();
        B b = (B) publicObjectField.get(a);

        //THEN
        assertThat(b, Is.<B>is((B) trueReflectionFieldPublicObjectField.get(a)));
        assertThat(b, Is.<B>is(a.publicObjectField));
    }

    @Test
    public void testGetInt() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveIntField = 5;
        int b = publicPrimitiveIntField.getInt(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveIntField.getInt(a)));
        assertThat(b, Is.is(a.publicPrimitiveIntField));
    }

    @Test
    public void testGetByte() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveByteField = 5;
        byte b = publicPrimitiveByteField.getByte(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveByteField.getByte(a)));
        assertThat(b, Is.is(a.publicPrimitiveByteField));
    }

    @Test
    public void testGetShort() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveShortField = 5;
        short b = publicPrimitiveShortField.getShort(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveShortField.getShort(a)));
        assertThat(b, Is.is(a.publicPrimitiveShortField));
    }

    @Test
    public void testGetLong() throws NoSuchFieldException, IllegalAccessException {
        //GIVEN

        //WHEN
        A a = new A();
        a.publicPrimitiveLongField = 5;
        long b = publicPrimitiveLongField.getLong(a);

        //THEN
        assertThat(b, Is.is(trueReflectionFieldPublicPrimitiveLongField.getLong(a)));
        assertThat(b, Is.is(a.publicPrimitiveLongField));
    }
}
