package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.cucumber.FeatureFileScenarioOutcomes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScenarioOutcomes {

    public static List<ScenarioOutcome> from(RequirementsOutcomes requirementsOutcomes) {
        if (requirementsOutcomes.getParentRequirement().isPresent() && isAFeature(requirementsOutcomes.getParentRequirement().get())) {
            return scenariosFrom(requirementsOutcomes.getParentRequirement().get(), requirementsOutcomes);
        } else {
            return requirementsOutcomes.getTestOutcomes().getOutcomes().stream()
                    .map(ScenarioOutcomes::outcomeFrom)
                    .collect(Collectors.toList());
        }
    }

    public static List<ScenarioOutcome> from(TestOutcomes testOutcomes) {
        return testOutcomes.getOutcomes().stream()
                .map(ScenarioOutcomes::outcomeFrom)
                .collect(Collectors.toList());
    }

    private static boolean isAFeature(Requirement parentRequirement) {
        return parentRequirement.getFeatureFileName() != null;
    }

    private static List<ScenarioOutcome> scenariosFrom(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {

        List<ScenarioOutcome> scenarioOutcomes = FeatureFileScenarioOutcomes.from(requirement).forOutcomesIn(requirementsOutcomes);

        if (scenarioOutcomes.isEmpty()) {
            scenarioOutcomes  = requirementsOutcomes.getTestOutcomes().getTests().stream()
                                                    .map(ScenarioOutcomes::outcomeFrom)
                                                    .collect(Collectors.toList());
        }

        return scenarioOutcomes;

    }

    private static ScenarioOutcome outcomeFrom(TestOutcome testOutcome) {

        List<String> exampleTables = (testOutcome.isDataDriven()) ?
                Collections.singletonList(testOutcome.getDataTable().toMarkdown()) : Collections.EMPTY_LIST;

        String userStoryName = (testOutcome.getUserStory() != null) ? testOutcome.getUserStory().getName() : null;
        String userStoryReportName = (testOutcome.getUserStory() != null) ? testOutcome.getUserStory().getReportName() : null;

        List<String> steps = (testOutcome.getDataDrivenSampleScenario() != null && !testOutcome.getDataDrivenSampleScenario().isEmpty()) ?
                testStepsFromSampleScenario(testOutcome.getDataDrivenSampleScenario()) :
                testOutcome.getTestSteps()
                        .stream().map(step -> RenderMarkdown.convertEmbeddedTablesIn(step.getDescription())).collect(Collectors.toList());


        return new ScenarioOutcome(testOutcome.getTitleWithLinks(),
                "Acceptance Test",
                testOutcome.getResult(),
                ReportNamer.forReportType(ReportType.HTML).getNormalizedTestNameFor(testOutcome),
                testOutcome.getStartTime(),
                testOutcome.getDuration(),
                testOutcome.isManual(),
                testOutcome.getDescription(),
                steps,
                exampleTables,
                testOutcome.getDataTableRowCount(),
                userStoryName,
                userStoryReportName);
    }

    private static List<String> testStepsFromSampleScenario(String sampleDataDrivenScenario) {
        return Arrays.asList(sampleDataDrivenScenario.split("\\R"));
    }

}
