package net.serenitybdd.plugins.jira;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


public class TestResultTally<T> {
    
    private final ConcurrentMap<String, List<T>> testOutcomesTally;

    public TestResultTally() {
        this.testOutcomesTally = Maps.newConcurrentMap();
    }

    public synchronized void recordResult(String issueNumber, T outcome) {
        getTestOutcomeListForIssue(issueNumber).add(outcome);
    }

    public List<T> getTestOutcomesForIssue(String issueNumber) {
       return ImmutableList.copyOf(getTestOutcomeListForIssue(issueNumber));
    }

    protected List<T> getTestOutcomeListForIssue(final String issueNumber) {
        List<T> resultTallyForIssue = testOutcomesTally.get(issueNumber);
        if (resultTallyForIssue == null) {
            testOutcomesTally.putIfAbsent(issueNumber, new Vector<>());
        }
        return testOutcomesTally.get(issueNumber);
    }
    
    public TestResult getResultForIssue(final String issueNumber) {
        List<T> testOutcomesForThisIssue = testOutcomesTally.get(issueNumber);
        if ( testOutcomesForThisIssue.stream().allMatch(TestOutcome.class::isInstance)) {
            List<TestOutcome> testOutcomes = (List<TestOutcome>)testOutcomesTally.get(issueNumber);
            List<TestResult> results =  testOutcomes.stream().map(TestOutcome::getResult).collect(Collectors.toList());
            return TestResultList.overallResultFrom(results);
        } else {
            return TestResultList.overallResultFrom(new ArrayList<>());
        }
    }

    public Set<String> getIssues() {
        return testOutcomesTally.keySet();
    }
}
