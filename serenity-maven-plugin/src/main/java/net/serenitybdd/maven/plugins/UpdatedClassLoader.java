package net.serenitybdd.maven.plugins;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

class UpdatedClassLoader {

    private MavenProject project;

    private UpdatedClassLoader(MavenProject project) {
        this.project = project;
    }

    static void withProjectClassesFrom(MavenProject project) throws MojoExecutionException {
        new UpdatedClassLoader(project).updateClassLoader();
    }

    private void updateClassLoader() throws MojoExecutionException {
        ClassLoader classLoader = getCompleteClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    private ClassLoader getCompleteClassLoader() throws MojoExecutionException {
        try {

            Set<String> classpathElements = new HashSet<>();

            classpathElements.addAll(buildClasspathElementsFrom(project));

            classpathElements.addAll(currentResources());

            URL urls[] = classpathElements.stream()
                    .map(this::urlFrom)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList())
                    .toArray(new URL[]{});

            return new URLClassLoader(urls, getClass().getClassLoader());


        } catch (Exception e)//gotta catch em all
        {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }

    private Collection<String> buildClasspathElementsFrom(MavenProject project) throws DependencyResolutionRequiredException {

        Collection<String> elements = new HashSet<>();

        if (project != null) {

            if (project.getCompileClasspathElements() != null) {
                elements.addAll(project.getCompileClasspathElements());
            }

            if (project.getBuild() != null) {
                elements.add(project.getBuild().getOutputDirectory());
                elements.add(project.getBuild().getTestOutputDirectory());
            }
        }


        return elements;
    }

    private List<String> currentResources() {
        if (Thread.currentThread().getContextClassLoader() instanceof URLClassLoader) {
            return Arrays.stream(
                    ((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs())
                    .map(URL::getPath)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private Optional<URL> urlFrom(String classpathElement) {
        try {
            return Optional.of(new File(classpathElement).toURI().toURL());
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }
}
