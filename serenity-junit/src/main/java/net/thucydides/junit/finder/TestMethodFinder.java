package net.thucydides.junit.finder;

import ch.lambdaj.function.matcher.Predicate;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.List;

import static ch.lambdaj.Lambda.filter;

public class TestMethodFinder {

    private final TestFinder testFinder;

    public TestMethodFinder(TestFinder testFinder) {
        this.testFinder = testFinder;
    }

    public List<Method> withNameContaining(String partialName) {
        return filter(methodsWithPartialName(partialName), testFinder.getAllTestMethods());
    }

    Matcher<Method> methodsWithPartialName(final String partialName) {
        return new Predicate<Method>() {
            public boolean apply(Method testMethod) {
                return testMethod.getName().contains(partialName);
            }
        };
    }

}
