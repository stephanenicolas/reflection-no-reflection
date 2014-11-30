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
}
