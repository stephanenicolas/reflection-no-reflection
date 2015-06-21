package org.reflection_no_reflection;

import java.util.Map;

public class GenericDeclarationImpl implements GenericDeclaration {
    private TypeVariable<?>[] typeParameters;

    public TypeVariable<?>[] getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(TypeVariable<?>[] typeParameters) {
        this.typeParameters = typeParameters;
    }}
