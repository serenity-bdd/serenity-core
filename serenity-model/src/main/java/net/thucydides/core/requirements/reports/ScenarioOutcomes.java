package net.thucydides.core.requirements.reports;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.DescriptionSplitter;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.cucumber.FeatureFileScenarioOutcomes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    public static List<ScenarioOutcome> from(TestOutcomes testOutcomes, RequirementsService requirements) {
        return testOutcomes.getOutcomes().stream()
                .map(outcome -> outcomeFrom(outcome, requirements))
                .collect(Collectors.toList());
    }

    private static boolean isAFeature(Requirement parentRequirement) {
        return parentRequirement.getFeatureFileName() != null;
    }

    private static List<ScenarioOutcome> scenariosFrom(Requirement requirement, RequirementsOutcomes requirementsOutcomes) {

        List<ScenarioOutcome> scenarioOutcomes = FeatureFileScenarioOutcomes.from(requirement).forOutcomesIn(requirementsOutcomes);

        if (scenarioOutcomes.isEmpty()) {
            scenarioOutcomes = requirementsOutcomes.getTestOutcomes().getTests().stream()
                    .map(ScenarioOutcomes::outcomeFrom)
                    .collect(Collectors.toList());
        }

        return scenarioOutcomes;

    }

    public static ScenarioOutcome outcomeFrom(TestOutcome testOutcome) {
        return outcomeFrom(testOutcome, Injectors.getInjector().getInstance(RequirementsService.class));
    }

    public static ScenarioOutcome outcomeFrom(TestOutcome testOutcome, RequirementsService requirements) {

        String featureName = (testOutcome.getUserStory() != null) ? testOutcome.getUserStory().getName() : "";
        String scenarioName = testOutcome.getName();
        List<String> exampleTables = (testOutcome.isDataDriven()) ?
                Collections.singletonList(testOutcome.getDataTable().toMarkdown(featureName, scenarioName)) : Collections.EMPTY_LIST;

        String userStoryName = (testOutcome.getUserStory() != null) ? testOutcome.getUserStory().getName() : null;
        String userStoryReportName = (testOutcome.getUserStory() != null) ? testOutcome.getUserStory().getReportName() : null;

        List<String> steps = (testOutcome.getDataDrivenSampleScenario() != null && !testOutcome.getDataDrivenSampleScenario().isEmpty()) ?
                testStepsFromSampleScenario(testOutcome.getDataDrivenSampleScenario()) :
                testOutcome.getTestSteps()
                        .stream().map(step -> RenderMarkdown.convertEmbeddedTablesIn(step.getDescription())).collect(Collectors.toList());

        Optional<Requirement> requirement = requirements.getParentRequirementFor(testOutcome);
        List<TestTag> scenarioTags = new ArrayList<>();
        if (requirement.isPresent() && requirement.get().getScenarioTags() != null) {
            if (requirement.get().getScenarioTags().get(testOutcome.getName()) != null) {
                scenarioTags.addAll(requirement.get().getScenarioTags().get(testOutcome.getName()));
            }
        }
        return new SingleScenarioOutcome(
                testOutcome.getQualified().withContext().getTitleWithLinks(),
                testOutcome.getTitle(),
                "Scenario",
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
                userStoryReportName,
                testOutcome.getTags(),
                testOutcome.getRule(),
                testOutcome.getExternalLink(),
                scenarioTags);
    }

    private static List<String> testStepsFromSampleScenario(String sampleDataDrivenScenario) {
        return DescriptionSplitter.splitIntoSteps(sampleDataDrivenScenario).stream().map(
                RenderMarkdown::convertEmbeddedTablesIn
        ).collect(Collectors.toList());
    }

}
