package net.thucydides.model.domain;

import net.serenitybdd.annotations.TestAnnotations;
import net.thucydides.model.domain.formatters.ReportFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TestOutcomeAnnotationReader {

    private final TestOutcome testOutcome;

    public static TestOutcomeAnnotationReader forTestOutcome(TestOutcome testOutcome) {
        return new TestOutcomeAnnotationReader(testOutcome);
    }

    TestOutcomeAnnotationReader(TestOutcome testOutcome) {
        this.testOutcome = testOutcome;
    }

    public static List<String> readIssuesIn(TestOutcome testOutcome) {
        List<String> taggedIssues = new ArrayList<>();
        if (testOutcome.getTestCase() != null) {
            addMethodLevelIssuesTo(testOutcome, taggedIssues);
            addClassLevelIssuesTo(testOutcome, taggedIssues);
        }
        addTitleLevelIssuesTo(testOutcome, taggedIssues);
        return taggedIssues;
    }

    public static List<String> readVersionsIn(TestOutcome testOutcome) {
        List<String> taggedVersions = new ArrayList<>();
        if (testOutcome.getTestCase() != null) {
            addMethodLevelVersionsTo(testOutcome, taggedVersions);
            addClassLevelVersionsTo(testOutcome, taggedVersions);
        }
        return taggedVersions;
    }

    private static void addClassLevelIssuesTo(TestOutcome testOutcome, List<String> issues) {
        String classIssue = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedIssueForTestCase(testOutcome.getTestCase());
        if (classIssue != null) {
            issues.add(classIssue);
        }
        String[] classIssues = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedIssuesForTestCase(testOutcome.getTestCase());
        if (classIssues != null) {
            issues.addAll(Arrays.asList(classIssues));
        }
    }

    private static void addMethodLevelIssuesTo(TestOutcome testOutcome, List<String> issues) {
        java.util.Optional<String> issue = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedIssueForMethod(testOutcome.getName());
        issue.ifPresent(issues::add);
        String[] multipleIssues = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedIssuesForMethod(testOutcome.getName());
        issues.addAll(Arrays.asList(multipleIssues));
    }

    private static void addTitleLevelIssuesTo(TestOutcome testOutcome, List<String> issues) {
        List<String> titleIssues = ReportFormatter.issuesIn(testOutcome.getTitle());
        if (!titleIssues.isEmpty()) {
            issues.addAll(titleIssues);
        }
    }


    private static void addClassLevelVersionsTo(TestOutcome testOutcome, List<String> versions) {
        String classVersion = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedVersionForTestCase(testOutcome.getTestCase());
        if (classVersion != null) {
            versions.add(classVersion);
        }
    }

    private static void addMethodLevelVersionsTo(TestOutcome testOutcome, List<String> versions) {
        java.util.Optional<String> version = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedVersionForMethod(testOutcome.getName());
        version.ifPresent(versions::add);
    }


}
