package net.serenitybdd.junit.finder;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.reflection.ClassFinder;
import net.thucydides.junit.annotations.UseTestDataFrom;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.*;

/**
 * The TestFinder class lets you find the Thucydides tests or test methods underneath a given package.
 * <p>You instantiate a TestFinder by providing the top-level package where the tests live.</p>
 * <p>You can then find the list of Thucydides test classes using getNormalTestClasses(), getDataDrivenTestClasses(),
 * and getAllTestClasses() (which returns both normal and data-driven tests).</p>
 * <p>You may also need to retrieve the list of test methods for a particular category of class. You can do this using the
 * getTestMethodsFrom() method, e.g.
 * <pre>new TestFinder("my.package").getTestMethodsFrom().normalTestClasses()</pre>
 */
public abstract class TestFinder {

    protected final String rootPackage;

    /**
     * Create a new test finder instance that will look for tests in the packages underneath the given root package.
     * @param rootPackage The root package used as the starting point when looking for test classes.
     */
    protected TestFinder(final String rootPackage) {
        this.rootPackage = rootPackage;
    }

    public static TestFinderBuilderFactory thatFinds() {
        return new TestFinderBuilderFactory();
    }

    public abstract List<Class<?>> getClasses();

    public abstract int countTestMethods();

    public TestMethodFinder findTestMethods() {
        return new TestMethodFinder(this);
    }

    protected List<Class<?>> getAllTestClasses() {
        return ClassFinder.loadClasses().annotatedWith(RunWith.class).fromPackage(rootPackage);
    }

    protected Set<Class<?>> getNormalTestClasses() {
        Set<Class<?>> normalTestClasses = new HashSet<>();
        for(Class<?> testClass : getAllTestClasses()) {
            if (normalThucydidesTest(testClass)) {
                normalTestClasses.add(testClass);
            }
        }
        return normalTestClasses;
    }

    protected List<Class<?>> getDataDrivenTestClasses() {
        return ClassFinder.loadClasses().annotatedWith(UseTestDataFrom.class).fromPackage(rootPackage);
    }

    protected List<Class<?>> sorted(List<Class<?>> classes) {
        classes.sort(byClassName());
        return classes;
    }

    @SuppressWarnings("deprecation")
    private boolean normalThucydidesTest(Class<?> testClass) {
        RunWith runWith = testClass.getAnnotation(RunWith.class);
        return ((runWith != null) && (runWith.value() == SerenityRunner.class));
    }

    public List<Method> getAllTestMethods() {
        return findMethodsFrom(getClasses());
    }

    private List<Method> findMethodsFrom(List<Class<?>> testClasses) {
        List<Method> methods = new ArrayList<>();

        for (Class<?> testClass : testClasses) {
            addEachMatchingTestMethodFrom(testClass).to(methods);
        }
        methods.sort(byName());
        return methods;
    }

    private Comparator<Method> byName() {
        return Comparator.comparing(Method::getName);
    }

    private static class TestMethodSearchBuilder {
        private final Class<?> testClass;

        private TestMethodSearchBuilder(Class<?> testClass) {
            this.testClass = testClass;
        }


        public void to(List<Method> methods) {
            for(Method method : testClass.getMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    methods.add(method);
                }
            }
        }
    }

    private TestMethodSearchBuilder addEachMatchingTestMethodFrom(Class<?> testClass) {
        return new TestMethodSearchBuilder(testClass);
    }

    private Comparator<Class<?>> byClassName() {
        return Comparator.comparing(Class::getName);
    }


}
