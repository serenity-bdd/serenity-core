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

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;

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
        String userStoryName = (testOutcome.getUserStory() != null) ? testOutcome.getUserStory().getName() : null;
        String userStoryReportName = (testOutcome.getUserStory() != null) ? testOutcome.getUserStory().getReportName() : null;

        if (testOutcome.isDataDriven()) {
            return dataDrivenOutcomeFrom(userStoryName, userStoryReportName, testOutcome, requirements);
        } else {
            return singleScenarioOutlineFrom(userStoryName, userStoryReportName, testOutcome, requirements);
        }
    }

    public static ScenarioOutcome singleScenarioOutlineFrom(String userStoryName,
                                                            String userStoryReportName,
                                                            TestOutcome testOutcome,
                                                            RequirementsService requirements) {

        List<String> steps = testOutcome.getTestSteps().stream()
                .map(step -> RenderMarkdown.convertEmbeddedTablesIn(step.getDescription()))
                .collect(Collectors.toList());

        List<TestTag> scenarioTags = getTestTags(requirements, testOutcome);

        return new SingleScenarioOutcome(
//                testOutcome.getQualified().withContext().getTitleWithLinks(),
                testOutcome.getQualified().getTitleWithLinks(),
                testOutcome.getTitle(),
                "Scenario",
                testOutcome.getResult(),
                ReportNamer.forReportType(ReportType.HTML).getNormalizedReportNameFor(testOutcome),
                testOutcome.getStartTime(),
                testOutcome.getDuration(),
                testOutcome.isManual(),
                testOutcome.getDescription(),
                steps,
                new ArrayList<>(),
                testOutcome.getDataTableRowCount(),
                userStoryName,
                userStoryReportName,
                testOutcome.getTags(),
                testOutcome.getRule(),
                testOutcome.getExternalLink(),
                scenarioTags,
                testOutcome.getContext());
    }

    private static List<TestTag> getTestTags(RequirementsService requirements, TestOutcome testOutcome) {
        Optional<Requirement> requirement = requirements.getParentRequirementFor(testOutcome);
        List<TestTag> scenarioTags = new ArrayList<>();
        if (requirement.isPresent() && requirement.get().getScenarioTags() != null) {
            if (requirement.get().getScenarioTags().get(testOutcome.getName()) != null) {
                scenarioTags.addAll(requirement.get().getScenarioTags().get(testOutcome.getName()));
            }
        }
        return scenarioTags;
    }

    public static ScenarioOutcome dataDrivenOutcomeFrom(String userStoryName,
                                                        String userStoryReportName,
                                                        TestOutcome testOutcome,
                                                        RequirementsService requirements) {
//        String featureName = (testOutcome.getUserStory() != null) ? testOutcome.getUserStory().getName() : "";
//        String scenarioName = testOutcome.getName();
//        List<String> exampleTables = (testOutcome.getDataTable().isEmpty()) ?
//                EMPTY_LIST : Collections.singletonList(testOutcome.getDataTable().toMarkdown(featureName, scenarioName));

        List<String> steps = (testOutcome.getDataDrivenSampleScenario() != null && !testOutcome.getDataDrivenSampleScenario().isEmpty()) ?
                testStepsFromSampleScenario(testOutcome.getDataDrivenSampleScenario()) :
                testOutcome.getTestSteps()
                        .stream().map(step -> RenderMarkdown.convertEmbeddedTablesIn(step.getDescription())).collect(Collectors.toList());

        List<TestTag> scenarioTags = getTestTags(requirements, testOutcome);

        List<String> reportBadges = ReportBadges.from(Collections.singletonList(testOutcome), testOutcome.getTitle());

        List<ExampleOutcome> exampleOutcomes = ExampleOutcomes.from(testOutcome);
        return new ScenarioSummaryOutcome(
//                testOutcome.getQualified().withContext().getTitleWithLinks(),
                testOutcome.getQualified().getTitleWithLinks(),
                "Scenario Outline",
                testOutcome.getResult(),
                reportBadges,
                ReportNamer.forReportType(ReportType.HTML).getNormalizedReportNameFor(testOutcome),
                testOutcome.getDescription(),
                steps,
                EMPTY_LIST, // No rendered examples available here
                exampleOutcomes,
                exampleOutcomes.size(),
                testOutcome.isManual(),
                userStoryName,
                userStoryReportName,
                testOutcome.getTags(),
                new HashMap<>(), // No known example tags here
                testOutcome.getRule(),
                testOutcome.getStartTime(),
                testOutcome.getDuration(),
                scenarioTags,
                testOutcome.getContext());
    }

    private static List<String> testStepsFromSampleScenario(String sampleDataDrivenScenario) {
        return DescriptionSplitter.splitIntoSteps(sampleDataDrivenScenario).stream().map(
                RenderMarkdown::convertEmbeddedTablesIn
        ).collect(Collectors.toList());
    }
}
