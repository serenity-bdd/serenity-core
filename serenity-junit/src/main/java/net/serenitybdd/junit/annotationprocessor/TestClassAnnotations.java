package net.serenitybdd.junit.annotationprocessor;

import net.thucydides.core.annotations.TestCaseAnnotations;

/**
 * Created by john on 4/02/2016.
 */
public class TestClassAnnotations {


    private final Class testClass;

    public TestClassAnnotations(Class<?> testClass) {
        this.testClass = testClass;
    }

    public static TestClassAnnotations forTestClass(Class<?> testClass) {
        return new TestClassAnnotations(testClass);
    }

    public boolean toUseAUniqueSession() {
        return TestCaseAnnotations.isUniqueSession(testClass);
    }
}
