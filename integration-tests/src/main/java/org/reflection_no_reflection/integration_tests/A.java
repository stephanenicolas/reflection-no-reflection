package org.reflection_no_reflection.integration_tests;

import javax.inject.Inject;

public class A {
    @Inject public B publicObjectField;
    @Inject public int publicPrimitiveIntField;
    @Inject public byte publicPrimitiveByteField;
}
