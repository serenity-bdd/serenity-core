package net.thucydides.core.requirements;

import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.reports.ScenarioOutcome;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RequirementsFilter {
    private Set<String> requirementTypesAppearingInScenarios;

    public RequirementsFilter(List<ScenarioOutcome> scenarios) {
        requirementTypesAppearingInScenarios =
                scenarios.stream()
                        .flatMap(scenarioOutcome -> scenarioOutcome.getTags().stream())
                        .map(TestTag::getType)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());
    }

    public static RequirementsFilter onlyRequirementsUsedIn(List<ScenarioOutcome> scenarios) {
        return new RequirementsFilter(scenarios);
    }

    public List<String> from(List<String> requirementTypes) {
        return requirementTypes.stream()
                .filter(this::aScenarioExistsWithARequirementsTagOfType)
                .collect(Collectors.toList());
    }

    private boolean aScenarioExistsWithARequirementsTagOfType(String requirementType) {
        return requirementTypesAppearingInScenarios.contains(requirementType.toLowerCase());
    }
}
