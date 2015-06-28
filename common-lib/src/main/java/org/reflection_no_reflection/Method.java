package org.reflection_no_reflection;

import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.reflect.generics.repository.MethodRepository;

public class Method extends Member implements GenericDeclaration, Invokable {
    private Class clazz;
    private int slot;
    // This is guaranteed to be interned by the VM in the 1.4
    // reflection implementation
    private String name;
    private Class returnType;
    private Class[] parameterTypes;
    private Class[] exceptionTypes;
    private int modifiers;
    // Generics and annotations support
    private transient String signature;
    // generic info repository; lazily initialized
    private transient MethodRepository genericInfo;
    private byte[] annotations;
    //TODO parameter annotations. Create an invokable member class
    //it should manage params and exceptions. Inherited by constructor and methods.
    private byte[] parameterAnnotations;
    private byte[] annotationDefault;

    // Modifiers that can be applied to a method in source code
    private static final int LANGUAGE_MODIFIERS =
        Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE |
            Modifier.ABSTRACT | Modifier.STATIC | Modifier.FINAL |
            Modifier.SYNCHRONIZED | Modifier.NATIVE;
    private boolean isVarArgs;

    // Generics infrastructure

    private String getGenericSignature() {
        return signature;
    }

    // Accessor for generic info repository
    private MethodRepository getGenericInfo() {
        throw new UnsupportedOperationException();
    }

    public Method(Class declaringClass,
                  String name,
                  Class[] parameterTypes,
                  Class returnType,
                  Class[] checkedExceptions,
                  int modifiers) {
        this.clazz = declaringClass;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.exceptionTypes = checkedExceptions;
        this.modifiers = modifiers;
    }

    /**
     * Returns the {@code Class} object representing the class or interface
     * that declares the method represented by this {@code Method} object.
     */
    public Class<?> getDeclaringClass() {
        return clazz;
    }

    /**
     * Returns the name of the method represented by this {@code Method}
     * object, as a {@code String}.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Java language modifiers for the method represented
     * by this {@code Method} object, as an integer. The {@code Modifier} class should
     * be used to decode the modifiers.
     *
     * @see Modifier
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Returns an array of {@code TypeVariable} objects that represent the
     * type variables declared by the generic declaration represented by this
     * {@code GenericDeclaration} object, in declaration order.  Returns an
     * array of length 0 if the underlying generic declaration declares no type
     * variables.
     *
     * @return an array of {@code TypeVariable} objects that represent
     * the type variables declared by this generic declaration
     * @throws GenericSignatureFormatError if the generic
     * signature of this generic declaration does not conform to
     * the format specified in the Java Virtual Machine Specification,
     * 3rd edition
     * @since 1.5
     */
    public TypeVariable<Method>[] getTypeParameters() {
        if (getGenericSignature() != null) {
            return (TypeVariable<Method>[]) getGenericInfo().getTypeParameters();
        } else {
            return (TypeVariable<Method>[]) new TypeVariable[0];
        }
    }

    /**
     * Returns a {@code Class} object that represents the formal return type
     * of the method represented by this {@code Method} object.
     *
     * @return the return type for the method this object represents
     */
    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * Returns a {@code Type} object that represents the formal return
     * type of the method represented by this {@code Method} object.
     *
     * <p>If the return type is a parameterized type,
     * the {@code Type} object returned must accurately reflect
     * the actual type parameters used in the source code.
     *
     * <p>If the return type is a type variable or a parameterized type, it
     * is created. Otherwise, it is resolved.
     *
     * @return a {@code Type} object that represents the formal return
     * type of the underlying  method
     * @throws GenericSignatureFormatError if the generic method signature does not conform to the format
     * specified in the Java Virtual Machine Specification, 3rd edition
     * @throws TypeNotPresentException if the underlying method's
     * return type refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if the
     * underlying method's return typed refers to a parameterized
     * type that cannot be instantiated for any reason
     * @since 1.5
     */
    public Type getGenericReturnType() {
        if (getGenericSignature() != null) {
            return getGenericInfo().getReturnType();
        } else {
            return getReturnType();
        }
    }

