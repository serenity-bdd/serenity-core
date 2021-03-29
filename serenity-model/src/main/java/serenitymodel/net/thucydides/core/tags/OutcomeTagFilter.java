package serenitymodel.net.thucydides.core.tags;

import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.reports.html.TestOutcomeFilter;
import serenitymodel.net.thucydides.core.requirements.reports.ScenarioOutcome;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;

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

