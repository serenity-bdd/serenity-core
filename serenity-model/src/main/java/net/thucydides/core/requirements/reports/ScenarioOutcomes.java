package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.cucumber.FeatureFileScenarioOutcomes;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScenarioOutcomes {
    public static List<ScenarioOutcome> from(RequirementsOutcomes requirementsOutcomes) {
        if (requirementsOutcomes.getParentRequirement().isPresent()) {
            return scenariosFrom(requirementsOutcomes.getParentRequirement().get(), requirementsOutcomes);
        } else {
            return requirementsOutcomes.getRequirementOutcomes().stream()
                    .map(ScenarioOutcomes::outcomeFrom)
                    .collect(Collectors.toList());
        }
    }

    // TODO: Fix the links for non-Cucumber scenrios
    private static List<ScenarioOutcome> scenariosFrom(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {

        List<ScenarioOutcome> scenarioOutcomes = FeatureFileScenarioOutcomes.from(requirement).forOutcomesIn(requirementsOutcomes);

        if (scenarioOutcomes.isEmpty()) {
            scenarioOutcomes  = requirementsOutcomes.getTestOutcomes().getTests().stream()
                                                    .map(testOutcome -> outcomeFrom(testOutcome))
                                                    .collect(Collectors.toList());
        }

        return scenarioOutcomes;

//        if (requirement.getType().equalsIgnoreCase("feature")) {
//            return FeatureFileScenarioOutcomes.from(requirement).forOutcomesIn(requirementsOutcomes);
//        } else {
//            return requirement.getChildren().stream()
//                    .map(scenario -> new ScenarioOutcome(scenario.getName(),"Scenario",
//                                                         requirementsOutcomes.getTestResultForTestNamed(scenario.getName())
//                                                                             .orElse(TestResult.UNDEFINED),
//                                            "#",
//                            null,
//                            0L))
//                    .collect(Collectors.toList());
//        }
    }


    // TODO: Fix the links for non-Cucumber scenrios
    private static ScenarioOutcome outcomeFrom(RequirementOutcome requirementOutcome) {

        ReportNameProvider report = new ReportNameProvider();

        return new ScenarioOutcome(requirementOutcome.getRequirement().getDisplayName(),
                requirementOutcome.getRequirement().getType(),
                requirementOutcome.getTestOutcomes().getResult(),
                report.forRequirement(requirementOutcome.getRequirement()),
                null,
                0L);
    }

    private static ScenarioOutcome outcomeFrom(TestOutcome testOutcome) {

        return new ScenarioOutcome(testOutcome.getTitleWithLinks(),
                "Accepttance Test",
                testOutcome.getResult(),
                testOutcome.getHtmlReport(),
                testOutcome.getStartTime(),
                testOutcome.getDuration(),
                testOutcome.isManual(),
                testOutcome.getDescription(),
                testOutcome.getTestSteps().stream().map(step -> step.getDescription()).collect(Collectors.toList()),
                Collections.EMPTY_LIST);
    }

}
