package net.thucydides.core.junit;

import net.thucydides.core.annotations.TestCaseAnnotations;

public class SerenityJUnitTestCase {
    private final Class<?> testSuite;

    public SerenityJUnitTestCase(Class<?> testSuite) {

        this.testSuite = testSuite;
    }

    public static SerenityJUnitTestCase inClass(Class<?> testSuite) {
        return new SerenityJUnitTestCase(testSuite);
    }

    public boolean isAWebTest() {
        return TestCaseAnnotations.isASerenityTestCase(testSuite) && TestCaseAnnotations.isWebTest(testSuite);
    }
}
