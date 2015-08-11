package net.thucydides.core.annotations;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.html.Formatter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static net.thucydides.core.tags.TagConverters.fromStringValuesToTestTags;
import static net.thucydides.core.tags.TagConverters.toTestTags;
import static net.thucydides.core.util.NameConverter.withNoArguments;

/**
 * Utility class used to help process annotations on tests and test steps.
 */
public class TestAnnotations {

    private final Class<?> testClass;

    private TestAnnotations(final Class<?> testClass) {
        this.testClass = testClass;
    }

    public static TestAnnotations forClass(final Class<?> testClass) {
        return new TestAnnotations(testClass);
    }

    public Optional<String> getAnnotatedTitleForMethod(final String methodName) {
        if ((testClass != null) && (testClassHasMethodCalled(methodName))) {
            return getAnnotatedTitle(methodName);
        }
        return Optional.absent();
    }

    public boolean isPending(final String methodName) {
        Optional<Method> method = getMethodCalled(methodName);
        return method.isPresent() && isPending(method.get());
    }

    public static boolean isPending(final Method method) {
        return method != null && (method.getAnnotation(Pending.class) != null);
    }

    public static boolean isIgnored(final Method method) {
        if (method != null) {
            return hasAnnotationCalled(method, "Ignore");
        }
        return false;
    }

    public static boolean shouldSkipNested(Method method) {
        if (method != null) {
            Step stepAnnotation = method.getAnnotation(Step.class);
            return ((stepAnnotation != null) && (!stepAnnotation.callNestedMethods()));
        }
        return false;
    }

