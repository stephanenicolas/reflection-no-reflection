package org.reflection_no_reflection.annotation_processor;


import com.google.inject.blender.AnnotationDatabaseGenerator;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;

import com.google.inject.blender.GuiceAnnotationProcessor;

/**
 * An annotation processor that detects classes that need to receive injections.
 * It is a {@link GuiceAnnotationProcessor} that is triggered for all the annotations
 * of both Guice and RoboGuice.
 * @author MikeBurton
 * @author SNI
 */
@SupportedAnnotationTypes({"com.google.inject.Inject", "com.google.inject.Provides", "javax.inject.Inject", "roboguice.inject.InjectView", "roboguice.inject.InjectResource", "roboguice.inject.InjectPreference", "roboguice.inject.InjectExtra", "roboguice.inject.InjectFragment", "roboguice.event.Observes", "roboguice.inject.ContentView"})
@SupportedOptions({"guiceAnnotationDatabasePackageName", "guiceUsesFragmentUtil", "guiceCommentsInjector"})
public class ReflectionNoReflectionAnnotationProcessor extends GuiceAnnotationProcessor {

    public static final String TEMPLATE_ANNOTATION_DATABASE_PATH = "templates/RGAnnotationDatabaseImpl.vm";

    private boolean isUsingFragmentUtil = true;
    private boolean isCommentingInjector = true;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        String isUsingFragmentUtilString = processingEnv.getOptions().get("guiceUsesFragmentUtil");
        if (isUsingFragmentUtilString!=null) {
            isUsingFragmentUtil = Boolean.parseBoolean(isUsingFragmentUtilString);
        }
        String isCommentingInjectorString = processingEnv.getOptions().get("guiceCommentsInjector");
        if (isCommentingInjectorString!=null) {
        	isCommentingInjector = Boolean.parseBoolean(isCommentingInjectorString);
        }
    }

	@Override
	public SourceVersion getSupportedSourceVersion() {
		//http://stackoverflow.com/a/8188860/693752
		return SourceVersion.latest();
	}

    @Override
    protected AnnotationDatabaseGenerator createAnnotationDatabaseGenerator() {
        return new ReflectionNoReflectionAnnotationDatabaseGenerator();
    }
    
    @Override
    protected void configure(AnnotationDatabaseGenerator annotationDatabaseGenerator) {
        super.configure(annotationDatabaseGenerator);
        annotationDatabaseGenerator.setTemplatePath(TEMPLATE_ANNOTATION_DATABASE_PATH);
        ((ReflectionNoReflectionAnnotationDatabaseGenerator)annotationDatabaseGenerator).setUsingFragmentUtil(isUsingFragmentUtil);
        ((ReflectionNoReflectionAnnotationDatabaseGenerator)annotationDatabaseGenerator).setCommentingInjector(isCommentingInjector);
    }

}
