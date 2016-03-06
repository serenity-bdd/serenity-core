package net.serenitybdd.junit.runners;

import net.thucydides.core.annotations.TestCaseAnnotations;
import org.junit.runners.model.TestClass;

/**
 * Created by john on 4/02/2016.
 */
public class TestClassAnnotations {


    private final TestClass testClass;

    public TestClassAnnotations(Class<?> testClass) {
        this.testClass = new TestClass(testClass);
    }

    public static TestClassAnnotations forTestClass(Class<?> testClass) {
        return new TestClassAnnotations(testClass);
    }

    public boolean toUseAUniqueSession() {
        return TestCaseAnnotations.isUniqueSession(testClass.getJavaClass());
    }
}
