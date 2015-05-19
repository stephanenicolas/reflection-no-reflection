package org.reflection_no_reflection.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

/**
 * @author SNI
 */
public class NoReflectionPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    def extension = getExtension()
    def pluginExtension = getPluginExtension()
    if (extension && pluginExtension) {
      NoReflectionPluginExtension ext = project.extensions.create(extension, pluginExtension)
    }

    final def log = project.logger
    final String LOG_TAG = this.getClass().getName()

    project.afterEvaluate {
      JavaCompile javaCompile = project.tasks.findByName('compileJava')
      javaCompile.options.compilerArgs += ['-AannotatedClasses=' + project.noreflection.annotationClasses.join(',')]
    }
  }

  protected Class getPluginExtension() {
    NoReflectionPluginExtension
  }

  protected String getExtension() {
    'noreflection'
  }
}
