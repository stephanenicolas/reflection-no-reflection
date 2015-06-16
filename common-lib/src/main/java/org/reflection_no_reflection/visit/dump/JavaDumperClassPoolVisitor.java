package org.reflection_no_reflection.visit.dump;

import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Field;
import org.reflection_no_reflection.Method;
import org.reflection_no_reflection.visit.ClassPoolVisitor;

/**
 * @author SNI.
 */
public class JavaDumperClassPoolVisitor implements ClassPoolVisitor {
    private static final String CLASS_SEPARATOR = "\n";
    private static final String INDENT = "\t";
    private StringBuffer buffer = new StringBuffer();

    @Override
    public <T> void visit(org.reflection_no_reflection.Class<T> aClass) {
        buffer.append(CLASS_SEPARATOR);
        buffer.append(aClass.getLevel());
        buffer.append(":");
        if(aClass.isAnnotation()) {
            buffer.append("@");
        }
        if(aClass.isInterface()) {
            buffer.append("interface ");
        }
        if(aClass.isArray()) {
            buffer.append("[]");
        }
        buffer.append(aClass.getName());
        buffer.append(" {");
        if (aClass.getFields().length != 0 || !aClass.getMethods().isEmpty()) {
            buffer.append(" \n");
        }
    }

    @Override
    public void endVisit(org.reflection_no_reflection.Class aClass) {
        buffer.append("}\n");
    }

    @Override
    public void visit(Field field) {
        buffer.append(INDENT);
        buffer.append(field.getType().getName());
        buffer.append(" ");
        buffer.append(field.getName());
        buffer.append("\n");
    }

    @Override
    public void visit(Method method) {
        buffer.append(INDENT);
        buffer.append(method.getReturnType().getName());
        buffer.append(" ");
        buffer.append(method.getName());
        buffer.append("()");
        buffer.append("\n");
    }

    @Override
    public void visit(Annotation annotation) {
        buffer.append(INDENT);
        buffer.append("@");
        buffer.append(annotation.annotationType().getName());
        if (!annotation.getMethods().isEmpty()) {
            buffer.append("(");
        }
    }

    @Override
    public void visitAnnotationMethod(Annotation annotation, Method method) {
        final String methodName = method.getName();
        buffer.append(" ");
        buffer.append(method.getReturnType().getName());
        buffer.append(" ");
        buffer.append(methodName);
        buffer.append(" = ");
        try {
            buffer.append(annotation.getValue(methodName));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Not associated value for method " + methodName);
        }
    }

    @Override
    public void endVisit(Annotation annotation) {
        if (!annotation.getMethods().isEmpty()) {
            buffer.append(" )");
        }
        buffer.append("\n");
    }

    public StringBuffer getBuffer() {
        return buffer;
    }
}
