package org.reflection_no_reflection.generator.sample;

import javax.inject.Inject;

public class Foo extends Bar implements Qurtz {
    @Inject Foo foo;
    @Inject Bar bar;
}

class Bar {
}

interface Qurtz {

}
