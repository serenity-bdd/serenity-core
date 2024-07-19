package net.thucydides.model.reports;


import net.thucydides.model.domain.TestOutcomeSummary;

import java.io.IOException;
import java.util.List;

public interface JiraUpdaterService
{
    /**
     * Loads test results from outcome directory and updates Jira comments and workflow.
     * @param outcomeDirectory  - test outcome directory.
     * @throws IOException
     */
    public List<TestOutcomeSummary> updateJiraForTestResultsFrom(String outcomeDirectory) throws IOException;

    /**
     *  Loads test results from outcome directory (matching the given regular expression filter for TestOutcomes names)
     *  and updates Jira comments and workflow.
     *
     * @param outcomeDirectory test outcome directory.
     * @param outcomesNameFilter - regular expression filter for the outcomes names.
     * @throws IOException
     */
    public List<TestOutcomeSummary> updateJiraForTestResultsFrom(String outcomeDirectory, String outcomesNameFilter) throws IOException;
}
