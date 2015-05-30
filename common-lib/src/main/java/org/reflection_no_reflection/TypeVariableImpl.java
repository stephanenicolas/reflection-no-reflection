package org.reflection_no_reflection;

import java.lang.reflect.Type;

/**
 * TypeVariable is the common superinterface for type variables of kinds.
 * A type variable is created the first time it is needed by a reflective
 * method, as specified in this package.  If a type variable t is referenced
 * by a type (i.e, class, interface or annotation type) T, and T is declared
 * by the nth enclosing class of T (see JLS 8.1.2), then the creation of t
 * requires the resolution (see JVMS 5) of the ith enclosing class of T,
 * for i = 0 to n, inclusive. Creating a type variable must not cause the
 * creation of its bounds. Repeated creation of a type variable has no effect.
 *
 * <p>Multiple objects may be instantiated at run-time to
 * represent a given type variable. Even though a type variable is
 * created only once, this does not imply any requirement to cache
 * instances representing the type variable. However, all instances
 * representing a type variable must be equal() to each other.
 * As a consequence, users of type variables must not rely on the identity
 * of instances of classes implementing this interface.
 *
 * @param <D> the type of generic declaration that declared the
 * underlying type variable.
 * @since 1.5
 */
public class TypeVariableImpl<D extends GenericDeclaration> implements TypeVariable<D> {
    private String name;
    private D genericDeclaration;

    public Type[] getBounds() {
        return new Type[0];
    }

    public D getGenericDeclaration() {
        return genericDeclaration;
    }

    public void setGenericDeclaration(D genericDeclaration) {
        this.genericDeclaration = genericDeclaration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
