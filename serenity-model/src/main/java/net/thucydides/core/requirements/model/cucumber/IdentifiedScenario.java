package net.thucydides.core.requirements.model.cucumber;

import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;

import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

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
        String renderedDescription =
                " > **" + scenarioDefinition.getName() + "**  " + resultToken() + lineSeparator()
                + scenarioDefinition.getSteps().stream()
                        .map(step -> " > " + step.getKeyword() + withEscapedParameterFields(step.getText()) + "  ")
                        .collect(Collectors.joining(lineSeparator()))
        ;

        renderedDescription += System.lineSeparator()
                               + "[<i class=\"fa fa-info-circle\"></i> More details](" + scenarioReport + ")"
                               + System.lineSeparator();

        return Optional.of(renderedDescription);

    }

    private String resultToken() {
        return "{result:" + feature.getName() + "!" + scenarioDefinition.getName() + "}";
    }

    private String withEscapedParameterFields(String text) {
        return text.replaceAll("<","&lt;").replaceAll(">","&gt;");
    }

    @Override
    public Optional<String> asExampleTable() {
        if (!(scenarioDefinition instanceof ScenarioOutline)) {
            return Optional.empty();
        }

        ScenarioOutline scenarioOutline = (ScenarioOutline) scenarioDefinition;

        return Optional.of(scenarioOutline.getExamples()
                .stream()
                .map(this::renderedFormOf)
                .collect(Collectors.joining(lineSeparator())));
    }

    private String renderedFormOf(Examples exampleTable) {

        ExampleRowResultIcon exampleRowCounter = new ExampleRowResultIcon(feature.getName()
                                                                            + "!"
                                                                            +scenarioDefinition.getName());

        StringBuilder renderedExampleTable = new StringBuilder();

        String tableName = RenderedExampleTable.nameFor(exampleTable);
        if (tableName.isEmpty()) {
            tableName = scenarioDefinition.getName();
        }
        String exampleTitle =  "### " + tableName;
        renderedExampleTable.append(exampleTitle);
        renderedExampleTable.append(RenderedExampleTable.descriptionFor(exampleTable));
        renderedExampleTable.append(RenderedExampleTable.renderedTable(exampleTable, exampleRowCounter));
        renderedExampleTable.append(System.lineSeparator()).append("[<i class=\"fa fa-info-circle\"></i> More details](" + scenarioReport + ")").append(System.lineSeparator());

        return renderedExampleTable.toString();
    }

}
