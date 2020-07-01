package net.thucydides.core.requirements.model.cucumber;


import io.cucumber.messages.Messages.GherkinDocument.Feature;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario.Examples;
import net.thucydides.core.digest.Digest;
import net.thucydides.core.requirements.reports.cucumber.RenderCucumber;

import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithNoTitle;
import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithTitle;

public class IdentifiedScenario extends NamedScenario {
    private Feature feature;
    private String scenarioReport;
    private String scenarioId;
    private Feature.Scenario scenarioDefinition;
    private ExampleTableInMarkdown exampleTableInMarkdown;

    protected IdentifiedScenario(Feature feature, Scenario scenarioDefinition) {
        this.feature = feature;
        this.scenarioReport = ScenarioReport.forScenario(scenarioDefinition.getName()).inFeature(feature);
        this.scenarioId = Digest.ofTextValue(scenarioDefinition.getName());
        this.scenarioDefinition = scenarioDefinition;
        this.exampleTableInMarkdown = new ExampleTableInMarkdown(feature, scenarioReport, scenarioDefinition);
    }

    @Override
    public Optional<String> asGivenWhenThen() {
        return asGivenWhenThen(ScenarioDisplayOption.WithNoTitle);
    }

    @Override
    public Optional<String> asGivenWhenThen(ScenarioDisplayOption displayOption) {

        String renderedDescription = "";
        String suffix = "";
        if (displayOption == WithTitle) {
            renderedDescription = "**" + scenarioDefinition.getName() + "**  " + resultToken() + lineSeparator();
        } else {
            suffix = resultToken();
        }
        renderedDescription += scenarioDefinition.getStepsList().stream()
                        .map(step -> RenderCucumber.step(step) + "  ")
                        .collect(Collectors.joining(lineSeparator())) + suffix;

        renderedDescription += System.lineSeparator()
                               + "[<i class=\"fa fa-info-circle\"></i> More details](#" + scenarioId + ")"
                               + System.lineSeparator();

        return Optional.of("" + renderedDescription + "");

    }

    private String resultToken() {
        return "{result:" + feature.getName() + "!" + scenarioDefinition.getName() + "}";
    }

    @Override
    public Optional<String> asExampleTable() {
        return asExampleTable(WithNoTitle);
    }

    @Override
    public Optional<String> asExampleTable(ScenarioDisplayOption withDisplayOption) {
        if (scenarioDefinition.getExamplesCount() == 0) {
            return Optional.empty();
        }

        StringBuilder renderedExamples = new StringBuilder();

        int exampleRow = 0;
        for(Examples example : scenarioDefinition.getExamplesList()) {
            renderedExamples.append(exampleTableInMarkdown.renderedFormOf(example, exampleRow++, withDisplayOption));
            if (exampleRow < scenarioDefinition.getExamplesCount() - 1) {
                renderedExamples.append(lineSeparator());
            }
        }
        return Optional.of(renderedExamples.toString());
    }
}
