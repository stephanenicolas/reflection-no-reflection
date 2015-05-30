package org.reflection_no_reflection;

import java.util.List;

/**
 * A module contains classes that can be loaded into memory.
 */
@SuppressWarnings("unused")
public interface Module {
    List<Class> getClassList();
}
