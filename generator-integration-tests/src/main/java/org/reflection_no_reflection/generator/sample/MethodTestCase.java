package org.reflection_no_reflection.generator.sample;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MethodTestCase {
    int a;
    @Inject public void simpleMethod() {a=3;}

    @Inject public String methodReturningString() {return "foo";}

    @Inject public int methodReturningInt() {return 3;}

    @Inject public int[] methodReturningIntArray() {return new int[] {3};}

    @Inject public void methodWithPrimitiveParam(int a) {this.a=3;}

    @Inject public void methodWithObjectParam(String s) {a=3;}

    //if there is no int after the array, then we are in the same case as
    // a varargs for method invocation for reflection
    @Inject public void methodWithArrayNotLastParam(String[] s, int b) {a=3;}

    @Inject public void methodWithArrayLastParam(String[] s) {a=3;}

    @Inject public void methodWithVarArgsParam(String... s) {a=3;}

    @Inject public void methodWithGenericsParam(ArrayList<String> s) {a=3;}

    @Inject public void methodWithInterfaceParam(List<String> s) {a=3;}

    @Inject public void methodWithException() throws Exception {a=3;}

    //@Inject public <T> T methodWithGenericsType(T t) throws Exception {return t;}

    //TODO
    //all primitives return types & params
    //static, final, abstract
    //generics param and return types (wildcard, intersection and upperbounds)
    //private
    //generics methods
}
