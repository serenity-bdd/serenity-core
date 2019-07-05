package net.thucydides.core.tags;

import net.serenitybdd.core.tags.EnvironmentDefinedTags;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.reports.ScenarioOutcome;
import net.thucydides.core.requirements.reports.ScenarioOutcomes;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OutcomeTagFilter {
    private final EnvironmentVariables environmentVariables;
    private final List<TestTag> tags;

    public OutcomeTagFilter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        tags = EnvironmentDefinedTags.definedIn(environmentVariables);
    }

    public List<ScenarioOutcome> scenariosFilteredByTagIn(List<ScenarioOutcome> testOutcomes) {

        if (tags.isEmpty()) return testOutcomes;

        return testOutcomes.stream()
                .filter(scenarioOutcome -> hasMatchingTags(scenarioOutcome.getTags(), tags))
                .collect(Collectors.toList());
    }

   public List<? extends TestOutcome> outcomesFilteredByTagIn(List<? extends TestOutcome> testOutcomes) {
       if (tags.isEmpty()) return testOutcomes;

       return testOutcomes.stream()
               .filter(outcome -> hasMatchingTags(outcome.getTags(), tags))
               .collect(Collectors.toList());

   }

    private boolean hasMatchingTags(Set<TestTag> outcomeTags, List<TestTag> filteredTags) {
        return outcomeTags.stream().anyMatch(
                filteredTags::contains
        );
    }
}
