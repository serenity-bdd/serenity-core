package net.thucydides.core.annotations;

import net.thucydides.core.model.TestTag;
import net.thucydides.core.model.formatters.ReportFormatter;
import net.thucydides.core.tags.TagConverters;
import net.thucydides.core.util.JUnitAdapter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
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

    public java.util.Optional<String> getAnnotatedTitleForMethod(final String methodName) {
        if ((testClass != null) && (testClassHasMethodCalled(methodName))) {
            return getAnnotatedTitle(methodName);
        }
        return java.util.Optional.empty();
    }

    public boolean isPending(final String methodName) {
        java.util.Optional<Method> method = getMethodCalled(methodName);
        return method.isPresent() && isPending(method.get());
    }

    public static boolean isPending(final Method method) {
        return method != null && (method.getAnnotation(Pending.class) != null);
    }

    public static boolean isIgnored(final Method method) {
        return JUnitAdapter.isIgnored(method);
    }

    public static boolean shouldSkipNested(Method method) {
        if (method != null) {
            Step stepAnnotation = method.getAnnotation(Step.class);
            return ((stepAnnotation != null) && (!stepAnnotation.callNestedMethods()));
        }
        return false;
    }

    public static boolean isAnExampleStep(Method method) {
        if (method != null) {
            Step stepAnnotation = method.getAnnotation(Step.class);
            return ((stepAnnotation != null) && (stepAnnotation.exampleRow()));
        }
        return false;
    }

    public boolean isIgnored(final String methodName) {
        java.util.Optional<Method> method = getMethodCalled(methodName);
        return method.isPresent() && isIgnored(method.get());
    }

    private java.util.Optional<String> getAnnotatedTitle(String methodName) {
        java.util.Optional<Method> testMethod = getMethodCalled(methodName);
        if (testMethod.isPresent()) {
            Title titleAnnotation = testMethod.get().getAnnotation(Title.class);
            if (titleAnnotation != null) {
                return java.util.Optional.of(titleAnnotation.value());
            }
        }
        return java.util.Optional.empty();
    }

    private boolean testClassHasMethodCalled(final String methodName) {
        return (getMethodCalled(methodName).isPresent());

    }

    private java.util.Optional<Method> getMethodCalled(final String methodName) {
        if (testClass == null) {
            return java.util.Optional.empty();
        }
        String baseMethodName = withNoArguments(methodName);
        try {
            if (baseMethodName == null) {
                return java.util.Optional.empty();
            } else {
                return java.util.Optional.ofNullable(testClass.getMethod(baseMethodName));
            }
        } catch (NoSuchMethodException e) {
            return java.util.Optional.empty();
        }
    }

    /**
     * Return a list of the issues mentioned in the title annotation of this method.
     */
    List<String> getAnnotatedIssuesForMethodTitle(String methodName) {
        java.util.Optional<String> title = getAnnotatedTitleForMethod(methodName);
        return title.map(ReportFormatter::issuesIn).orElseGet(() -> ReportFormatter.issuesIn(methodName));
    }


    private Optional<String> getAnnotatedIssue(String methodName) {
        java.util.Optional<Method> testMethod = getMethodCalled(methodName);
        if ((testMethod.isPresent()) && (testMethod.get().getAnnotation(Issue.class) != null)) {
            return Optional.of(testMethod.get().getAnnotation(Issue.class).value());
        }
        return Optional.empty();
    }

    private Optional<String> getAnnotatedVersion(String methodName) {
        java.util.Optional<Method> testMethod = getMethodCalled(methodName);
        if ((testMethod.isPresent()) && (testMethod.get().getAnnotation(Version.class) != null)) {
            return Optional.of(testMethod.get().getAnnotation(Version.class).value());
        }
        return Optional.empty();
    }

    private String[] getAnnotatedIssues(String methodName) {
        java.util.Optional<Method> testMethod = getMethodCalled(methodName);
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

    List<String> getIssuesForMethod(String methodName) {
        List<String> issues = new ArrayList<>();

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
            issues.addAll(asList(getAnnotatedIssues(methodName)));
        }

        if (getAnnotatedIssue(methodName).isPresent()) {
            issues.add(getAnnotatedIssue(methodName).get());
        }

        if (getAnnotatedTitle(methodName) != null) {
            addIssuesFromTestScenarioName(methodName, issues);
        }
    }

    public List<TestTag> getTagsForMethod(String methodName) {

        List<TestTag> allTags = new ArrayList<>(getClassTags());
        allTags.addAll(getTagsFor(methodName));

        return new ArrayList<>(allTags);
    }

    public List<TestTag> getClassTags() {
        return getClassTags(testClass);
    }

    public List<TestTag> getAllTags() {
        Set<TestTag> classTags = new HashSet<>(getClassTags(testClass));
        classTags.addAll(
                Arrays.stream(testClass.getMethods())
                .filter(method -> method.getDeclaredAnnotation(WithTag.class) != null)
                .map(method -> method.getDeclaredAnnotation(WithTag.class).value() )
                .map(TestTag::withValue)
                .collect(Collectors.toSet()));

        classTags.addAll(
                Arrays.stream(testClass.getDeclaredMethods())
                        .filter(method -> method.getDeclaredAnnotation(WithTag.class) != null)
                        .map(method -> method.getDeclaredAnnotation(WithTag.class).value() )
                        .map(TestTag::withValue)
                        .collect(Collectors.toSet()));

        return new ArrayList<>(classTags);
    }

    private final List<TestTag> NO_TAGS = new ArrayList<>();

    private List<TestTag> getClassTags(Class<?> testClass) {
        List<TestTag> tags = new ArrayList<>();

        if (testClass == null) { return NO_TAGS; }

        addTagValues(tags, testClass.getAnnotation(WithTagValuesOf.class));
        addTags(tags, testClass.getAnnotation(WithTags.class));
        addTag(tags, testClass.getAnnotation(WithTag.class));
        if (testClass.getSuperclass() != Object.class) {
            tags.addAll(getClassTags(testClass.getSuperclass()));
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

            Set<TestTag> newTags = Arrays.stream(tagSet.value())
                    .map(TagConverters::convertToTestTag)
                    .collect(Collectors.toSet());

            tags.addAll(newTags);
        }
    }

    private void addTagValues(List<TestTag> tags, WithTagValuesOf tagSet) {
        if (tagSet != null) {

            Set<TestTag> newTags = Arrays.stream(tagSet.value())
                    .map(TestTag::withValue)
                    .collect(Collectors.toSet());

            tags.addAll(newTags);
        }
    }


    private List<TestTag> getTagsFor(String methodName) {
        List<TestTag> tags = new ArrayList<>();

        java.util.Optional<Method> testMethod = getMethodCalled(methodName);
        if (testMethod.isPresent()) {
            addTagValues(tags, testMethod.get().getAnnotation(WithTagValuesOf.class));
            addTags(tags, testMethod.get().getAnnotation(WithTags.class));
            addTag(tags, testMethod.get().getAnnotation(WithTag.class));
        }
        return tags;
    }

    private TestTag convertToTestTag(WithTag withTag) {
        if (StringUtils.isEmpty(withTag.value())) {
            return TestTag.withName(withTag.name()).andType(withTag.type());
        } else {
            return TestTag.withValue(withTag.value());
        }
    }

    public List<String> getTestMethodNames() {
        return Arrays.stream(testClass.getDeclaredMethods())
                        .filter(method -> hasAnnotationNamed(method,"Test"))
                        .map(Method::getName)
                        .collect(Collectors.toList());
    }

    private boolean hasAnnotationNamed(Method method, String annotationName) {
        return Arrays.stream(method.getAnnotations()).anyMatch(
                annotation -> annotation.toString().contains("." + annotationName + "(")
        );
    }
}
