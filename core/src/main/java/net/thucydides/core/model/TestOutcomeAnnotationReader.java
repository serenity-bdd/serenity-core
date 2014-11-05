package net.thucydides.core.model;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.reports.html.Formatter;

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

    public List<String> readIssues() {
        List<String> taggedIssues = Lists.newArrayList();
        if (testOutcome.getTestCase() != null) {
            addMethodLevelIssuesTo(taggedIssues);
            addClassLevelIssuesTo(taggedIssues);
        }
        addTitleLevelIssuesTo(taggedIssues);
        return taggedIssues;
    }


    public List<String> readVersions() {
        List<String> taggedVersions = Lists.newArrayList();
        if (testOutcome.getTestCase() != null) {
            addMethodLevelVersionsTo(taggedVersions);
            addClassLevelVersionsTo(taggedVersions);
        }
        return taggedVersions;
    }


    private void addClassLevelIssuesTo(List<String> issues) {
        String classIssue = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedIssueForTestCase(testOutcome.getTestCase());
        if (classIssue != null) {
            issues.add(classIssue);
        }
        String[] classIssues = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedIssuesForTestCase(testOutcome.getTestCase());
        if (classIssues != null) {
            issues.addAll(Arrays.asList(classIssues));
        }
    }

    private void addMethodLevelIssuesTo(List<String> issues) {
        Optional<String> issue = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedIssueForMethod(testOutcome.getMethodName());
        if (issue.isPresent()) {
            issues.add(issue.get());
        }
        String[] multipleIssues = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedIssuesForMethod(testOutcome.getMethodName());
        issues.addAll(Arrays.asList(multipleIssues));
    }

    private void addTitleLevelIssuesTo(List<String> issues) {
        List<String> titleIssues = Formatter.issuesIn(testOutcome.getTitle());
        if (!titleIssues.isEmpty()) {
            issues.addAll(titleIssues);
        }
    }


    private void addClassLevelVersionsTo(List<String> versions) {
        String classVersion = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedVersionForTestCase(testOutcome.getTestCase());
        if (classVersion != null) {
            versions.add(classVersion);
        }
    }

    private void addMethodLevelVersionsTo(List<String> versions) {
        Optional<String> version = TestAnnotations.forClass(testOutcome.getTestCase()).getAnnotatedVersionForMethod(testOutcome.getMethodName());
        if (version.isPresent()) {
            versions.add(version.get());
        }
    }


}
