package net.thucydides.model.tags;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.reports.html.TestOutcomeFilter;
import net.thucydides.model.requirements.reports.ScenarioOutcome;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.List;
import java.util.stream.Collectors;

public class OutcomeTagFilter {
    private final EnvironmentVariables environmentVariables;

    private final TestOutcomeFilter testOutcomeFilter;
    public OutcomeTagFilter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.testOutcomeFilter = new TestOutcomeFilter(environmentVariables);
    }

    public List<ScenarioOutcome> scenariosFilteredByTagIn(List<ScenarioOutcome> scenarioOutcomes) {
        return scenarioOutcomes.stream()
                .filter(testOutcomeFilter::matches)
                .collect(Collectors.toList());

    }

   public List<? extends TestOutcome> outcomesFilteredByTagIn(List<? extends TestOutcome> testOutcomes) {
       return testOutcomes.stream()
               .filter(testOutcomeFilter::matches)
               .collect(Collectors.toList());
   }
}

