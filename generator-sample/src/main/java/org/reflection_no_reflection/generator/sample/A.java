package org.reflection_no_reflection.generator.sample;

import javax.inject.Inject;

public class A {
    @Inject public B b;
    @Inject public byte c;
    @Inject public short d;
    @Inject public int e;
    @Inject public long f;
    @Inject public float g;
    @Inject public double h;
    @Inject public char i;
    @Inject public boolean j;

    @Inject public void m() {e=3;}
}
