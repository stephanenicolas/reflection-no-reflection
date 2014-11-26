package org.reflection_no_reflection.no_reflection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.no_reflection.FieldGetterAndSetter;

public abstract class AnnotationDatabase {
    protected static FieldGetterAndSetter fieldGetterAndSetter;

    //TODO add the additional packages here and load database impl classes.
    protected AnnotationDatabase() {}

    //TODO rename methods
    public abstract void fillAnnotationClassesAndFieldsNames(HashMap<String, Map<String, Set<Field>>> mapAnnotationToMapClassWithInjectionNameToFieldSet);

    public abstract void fillAnnotationClassesAndMethods(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodSet);

    public abstract void fillAnnotationClassesAndConstructors(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToConstructorSet);

    public abstract void fillClassesContainingInjectionPointSet(HashSet<String> classesContainingInjectionPointsSet);

    public abstract void fillBindableClasses(HashSet<String> injectedClasses);
}
