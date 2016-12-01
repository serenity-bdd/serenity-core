package net.serenitybdd.core.di;

import java.lang.annotation.Annotation;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestContextManager;

public class SpringDependencyInjector implements DependencyInjector {

    /**
     * Setup Spring dependencies in a step library, based on the Spring ContextConfiguration annotation.
     * @param target
     */
    public void injectDependenciesInto(Object target) {
        if (springIsOnClasspath() && (annotatedWithSpringContext(target) || annotatedWithSpringBootTest(target))) {
            TestContextManager contextManager = getTestContextManager(target.getClass());
            try {
                contextManager.prepareTestInstance(target);
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate test instance", e);
            }
        }
    }

    @Override
    public void reset() {}

    private boolean annotatedWithSpringContext(Object target) {

        return (AnnotationUtils.findAnnotation(target.getClass(), ContextConfiguration.class) != null) || (AnnotationUtils.findAnnotation(target.getClass(), ContextHierarchy.class) != null);
    }

    private boolean annotatedWithSpringBootTest(Object target) {
        try {
            Class<?> bootTest = Class.forName("org.springframework.boot.test.context.SpringBootTest");
            Class<? extends Annotation> bootTestAnnotation = (Class<? extends Annotation>) bootTest;
            return null != AnnotationUtils.findAnnotation(target.getClass(), bootTestAnnotation);
        } catch(ClassNotFoundException e) {
            return false;
        }
	}

    private boolean springIsOnClasspath() {
        try {
            Class.forName("org.springframework.test.context.ContextConfiguration");
            return true;
        } catch(ClassNotFoundException e) {
            return false;
        }
    }

    protected TestContextManager getTestContextManager(Class<?> clazz) {
        return new TestContextManager(clazz);
    }
}
