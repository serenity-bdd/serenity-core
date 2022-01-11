package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.Scenario;
import net.thucydides.core.digest.Digest;
import net.thucydides.core.requirements.reports.cucumber.RenderCucumber;

import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithNoTitle;
import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithTitle;

public class IdentifiedScenario extends NamedScenario {
    private final Feature feature;
    private final String scenarioId;
    private final Scenario scenarioDefinition;
    private final ExampleTableInMarkdown exampleTableInMarkdown;

    protected IdentifiedScenario(Feature feature, Scenario scenarioDefinition) {
        this.feature = feature;
        String scenarioReport = ScenarioReport.forScenario(scenarioDefinition.getName()).inFeature(feature);
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
        renderedDescription += scenarioDefinition.getSteps().stream()
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
        if (scenarioDefinition.getExamples().isEmpty()) {
            return Optional.empty();
        }

        StringBuilder renderedExamples = new StringBuilder();

        int exampleRow = 0;
        for(Examples example : scenarioDefinition.getExamples()) {
            renderedExamples.append(exampleTableInMarkdown.renderedFormOf(example, exampleRow++, withDisplayOption));
            if (exampleRow < scenarioDefinition.getExamples().size() - 1) {
                renderedExamples.append(lineSeparator());
            }
        }
        return Optional.of(renderedExamples.toString());
    }
}
