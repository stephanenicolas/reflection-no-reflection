package org.reflection_no_reflection;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A module contains classes that can be loaded into memory.
 */
@SuppressWarnings("unused")
public interface Module {
    List<Class> getClassList();

    Map<Class<? extends Annotation>, Set<Class>> getMapOfAnnotationTypeToClassesContainingAnnotation();
}
