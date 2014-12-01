package org.reflection_no_reflection.integration_tests;

import javax.inject.Inject;

public class A {
    @Inject public B publicObjectField;
    @Inject public int publicPrimitiveIntField;
    @Inject public byte publicPrimitiveByteField;
    @Inject public short publicPrimitiveShortField;
    @Inject public long publicPrimitiveLongField;
    @Inject public float publicPrimitiveFloatField;
    @Inject public double publicPrimitiveDoubleField;
    @Inject public boolean publicPrimitiveBooleanField;
    @Inject public char publicPrimitiveCharField;

    @Inject public Integer publicWrapperIntegerField;

    @Inject private B privateObjectField;
    @Inject private int privatePrimitiveIntField;
    @Inject private byte privatePrimitiveByteField;
    @Inject private short privatePrimitiveShortField;
    @Inject private long privatePrimitiveLongField;
    @Inject private float privatePrimitiveFloatField;
    @Inject private double privatePrimitiveDoubleField;
    @Inject private boolean privatePrimitiveBooleanField;
    @Inject private char privatePrimitiveCharField;

    @Inject public static B publicStaticObjectField;
    @Inject private static B privateStaticObjectField;

    //to make all private fields accessible to tests 
    //we make them protected not to interfere with a call to a setter when weaving 
    // (which is not appropriate as we don't know what a setter does, we must generate our own setter)

    protected B getPrivateObjectField() {
        return privateObjectField;
    }

    protected void setPrivateObjectField(B privateObjectField) {
        this.privateObjectField = privateObjectField;
    }

    protected int getPrivatePrimitiveIntField() {
        return privatePrimitiveIntField;
    }

    protected void setPrivatePrimitiveIntField(int privatePrimitiveIntField) {
        this.privatePrimitiveIntField = privatePrimitiveIntField;
    }

    protected byte getPrivatePrimitiveByteField() {
        return privatePrimitiveByteField;
    }

    protected void setPrivatePrimitiveByteField(byte privatePrimitiveByteField) {
        this.privatePrimitiveByteField = privatePrimitiveByteField;
    }

    protected short getPrivatePrimitiveShortField() {
        return privatePrimitiveShortField;
    }

    protected void setPrivatePrimitiveShortField(short privatePrimitiveShortField) {
        this.privatePrimitiveShortField = privatePrimitiveShortField;
    }

    protected long getPrivatePrimitiveLongField() {
        return privatePrimitiveLongField;
    }

    protected void setPrivatePrimitiveLongField(long privatePrimitiveLongField) {
        this.privatePrimitiveLongField = privatePrimitiveLongField;
    }

    protected float getPrivatePrimitiveFloatField() {
        return privatePrimitiveFloatField;
    }

    protected void setPrivatePrimitiveFloatField(float privatePrimitiveFloatField) {
        this.privatePrimitiveFloatField = privatePrimitiveFloatField;
    }

    protected double getPrivatePrimitiveDoubleField() {
        return privatePrimitiveDoubleField;
    }

    protected void setPrivatePrimitiveDoubleField(double privatePrimitiveDoubleField) {
        this.privatePrimitiveDoubleField = privatePrimitiveDoubleField;
    }

    protected boolean isPrivatePrimitiveBooleanField() {
        return privatePrimitiveBooleanField;
    }

    protected void setPrivatePrimitiveBooleanField(boolean privatePrimitiveBooleanField) {
        this.privatePrimitiveBooleanField = privatePrimitiveBooleanField;
    }

    protected char getPrivatePrimitiveCharField() {
        return privatePrimitiveCharField;
    }

    protected void setPrivatePrimitiveCharField(char privatePrimitiveCharField) {
        this.privatePrimitiveCharField = privatePrimitiveCharField;
    }

    public static B getPrivateStaticObjectField() {
        return privateStaticObjectField;
    }

    public static void setPrivateStaticObjectField(B privateStaticObjectField) {
        A.privateStaticObjectField = privateStaticObjectField;
    }
}
