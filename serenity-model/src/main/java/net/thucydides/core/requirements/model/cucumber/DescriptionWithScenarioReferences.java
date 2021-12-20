package net.thucydides.core.requirements.model.cucumber;

import io.cucumber.messages.types.Feature;

import static net.thucydides.core.requirements.model.cucumber.ScenarioDisplayOption.WithTitle;

public class DescriptionWithScenarioReferences {

    private static final String SCENARIO_PREFIX = "{Scenario}";
    private static final String SCENARIO_WITH_TITLE_PREFIX = "{Scenario!WithTitle}";
    private static final String EXAMPLES_PREFIX = "{Examples}";
    private static final String EXAMPLES_WITH_TITLE_PREFIX = "{Examples!WithTitle}";

    private final Feature feature;

    public DescriptionWithScenarioReferences(Feature feature) {
        this.feature = feature;
    }

    public String forText(String line) {
        if (line.trim().startsWith(SCENARIO_PREFIX)) {
            return ReferencedScenario.in(feature).withName(scenarioNameFrom(line,SCENARIO_PREFIX)).asGivenWhenThen()
                    .orElse(highlighted(line));
        }
        if (line.trim().startsWith(SCENARIO_WITH_TITLE_PREFIX)) {
            return ReferencedScenario.in(feature).withName(scenarioNameFrom(line,SCENARIO_WITH_TITLE_PREFIX)).asGivenWhenThen(WithTitle)
                    .orElse(highlighted(line));
        }

        if (line.trim().startsWith(EXAMPLES_PREFIX)) {
            return ReferencedScenario.in(feature).withName(scenarioNameFrom(line,EXAMPLES_PREFIX)).asExampleTable()
                    .orElse(ReferencedExampleTable.in(feature).withName(scenarioNameFrom(line,EXAMPLES_PREFIX)).asExampleTable()
                            .orElse(highlighted(line)))
                    ;
        }

        if (line.trim().startsWith(EXAMPLES_WITH_TITLE_PREFIX)) {
            return ReferencedScenario.in(feature).withName(scenarioNameFrom(line,EXAMPLES_WITH_TITLE_PREFIX)).asExampleTable(WithTitle)
                    .orElse(ReferencedExampleTable.in(feature).withName(scenarioNameFrom(line,EXAMPLES_WITH_TITLE_PREFIX)).asExampleTable(WithTitle)
                            .orElse(highlighted(line)))
                    ;
        }

        return line;
    }

    private String highlighted(String line) {
        return "<span class=\"missing-scenario-warning\">" + line + "</span>";
    }

    public static DescriptionWithScenarioReferences from(Feature feature) { return new DescriptionWithScenarioReferences(feature); }

    private String scenarioNameFrom(String line, String prefix) {
        return line.substring(line.indexOf(prefix) + prefix.length() + 1);
    }

}
