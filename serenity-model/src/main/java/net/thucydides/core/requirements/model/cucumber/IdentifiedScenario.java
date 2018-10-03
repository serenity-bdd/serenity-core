package net.thucydides.core.requirements.model.cucumber;

import gherkin.ast.*;
import net.thucydides.core.requirements.reports.cucumber.RenderCucumber;

import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithNoTitle;
import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithTitle;

public class IdentifiedScenario extends NamedScenario {
    private Feature feature;
    private String scenarioReport;
    private ScenarioDefinition scenarioDefinition;

    protected IdentifiedScenario(Feature feature, ScenarioDefinition scenarioDefinition) {
        this.feature = feature;
        this.scenarioReport = ScenarioReport.forScenario(scenarioDefinition.getName()).inFeature(feature);
        this.scenarioDefinition = scenarioDefinition;
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
                               + "[<i class=\"fa fa-info-circle\"></i> More details](" + scenarioReport + ")"
                               + System.lineSeparator();

        return Optional.of(renderedDescription);

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
        if (!(scenarioDefinition instanceof ScenarioOutline)) {
            return Optional.empty();
        }

        ScenarioOutline scenarioOutline = (ScenarioOutline) scenarioDefinition;

        return Optional.of(scenarioOutline.getExamples()
                .stream()
                .map(example -> renderedFormOf(example, withDisplayOption))
                .collect(Collectors.joining(lineSeparator())));
    }

    private String renderedFormOf(Examples exampleTable, ScenarioDisplayOption displayOption) {

        ExampleRowResultIcon exampleRowCounter = new ExampleRowResultIcon(feature.getName()
                                                                            + "!"
                                                                            +scenarioDefinition.getName());

        StringBuilder renderedExampleTable = new StringBuilder();

        String tableName = RenderedExampleTable.nameFor(exampleTable);
        if (tableName.isEmpty()) {
            tableName = scenarioDefinition.getName();
        }
        if (displayOption == WithTitle) {
            String exampleTitle = "### " + tableName;
            renderedExampleTable.append(exampleTitle);
        }
        renderedExampleTable.append(System.lineSeparator());
        renderedExampleTable.append(RenderedExampleTable.descriptionFor(exampleTable));
        renderedExampleTable.append(RenderedExampleTable.renderedTable(exampleTable, exampleRowCounter));
        renderedExampleTable.append(System.lineSeparator()).append("[<i class=\"fa fa-info-circle\"></i> More details](" + scenarioReport + ")").append(System.lineSeparator());

        return renderedExampleTable.toString();
    }

}