    public void setAccessible(boolean accessible) {
        //nothing to do
    }
    /**
     * Returns an array of {@code Class} objects that represent the formal
     * parameter types, in declaration order, of the method
     * represented by this {@code Method} object.  Returns an array of length
     * 0 if the underlying method takes no parameters.
     *
     * @return the parameter types for the method this object
     * represents
     */
    public Class<?>[] getParameterTypes() {
        return (Class<?>[]) parameterTypes.clone();
    }

    /**
     * Returns an array of {@code Type} objects that represent the formal
     * parameter types, in declaration order, of the method represented by
     * this {@code Method} object. Returns an array of length 0 if the
     * underlying method takes no parameters.
     *
     * <p>If a formal parameter type is a parameterized type,
     * the {@code Type} object returned for it must accurately reflect
     * the actual type parameters used in the source code.
     *
     * <p>If a formal parameter type is a type variable or a parameterized
     * type, it is created. Otherwise, it is resolved.
     *
     * @return an array of Types that represent the formal
     * parameter types of the underlying method, in declaration order
     * @throws GenericSignatureFormatError if the generic method signature does not conform to the format
     * specified in the Java Virtual Machine Specification, 3rd edition
     * @throws TypeNotPresentException if any of the parameter
     * types of the underlying method refers to a non-existent type
     * declaration
     * @throws MalformedParameterizedTypeException if any of
     * the underlying method's parameter types refer to a parameterized
     * type that cannot be instantiated for any reason
     * @since 1.5
     */
    public Type[] getGenericParameterTypes() {
        if (getGenericSignature() != null) {
            return getGenericInfo().getParameterTypes();
        } else {
            return getParameterTypes();
        }
    }

    /**
     * Returns an array of {@code Class} objects that represent
     * the types of the exceptions declared to be thrown
     * by the underlying method
     * represented by this {@code Method} object.  Returns an array of length
     * 0 if the method declares no exceptions in its {@code throws} clause.
     *
     * @return the exception types declared as being thrown by the
     * method this object represents
     */
    public Class<?>[] getExceptionTypes() {
        return (Class<?>[]) exceptionTypes.clone();
    }

    /**
     * Returns an array of {@code Type} objects that represent the
     * exceptions declared to be thrown by this {@code Method} object.
     * Returns an array of length 0 if the underlying method declares
     * no exceptions in its {@code throws} clause.
     *
     * <p>If an exception type is a parameterized type, the {@code Type}
     * object returned for it must accurately reflect the actual type
     * parameters used in the source code.
     *
     * <p>If an exception type is a type variable or a parameterized
     * type, it is created. Otherwise, it is resolved.
     *
     * @return an array of Types that represent the exception types
     * thrown by the underlying method
     * @throws GenericSignatureFormatError if the generic method signature does not conform to the format
     * specified in the Java Virtual Machine Specification, 3rd edition
     * @throws TypeNotPresentException if the underlying method's
     * {@code throws} clause refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if
     * the underlying method's {@code throws} clause refers to a
     * parameterized type that cannot be instantiated for any reason
     * @since 1.5
     */
    public Type[] getGenericExceptionTypes() {
        Type[] result;
        if (getGenericSignature() != null &&
            ((result = getGenericInfo().getExceptionTypes()).length > 0)) {
            return result;
        } else {
            return getExceptionTypes();
        }
    }

