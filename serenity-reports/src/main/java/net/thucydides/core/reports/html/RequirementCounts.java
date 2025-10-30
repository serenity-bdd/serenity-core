package net.thucydides.core.reports.html;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.requirements.reports.RequirementsOutcomes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequirementCounts {
    private final RequirementsOutcomes outcomes;

    public RequirementCounts(RequirementsOutcomes outcomes) {
        this.outcomes = outcomes;
    }

    public static RequirementCounts forOutcomesIn(RequirementsOutcomes outcomes) {
        return new RequirementCounts(outcomes);
    }

    /**
     * Returns automated and manual result counts of each of the specified result types
     */
    public String byType() {
        List<Integer> results = new ArrayList<>();

        results.add(outcomes.getCompletedRequirementsCount());
        results.add(outcomes.getPendingRequirementsCount());
        results.add(outcomes.getIgnoredRequirementsCount());
        results.add(outcomes.getSkippedRequirementsCount());
        results.add(outcomes.getFailingRequirementsCount());
        results.add(outcomes.getErrorRequirementsCount());
        results.add(outcomes.getCompromisedRequirementsCount());

        return Arrays.toString(results.toArray());
    }

    public String percentageLabelsByType() {
        List<String> resultLabels = new ArrayList<>();

        resultLabels.add(ifNotEmpty(outcomes.getFormattedPercentage().withResult(TestResult.SUCCESS)));
        resultLabels.add(ifNotEmpty(outcomes.getFormattedPercentage().withResult(TestResult.PENDING)));
        resultLabels.add(ifNotEmpty(outcomes.getFormattedPercentage().withResult(TestResult.IGNORED)));
        resultLabels.add(ifNotEmpty(outcomes.getFormattedPercentage().withResult(TestResult.SKIPPED)));
        resultLabels.add(ifNotEmpty(outcomes.getFormattedPercentage().withResult(TestResult.FAILURE)));
        resultLabels.add(ifNotEmpty(outcomes.getFormattedPercentage().withResult(TestResult.ERROR)));
        resultLabels.add(ifNotEmpty(outcomes.getFormattedPercentage().withResult(TestResult.COMPROMISED)));

        return Arrays.toString(resultLabels.toArray());
    }

    private String ifNotEmpty(String result) {
        return (result.equals("0.0%") ? " " : "'" + result + "'");
    }


}
