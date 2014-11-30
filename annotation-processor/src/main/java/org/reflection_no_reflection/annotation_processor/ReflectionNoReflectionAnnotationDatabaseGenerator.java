package org.reflection_no_reflection.annotation_processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.tools.JavaFileObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.reflection_no_reflection.Annotation;
import org.reflection_no_reflection.Field;

/**
 * Generates a AnnotationDatabase + reflection database.
 *
 * @author Mike Burton
 * @author SNI
 */
public class ReflectionNoReflectionAnnotationDatabaseGenerator {

    private boolean isUsingFragmentUtil;
    private boolean isCommentingInjector;
    private String templatePath;
    private String packageName;
    private HashMap<String, Map<String, Set<Field>>> mapAnnotationToMapClassWithInjectionNameToFieldSet;
    private HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodSet;
    private HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToConstructorSet;
    private HashMap<String, Annotation> mapAnnotationNameToAnnotation;
    private HashSet<String> classesContainingInjectionPointsSet;
    private HashSet<String> bindableClasses;

    public void generateAnnotationDatabase(JavaFileObject jfo) throws IOException {

        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(props);

        VelocityContext context = createVelocityContext();

        Template template = null;

        PrintWriter w = null;
        try {
            template = Velocity.getTemplate(templatePath);
            w = new PrintWriter(jfo.openWriter());
            template.merge(context, w);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("Impossible to generate annotation database.", ex);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new IOException("Impossible to close annotation database.", ex);
                }
            }
        }
    }

    protected VelocityContext createVelocityContext() {
        VelocityContext context = new VelocityContext();
        context.put("packageName", packageName);
        context.put("mapAnnotationToMapClassWithInjectionNameToFieldSet", mapAnnotationToMapClassWithInjectionNameToFieldSet);
        context.put("mapAnnotationToMapClassWithInjectionNameToMethodSet", mapAnnotationToMapClassWithInjectionNameToMethodSet);
        context.put("mapAnnotationToMapClassWithInjectionNameToConstructorSet", mapAnnotationToMapClassWithInjectionNameToConstructorSet);
        context.put("mapAnnotationNameToAnnotation", mapAnnotationNameToAnnotation);
        context.put("classesContainingInjectionPointsSet", classesContainingInjectionPointsSet);
        context.put("injectedClasses", bindableClasses);
        context.put("isUsingFragmentUtil", isUsingFragmentUtil);
        context.put("isCommentingInjector", isCommentingInjector);

        return context;
    }

    @SuppressWarnings("unused")
    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    @SuppressWarnings("unused")
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @SuppressWarnings("unused")
    public HashMap<String, Map<String, Set<Field>>> getMapAnnotationToMapClassWithInjectionNameToFieldSet() {
        return mapAnnotationToMapClassWithInjectionNameToFieldSet;
    }

    public void setMapAnnotationToMapClassWithInjectionNameToFieldSet(HashMap<String, Map<String, Set<Field>>> mapAnnotationToMapClassWithInjectionNameToFieldSet) {
        this.mapAnnotationToMapClassWithInjectionNameToFieldSet = mapAnnotationToMapClassWithInjectionNameToFieldSet;
    }

    @SuppressWarnings("unused")
    public HashMap<String, Map<String, Set<String>>> getMapAnnotationToMapClassWithInjectionNameToMethodSet() {
        return mapAnnotationToMapClassWithInjectionNameToMethodSet;
    }

    public void setMapAnnotationToMapClassWithInjectionNameToMethodSet(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodSet) {
        this.mapAnnotationToMapClassWithInjectionNameToMethodSet = mapAnnotationToMapClassWithInjectionNameToMethodSet;
    }

    @SuppressWarnings("unused")
    public HashMap<String, Map<String, Set<String>>> getMapAnnotationToMapClassWithInjectionNameToConstructorSet() {
        return mapAnnotationToMapClassWithInjectionNameToConstructorSet;
    }

    @SuppressWarnings("unused")
    public void setMapAnnotationToMapClassWithInjectionNameToConstructorSet(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToConstructorSet) {
        this.mapAnnotationToMapClassWithInjectionNameToConstructorSet = mapAnnotationToMapClassWithInjectionNameToConstructorSet;
    }

    @SuppressWarnings("unused")
    public HashSet<String> getClassesContainingInjectionPointsSet() {
        return classesContainingInjectionPointsSet;
    }

    public void setClassesContainingInjectionPointsSet(HashSet<String> classesContainingInjectionPointsSet) {
        this.classesContainingInjectionPointsSet = classesContainingInjectionPointsSet;
    }

    public void setMapAnnotationNameToAnnotation(HashMap<String, Annotation> mapAnnotationNameToAnnotation) {
        this.mapAnnotationNameToAnnotation = mapAnnotationNameToAnnotation;
    }

    @SuppressWarnings("unused")
    public HashMap<String, Annotation> getMapAnnotationNameToAnnotation() {
        return mapAnnotationNameToAnnotation;
    }

    @SuppressWarnings("unused")
    public HashSet<String> getBindableClasses() {
        return bindableClasses;
    }

    public void setBindableClasses(HashSet<String> bindableClasses) {
        this.bindableClasses = bindableClasses;
    }

    @SuppressWarnings("unused")
    public boolean isUsingFragmentUtil() {
        return isUsingFragmentUtil;
    }

    @SuppressWarnings("unused")
    public boolean isCommentingInjector() {
        return isCommentingInjector;
    }

    public void setUsingFragmentUtil(boolean isUsingFragmentUtil) {
        this.isUsingFragmentUtil = isUsingFragmentUtil;
    }

    public void setCommentingInjector(boolean isCommentingInjector) {
        this.isCommentingInjector = isCommentingInjector;
    }
}
