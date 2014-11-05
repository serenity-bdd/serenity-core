package net.thucydides.core.steps;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

public class SpringDependencyInjector implements DependencyInjector {

    /**
     * Setup Spring dependencies in a step library, based on the Spring ContextConfiguration annotation.
     * @param target
     */
    public void injectDependenciesInto(Object target) {
        if (springIsOnClasspath() && annotatedWithSpringContext(target)) {
            TestContextManager contextManager = getTestContextManager(target.getClass());
            try {
                contextManager.prepareTestInstance(target);
            } catch (Exception e) {
                throw new IllegalStateException("Could not instantiate test instance", e);
            }
        }
    }

    private boolean annotatedWithSpringContext(Object target) {
        return (target.getClass().getAnnotation(ContextConfiguration.class) != null);
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
