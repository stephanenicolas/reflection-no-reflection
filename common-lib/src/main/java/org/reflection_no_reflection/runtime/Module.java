package org.reflection_no_reflection.runtime;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflection_no_reflection.*;
import org.reflection_no_reflection.Class;

/**
 * A module contains classes that can be loaded into memory.
 */
@SuppressWarnings("unused")
public interface Module {
    Set<org.reflection_no_reflection.Class> getClassSet();

    Map<String, Set<String>> getMapAnnotationNameToNameOfClassesContainingAnnotation();

    Class<?> loadClass(String className);
}
