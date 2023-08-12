package net.thucydides.core.steps;

import net.serenitybdd.annotations.TestsRequirement;
import net.serenitybdd.annotations.TestsRequirements;
import net.serenitybdd.annotations.Title;
import net.thucydides.model.reflection.MethodFinder;
import net.thucydides.model.util.NameConverter;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static net.thucydides.model.util.NameConverter.withNoArguments;

/**
 * Helps analyse annotations on test methods, steps and step groups.
 */
public class TestDescription {

    private final Class<?> testClass;
    private final String methodName;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestDescription.class);

    public TestDescription(Class<?> testClass, String methodName) {
        this.testClass = testClass;
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getName() {
        String annotatedTitle = getAnnotatedTitle();
        if (annotatedTitle != null) {
            return annotatedTitle;
        }
        return NameConverter.humanize(getMethodName());
    }

    public Class<?> getTestClass() {
        return testClass;
    }


    public Method getTestMethod() {
        Method testMethod = null;
        if (getTestClass() != null) {
            testMethod =  methodCalled(withNoArguments(methodName), getTestClass());
        }
        if (testMethod == null) {
            LOGGER.error("No test method called {} was found in {}", methodName, testClass);
            throw new TestMethodNotFoundException("No test method called " + methodName + " was found in " + testClass);
        }
        return testMethod;
    }


    private Method methodCalled(final String methodName, final Class<?> testClass) {
        return MethodFinder.inClass(testClass).getMethodNamed(methodName);
    }

    public String getAnnotatedTitle() {

        Method testMethod = getTestMethod();
        Title title = testMethod.getAnnotation(Title.class);
        if (title != null) {
            return title.value();
        }
        return null;
    }

    public Set<String> getAnnotatedRequirements() {
        Set<String> requirements = new HashSet<String>();
        if (getTestClass() != null) {
            Method testMethod = getTestMethod();
            addRequirementFrom(requirements, testMethod);
            addMultipleRequirementsFrom(requirements, testMethod);
        }
        return requirements;
    }

    private void addMultipleRequirementsFrom(final Set<String> requirements, final Method testMethod) {
        TestsRequirements testRequirements = testMethod.getAnnotation(TestsRequirements.class);
        if (testRequirements != null) {
            requirements.addAll(Arrays.asList(testRequirements.value()));
        }
    }

    private void addRequirementFrom(final Set<String> requirements, final Method testMethod) {
        TestsRequirement testsRequirement = testMethod.getAnnotation(TestsRequirement.class);
        if (testsRequirement != null) {
            requirements.add(testsRequirement.value());
        }
    }
}
