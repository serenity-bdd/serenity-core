package net.thucydides.core.annotations;


import net.serenitybdd.annotations.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class WhenReadingTestAndStepAnnotations {

    static final class SampleTestCase {
        public void normalTest(){}

        @Title("A title")
        public void normalTestWithTitle(){}

        @Title("A title")
        void nonPublicTestWithTitle() {}

        @Pending
        public void pendingTest(){}

        @Ignore
        public void skippedTest() {}

        @Title("Fixes #MYPROJECT-123 and #MYPROJECT-456")
        public void testWithIssuesInTitle(){}

        @Issue("#MYPROJECT-123")
        public void testWithAnnotatedIssue(){}

        @Issues({"#MYPROJECT-123","#MYPROJECT-456"})
        public void testWithAnnotatedIssues(){}

        @Title("Also fixes #MYPROJECT-1")
        @Issue("#MYPROJECT-2")
        @Issues({"#MYPROJECT-3","#MYPROJECT-4"})
        public void testWithLotsOfIssues(){}
    }

    @Test
    public void shouldReadMethodTitles() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class)
                                   .getAnnotatedTitleForMethod("normalTestWithTitle").get(), is("A title"));
    }

    @Test
    public void shouldReadMethodTitlesFromNonPublicTestMethod() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class)
                .getAnnotatedTitleForMethod("nonPublicTestWithTitle").get(), is("A title"));
    }

    @Test
    public void shouldReadNoAnnotatedIssuesIfNoneFound() {

        assertThat(TestAnnotations.forClass(SampleTestCase.class)
                .getAnnotatedIssuesForMethodTitle("normalTest").isEmpty(), is(true));
    }

    @Test
    public void shouldReadAnnotatedIssues() {

        assertThat(TestAnnotations.forClass(SampleTestCase.class)
                .getAnnotatedIssuesForMethodTitle("testWithIssuesInTitle"), allOf(hasItem("#MYPROJECT-123"),hasItem("#MYPROJECT-456")));
    }

    @Test
    public void shouldIdentifyPendingSteps() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class).isPending("pendingTest"), is(true));
    }

    @Test
    public void shouldIdentifyNonPendingSteps() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class).isPending("normalTest"), is(false));
    }

    @Test
    public void shouldIdentifySkippedSteps() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class).isIgnored("skippedTest"), is(true));
    }

    @Test
    public void shouldIdentifyNonSkippedSteps() {
        assertThat(TestAnnotations.forClass(SampleTestCase.class).isIgnored("normalTest"), is(false));
    }


    @Test
    public void shouldReadIssueAnnotationsFromATestClass() {
        String[] issues = TestAnnotations.forClass(SampleTestCase.class)
                                         .getAnnotatedIssuesForMethod("testWithAnnotatedIssues");

        assertThat(issues.length, is(2));
        assertThat(issues[0], is("#MYPROJECT-123"));
        assertThat(issues[1], is("#MYPROJECT-456"));
    }

    @Test
    public void shouldReadSingleIssueAnnotationFromATestClass() {
        java.util.Optional<String> issue = TestAnnotations.forClass(SampleTestCase.class)
                                         .getAnnotatedIssueForMethod("testWithAnnotatedIssue");

        assertThat(issue.get(), is("#MYPROJECT-123"));
    }

    @Test
    public void shouldReadMethodIssueFromATestClass() {
        List<String> issues = TestAnnotations.forClass(SampleTestCase.class)
                                         .getIssuesForMethod("testWithAnnotatedIssue");

        assertThat(issues.size(), is(1));
        assertThat(issues, hasItem("#MYPROJECT-123"));
    }

    @Test
    public void shouldReadMethodIssuesFromATestClass() {
        List<String> issues = TestAnnotations.forClass(SampleTestCase.class)
                                         .getIssuesForMethod("testWithAnnotatedIssues");

        assertThat(issues.size(), is(2));
        assertThat(issues, hasItems("#MYPROJECT-123", "#MYPROJECT-456"));
    }

    @Test
    public void shouldReadMethodIssuesInTitleFromATestClass() {
        List<String> issues = TestAnnotations.forClass(SampleTestCase.class)
                                         .getIssuesForMethod("testWithIssuesInTitle");

        assertThat(issues.size(), is(2));
        assertThat(issues, hasItems("#MYPROJECT-123", "#MYPROJECT-456"));
    }

    @Test
    public void shouldReadMultipleMethodIssuesInTitleFromATestClass() {
        List<String> issues = TestAnnotations.forClass(SampleTestCase.class)
                                         .getIssuesForMethod("testWithLotsOfIssues");

        assertThat(issues.size(), is(4));
        assertThat(issues, hasItems("#MYPROJECT-1", "#MYPROJECT-2", "#MYPROJECT-3", "#MYPROJECT-4"));
    }

}
