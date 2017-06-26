package net.serenitybdd.junit.finder;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class TestMethodFinder {

    private final TestFinder testFinder;

    public TestMethodFinder(TestFinder testFinder) {
        this.testFinder = testFinder;
    }

    public List<Method> withNameContaining(String partialName) {
        return testFinder.getAllTestMethods().stream()
                .filter(testMethod -> testMethod.getName().contains(partialName))
                .collect(Collectors.toList());
    }
}
