package org.reflection_no_reflection.generator.sample;

import javax.inject.Inject;

public class FieldTestCase {
    @Inject public String a;
    @Inject public Foo foo;
    @Inject public byte c;
    @Inject public short d;
    @Inject public int e;
    @Inject public long f;
    @Inject public float g;
    @Inject public double h;
    @Inject public char i;
    @Inject public boolean j;

    @Inject public String[] k;

    //TODO generics, arrays 1&2D, static
    //oosh final
    //private
}