    private static boolean hasAnnotationCalled(Method method, String annotationName) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equals(annotationName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isIgnored(final String methodName) {
        Optional<Method> method = getMethodCalled(methodName);
        return method.isPresent() && isIgnored(method.get());
    }

    private Optional<String> getAnnotatedTitle(String methodName) {
        Optional<Method> testMethod = getMethodCalled(methodName);
        if (testMethod.isPresent()) {
            Title titleAnnotation = testMethod.get().getAnnotation(Title.class);
            if (titleAnnotation != null) {
                return Optional.of(titleAnnotation.value());
            }
        }
        return Optional.absent();
    }

    private boolean testClassHasMethodCalled(final String methodName) {
        return (getMethodCalled(methodName).isPresent());

    }

    private Optional<Method> getMethodCalled(final String methodName) {
        if (testClass == null) {
            return Optional.absent();
        }
        String baseMethodName = withNoArguments(methodName);
        try {
            if (baseMethodName == null) {
                return Optional.absent();
            } else {
                return Optional.fromNullable(testClass.getMethod(baseMethodName));
            }
        } catch (NoSuchMethodException e) {
            return Optional.absent();
        }
    }

    /**
     * Return a list of the issues mentioned in the title annotation of this method.
     */
    public List<String> getAnnotatedIssuesForMethodTitle(String methodName) {
        Optional<String> title = getAnnotatedTitleForMethod(methodName);
        if (title.isPresent()) {
            return Formatter.issuesIn(title.get());
        } else {
            return Formatter.issuesIn(methodName);
        }
    }


    public Optional<String> getAnnotatedIssue(String methodName) {
        Optional<Method> testMethod = getMethodCalled(methodName);
        if ((testMethod.isPresent()) && (testMethod.get().getAnnotation(Issue.class) != null)) {
            return Optional.of(testMethod.get().getAnnotation(Issue.class).value());
        }
        return Optional.absent();
    }

    private Optional<String> getAnnotatedVersion(String methodName) {
        Optional<Method> testMethod = getMethodCalled(methodName);
        if ((testMethod.isPresent()) && (testMethod.get().getAnnotation(Version.class) != null)) {
            return Optional.of(testMethod.get().getAnnotation(Version.class).value());
        }
        return Optional.absent();
    }

    private String[] getAnnotatedIssues(String methodName) {
        Optional<Method> testMethod = getMethodCalled(methodName);
        if ((testMethod.isPresent()) && (testMethod.get().getAnnotation(Issues.class) != null)) {
            return testMethod.get().getAnnotation(Issues.class).value();
        }
        return new String[]{};
    }

    /**
     * Return a list of the issues mentioned in the Issue annotation of this method.
     * @param methodName the name of the test method in the Java test class, if applicable.
     * returns 
     */
    public Optional<String> getAnnotatedIssueForMethod(String methodName) {
        return getAnnotatedIssue(methodName);
    }

    public Optional<String> getAnnotatedVersionForMethod(String methodName) {
        return getAnnotatedVersion(methodName);
    }

    public String[] getAnnotatedIssuesForMethod(String methodName) {
        return getAnnotatedIssues(methodName);
    }

    public String getAnnotatedIssueForTestCase(Class<?> testCase) {
        Issue issueAnnotation = testCase.getAnnotation(Issue.class);
        if (issueAnnotation != null) {
            return issueAnnotation.value();
        } else {
            return null;
        }
    }

    public String getAnnotatedVersionForTestCase(Class<?> testCase) {
        Version versionAnnotation = testCase.getAnnotation(Version.class);
        if (versionAnnotation != null) {
            return versionAnnotation.value();
        } else {
            return null;
        }
    }

    public String[] getAnnotatedIssuesForTestCase(Class<?> testCase) {
        Issues issueAnnotation = testCase.getAnnotation(Issues.class);
        if (issueAnnotation != null) {
            return issueAnnotation.value();
        } else {
            return null;
        }
    }

    public List<String> getIssuesForMethod(String methodName) {
        List<String> issues = new ArrayList<String>();

        if (testClass != null) {
            addIssuesFromMethod(methodName, issues);
        } else {
            addIssuesFromTestScenarioName(methodName, issues);
        }
        return issues;
    }

    private void addIssuesFromTestScenarioName(String methodName, List<String> issues) {
        issues.addAll(getAnnotatedIssuesForMethodTitle(methodName));
    }

    private void addIssuesFromMethod(String methodName, List<String> issues) {
        if (getAnnotatedIssues(methodName) != null) {
            issues.addAll(Arrays.asList(getAnnotatedIssues(methodName)));
        }

        if (getAnnotatedIssue(methodName).isPresent()) {
            issues.add(getAnnotatedIssue(methodName).get());
        }

        if (getAnnotatedTitle(methodName) != null) {
            addIssuesFromTestScenarioName(methodName, issues);
        }
    }

    public List<TestTag> getTagsForMethod(String methodName) {

        List<TestTag> allTags = new ArrayList<>(getTags());
        allTags.addAll(getTagsFor(methodName));

        return ImmutableList.copyOf(allTags);
    }

    public List<TestTag> getTags() {
        return getTags(testClass);
    }

    private List<TestTag> getTags(Class<?> testClass) {
        List<TestTag> tags = new ArrayList<>();
        if (testClass != null) {
            addTagValues(tags, testClass.getAnnotation(WithTagValuesOf.class));
            addTags(tags, testClass.getAnnotation(WithTags.class));
            addTag(tags, testClass.getAnnotation(WithTag.class));
        }
        if (testClass.getSuperclass() != Object.class) {
            tags.addAll(getTags(testClass.getSuperclass()));
        }
        return tags;
    }

    private void addTag(List<TestTag> tags, WithTag tagAnnotation) {
        if (tagAnnotation != null) {
            tags.add(convertToTestTag(tagAnnotation));
        }
    }

    private void addTags(List<TestTag> tags, WithTags tagSet) {
        if (tagSet != null) {
            tags.addAll(convert(tagSet.value(), toTestTags()));
        }
    }

    private void addTagValues(List<TestTag> tags, WithTagValuesOf tagSet) {
        if (tagSet != null) {
            tags.addAll(convert(tagSet.value(), fromStringValuesToTestTags()));
        }
    }



    private List<TestTag> getTagsFor(String methodName) {
        List<TestTag> tags = new ArrayList<TestTag>();

        Optional<Method> testMethod = getMethodCalled(methodName);
        if (testMethod.isPresent()) {
            addTagValues(tags, testMethod.get().getAnnotation(WithTagValuesOf.class));
            addTags(tags, testMethod.get().getAnnotation(WithTags.class));
            addTag(tags, testMethod.get().getAnnotation(WithTag.class));
        }
        return tags;
    }

    public TestTag convertToTestTag(WithTag withTag) {
        if (StringUtils.isEmpty(withTag.value())) {
            return TestTag.withName(withTag.name()).andType(withTag.type());
        } else {
            return TestTag.withValue(withTag.value());
        }
    }

}