    /**
     * Compares this {@code Method} against the specified object.  Returns
     * true if the objects are the same.  Two {@code Methods} are the same if
     * they were declared by the same class and have the same name
     * and formal parameter types and return type.
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Method) {
            Method other = (Method) obj;
            if ((getDeclaringClass() == other.getDeclaringClass())
                && (getName().equals(other.getName()))) {
                if (!returnType.equals(other.getReturnType())) {
                    return false;
                }
                /* Avoid unnecessary cloning */
                Class[] params1 = parameterTypes;
                Class[] params2 = other.parameterTypes;
                if (params1.length == params2.length) {
                    for (int i = 0; i < params1.length; i++) {
                        if (params1[i] != params2[i]) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public Object invoke(Object obj, Object... args) throws InvocationTargetException {
        return getDeclaringClass().getReflector().invokeMethod(obj, getName(), toString(), args);
    }

    /**
     * Returns a hashcode for this {@code Method}.  The hashcode is computed
     * as the exclusive-or of the hashcodes for the underlying
     * method's declaring class name and the method's name.
     */
    public int hashCode() {
        return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
    }

    /**
     * Returns a string describing this {@code Method}.  The string is
     * formatted as the method access modifiers, if any, followed by
     * the method return type, followed by a space, followed by the
     * class declaring the method, followed by a period, followed by
     * the method name, followed by a parenthesized, comma-separated
     * list of the method's formal parameter types. If the method
     * throws checked exceptions, the parameter list is followed by a
     * space, followed by the word throws followed by a
     * comma-separated list of the thrown exception types.
     * For example:
     * <pre>
     *    public boolean java.lang.Object.equals(java.lang.Object)
     * </pre>
     *
     * <p>The access modifiers are placed in canonical order as
     * specified by "The Java Language Specification".  This is
     * {@code public}, {@code protected} or {@code private} first,
     * and then other modifiers in the following order:
     * {@code abstract}, {@code static}, {@code final},
     * {@code synchronized}, {@code native}.
     */
    public String toString() {
        try {
            StringBuffer sb = new StringBuffer();
            int mod = getModifiers() & LANGUAGE_MODIFIERS;
            if (mod != 0) {
                sb.append(Modifier.toString(mod) + " ");
            }
            sb.append(Field.getTypeName(getReturnType()) + " ");
            sb.append(Field.getTypeName(getDeclaringClass()) + ".");
            sb.append(getName() + "(");
            Class[] params = parameterTypes; // avoid clone
            for (int j = 0; j < params.length; j++) {
                sb.append(Field.getTypeName(params[j]));
                if (j < (params.length - 1)) {
                    sb.append(",");
                }
            }
            sb.append(")");
            Class[] exceptions = exceptionTypes; // avoid clone
            if (exceptions.length > 0) {
                sb.append(" throws ");
                for (int k = 0; k < exceptions.length; k++) {
                    sb.append(exceptions[k].getName());
                    if (k < (exceptions.length - 1)) {
                        sb.append(",");
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    /**
     * Returns a string describing this {@code Method}, including
     * type parameters.  The string is formatted as the method access
     * modifiers, if any, followed by an angle-bracketed
     * comma-separated list of the method's type parameters, if any,
     * followed by the method's generic return type, followed by a
     * space, followed by the class declaring the method, followed by
     * a period, followed by the method name, followed by a
     * parenthesized, comma-separated list of the method's generic
     * formal parameter types.
     *
     * A space is used to separate access modifiers from one another
     * and from the type parameters or return type.  If there are no
     * type parameters, the type parameter list is elided; if the type
     * parameter list is present, a space separates the list from the
     * class name.  If the method is declared to throw exceptions, the
     * parameter list is followed by a space, followed by the word
     * throws followed by a comma-separated list of the generic thrown
     * exception types.  If there are no type parameters, the type
     * parameter list is elided.
     *
     * <p>The access modifiers are placed in canonical order as
     * specified by "The Java Language Specification".  This is
     * {@code public}, {@code protected} or {@code private} first,
     * and then other modifiers in the following order:
     * {@code abstract}, {@code static}, {@code final},
     * {@code synchronized} {@code native}.
     *
     * @return a string describing this {@code Method},
     * include type parameters
     * @since 1.5
     */
    public String toGenericString() {
        try {
            StringBuilder sb = new StringBuilder();
            int mod = getModifiers() & LANGUAGE_MODIFIERS;
            if (mod != 0) {
                sb.append(Modifier.toString(mod) + " ");
            }
            TypeVariable<?>[] typeparms = getTypeParameters();
            if (typeparms.length > 0) {
                boolean first = true;
                sb.append("<");
                for (TypeVariable<?> typeparm : typeparms) {
                    if (!first) {
                        sb.append(",");
                    }
                    // Class objects can't occur here; no need to test
                    // and call Class.getName().
                    sb.append(typeparm.toString());
                    first = false;
                }
                sb.append("> ");
            }

            Type genRetType = getGenericReturnType();
            sb.append(((genRetType instanceof Class<?>) ?
                Field.getTypeName((Class<?>) genRetType) : genRetType.toString()) + " ");

            sb.append(Field.getTypeName(getDeclaringClass()) + ".");
            sb.append(getName() + "(");
            Type[] params = getGenericParameterTypes();
            for (int j = 0; j < params.length; j++) {
                String param = (params[j] instanceof Class) ?
                    Field.getTypeName((Class) params[j]) :
                    (params[j].toString());
                sb.append(param);
                if (j < (params.length - 1)) {
                    sb.append(",");
                }
            }
            sb.append(")");
            Type[] exceptions = getGenericExceptionTypes();
            if (exceptions.length > 0) {
                sb.append(" throws ");
                for (int k = 0; k < exceptions.length; k++) {
                    sb.append((exceptions[k] instanceof Class) ?
                                  ((Class) exceptions[k]).getName() :
                        exceptions[k].toString());
                    if (k < (exceptions.length - 1)) {
                        sb.append(",");
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    /**
     * Returns {@code true} if this method is a bridge
     * method; returns {@code false} otherwise.
     *
     * @return true if and only if this method is a bridge
     * method as defined by the Java Language Specification.
     * @since 1.5
     */
    public boolean isBridge() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if this method was declared to take
     * a variable number of arguments; returns {@code false}
     * otherwise.
     *
     * @return {@code true} if an only if this method was declared to
     * take a variable number of arguments.
     * @since 1.5
     */
    public boolean isVarArgs() {
        return isVarArgs;
    }

    public void setIsVarArgs(boolean isVarArgs) {
        this.isVarArgs = isVarArgs;
    }

    /**
     * Returns {@code true} if this method is a synthetic
     * method; returns {@code false} otherwise.
     *
     * @return true if and only if this method is a synthetic
     * method as defined by the Java Language Specification.
     * @since 1.5
     */
    public boolean isSynthetic() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the default value for the annotation member represented by
     * this {@code Method} instance.  If the member is of a primitive type,
     * an instance of the corresponding wrapper type is returned. Returns
     * null if no default is associated with the member, or if the method
     * instance does not represent a declared member of an annotation type.
     *
     * @return the default value for the annotation member represented
     * by this {@code Method} instance.
     * @throws TypeNotPresentException if the annotation is of type
     * {@link Class} and no definition can be found for the
     * default class value.
     * @since 1.5
     */
    public Object getDefaultValue() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an array of arrays that represent the annotations on the formal
     * parameters, in declaration order, of the method represented by
     * this {@code Method} object. (Returns an array of length zero if the
     * underlying method is parameterless.  If the method has one or more
     * parameters, a nested array of length zero is returned for each parameter
     * with no annotations.) The annotation objects contained in the returned
     * arrays are serializable.  The caller of this method is free to modify
     * the returned arrays; it will have no effect on the arrays returned to
     * other callers.
     *
     * @return an array of arrays that represent the annotations on the formal
     * parameters, in declaration order, of the method represented by this
     * Method object
     * @since 1.5
     */
    public java.lang.annotation.Annotation[][] getParameterAnnotations() {
        throw new UnsupportedOperationException();
    }

}
