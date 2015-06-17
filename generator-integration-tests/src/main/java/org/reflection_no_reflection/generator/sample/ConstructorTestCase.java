package org.reflection_no_reflection.generator.sample;

import javax.inject.Inject;

public class ConstructorTestCase {
    int a;

    @Inject public ConstructorTestCase() {a = 3;}
}
