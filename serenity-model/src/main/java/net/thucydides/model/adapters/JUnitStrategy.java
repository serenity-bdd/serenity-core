package net.thucydides.model.adapters;


import net.thucydides.model.domain.TestTag;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public interface JUnitStrategy {

    boolean isTestClass(final Class<?> testClass);

    boolean isTestMethod(final Method method);

    boolean isTestSetupMethod(final Method method);

    boolean isSerenityTestCase(final Class<?> testClass);

    boolean isAssumptionViolatedException(final Throwable throwable);

    boolean isATaggableClass(final Class<?> testClass);

    boolean isIgnored(final Method method);

    Optional<String> getTitleAnnotation(Method testMethod);

    List<TestTag> getTagsFor(Method testMethod);

}
