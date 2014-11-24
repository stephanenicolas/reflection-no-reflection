package org.reflection_no_reflection.annotation_processor;

import com.google.inject.blender.AnnotationDatabaseGenerator;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.tools.JavaFileObject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Generates a AnnotationDatabase implementation for RoboGuice.
 * @author Mike Burton
 * @author SNI
 */
public class ReflectionNoReflectionAnnotationDatabaseGenerator extends AnnotationDatabaseGenerator {

    private boolean isUsingFragmentUtil;
	private boolean isCommentingInjector;

    protected VelocityContext createVelocityContext() {
        VelocityContext context = super.createVelocityContext();
        context.put("isUsingFragmentUtil", isUsingFragmentUtil);
        context.put("isCommentingInjector", isCommentingInjector);
        return context;
    }

    public boolean isUsingFragmentUtil() {
        return isUsingFragmentUtil;
    }
    
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
