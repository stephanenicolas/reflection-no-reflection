package org.reflection_no_reflection.generator.sample;

import javax.inject.Inject;

public class MethodTestCase {
    int a;
    @Inject public void simpleMethod() {a=3;}
    @Inject public String methodReturningString() {return "foo";}
    @Inject public int methodReturningInt() {return 3;}
    @Inject public int[] methodReturningIntArray() {return new int[] {3};}
    //TODO
    //all primitives return types & params
    //static, final, abstract
    //generics param and return types (wildcard, intersection and upperbounds)
    //exceptions
    //arrays param and return types
    //private
    //generics methods
}
