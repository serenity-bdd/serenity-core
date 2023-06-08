package net.thucydides.core.requirements.reports.cucumber;


import io.cucumber.messages.types.*;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.CucumberTagConverter;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.cucumber.AnnotatedFeature;
import net.thucydides.core.requirements.reports.*;
import net.thucydides.core.tags.TagScanner;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static net.thucydides.core.reports.html.CucumberTagConverter.fromGherkinTags;


public class FeatureFileScenarioOutcomes {

    private final Requirement requirement;
    private final EnvironmentVariables environmentVariables;
    private final TagScanner tagScanner;

    public FeatureFileScenarioOutcomes(Requirement requirement, EnvironmentVariables environmentVariables) {
        this.requirement = requirement;
        this.environmentVariables = environmentVariables;
        this.tagScanner = new TagScanner(environmentVariables);
    }

    public FeatureFileScenarioOutcomes(Requirement requirement) {
        this(requirement, SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    public static FeatureFileScenarioOutcomes from(Requirement requirement) {
        return new FeatureFileScenarioOutcomes(requirement);
    }

    private String normalizedFormOf(String path) {
        return path.replace("\\", "/");
    }

    public List<ScenarioOutcome> forOutcomesIn(RequirementsOutcomes requirementsOutcomes) {
        Optional<AnnotatedFeature> feature = FeatureCache.getCache().loadFeature(pathFromResourceOnClasspath(normalizedFormOf(requirement.getPath())));

        if (!feature.isPresent()) {
            return Collections.emptyList();
        } else {
            List<ScenarioOutcome> scenarioOutcomes = new ArrayList<>();
            Feature currentFeature = feature.get().getFeature();
            for (FeatureChild currentChild : currentFeature.getChildren()) {
                if (currentChild.getRule() != null && currentChild.getRule().isPresent()) {
                    Rule currentRule = currentChild.getRule().get();
                    currentRule.getChildren().stream()
                            .filter(ruleChild -> ruleChild.getScenario() != null && ruleChild.getScenario().isPresent())
                            .forEach(
                                    ruleChild -> scenarioOutcomes.add(
                                            scenarioOutcomeFrom(currentFeature,
                                                    ruleChild.getScenario().get(),
                                                    requirementsOutcomes.getTestOutcomes(),
                                                    net.thucydides.core.model.Rule.from(currentRule)))
                            );

                } else {
                    if (currentChild.getScenario() != null && currentChild.getScenario().isPresent()) {
                        scenarioOutcomes.add(
                                scenarioOutcomeFrom(currentFeature,
                                        currentChild.getScenario().get(),
                                        requirementsOutcomes.getTestOutcomes()));
                    }
                    if (currentChild.getBackground() != null && currentChild.getBackground().isPresent()) {
                        scenarioOutcomes.add(
                                scenarioBackgroundFrom(currentFeature,
                                        currentChild.getBackground().get(),
                                        requirementsOutcomes.getTestOutcomes()));
                    }
                }
            }
            return scenarioOutcomes;
        }
    }


    private ScenarioOutcome scenarioOutcomeFrom(Feature feature,
                                                Scenario scenario,
                                                TestOutcomes testOutcomes) {
        return scenarioOutcomeFrom(feature, scenario, testOutcomes, null);
    }


    private ScenarioOutcome scenarioBackgroundFrom(Feature feature,
                                                   Background scenario,
                                                   TestOutcomes testOutcomes) {

        List<TestOutcome> outcomes = testOutcomes.testOutcomesWithName(scenario.getName());

        String scenarioTitle = scenario.getName();

        TestResult result = (outcomes.isEmpty()) ? TestResult.UNDEFINED :
                TestResultList.overallResultFrom(outcomes.stream().map(TestOutcome::getResult).collect(Collectors.toList()));

        List<String> reportBadges = ReportBadges.from(outcomes, scenario.getName());

        String featureReport = new ReportNameProvider().forRequirement(feature.getName(), "feature");
        Optional<String> scenarioReport = (outcomes.isEmpty()) ? Optional.empty() : Optional.of(outcomes.get(0).getHtmlReport());

        List<String> renderedSteps = scenario.getSteps().stream()
                .map(RenderCucumber::step)
                .collect(Collectors.toList());

        return new ScenarioSummaryOutcome(scenarioTitle,
                scenario.getKeyword(),
                result,
                reportBadges,
                scenarioReport.orElse(""),
                scenario.getDescription(),
                renderedSteps,
                new ArrayList<>(),
                new ArrayList<>(),
                0,
                false,
                feature.getName(),
                featureReport,
                new HashSet<>(),
                new HashMap<>(),
                null,
                startTimeOfFirstTestIn(outcomes),
                totalDurationOf(outcomes),
                Collections.emptyList(),
                firstContextIn(testOutcomes));
    }

    private ZonedDateTime startTimeOfFirstTestIn(List<TestOutcome> outcomes) {
        return outcomes.stream()
                .map(TestOutcome::getStartTime)
                .sorted()
                .findFirst()
                .orElse(null);
    }

    private Long totalDurationOf(List<TestOutcome> outcomes) {
        return outcomes.stream()
                .mapToLong(TestOutcome::getDuration)
                .sum();
    }

    private ScenarioOutcome scenarioOutcomeFrom(Feature feature,
                                                Scenario scenario,
                                                TestOutcomes testOutcomes,
                                                net.thucydides.core.model.Rule rule) {

        List<TestOutcome> outcomes = testOutcomes.testOutcomesWithName(scenario.getName());

        String scenarioTitle = scenario.getName();

        TestResult result = (outcomes.isEmpty()) ? TestResult.UNDEFINED :
                TestResultList.overallResultFrom(outcomes.stream().map(TestOutcome::getResult).collect(Collectors.toList()));

        List<String> reportBadges = ReportBadges.from(outcomes, scenario.getName());

        String featureReport = new ReportNameProvider().forRequirement(feature.getName(), "feature");
        Optional<String> scenarioReport = (outcomes.isEmpty()) ? Optional.empty() : Optional.of(outcomes.get(0).getHtmlReport());

        List<String> renderedSteps = scenario.getSteps().stream()
                .map(RenderCucumber::step)
                .collect(Collectors.toList());

        List<Examples> filteredExamples = new ArrayList<>();
        Map<String, Collection<TestTag>> exampleTags = new HashMap<>();

        if (scenarioContainsExamples(scenario)) {
            List<Examples> examples = scenario.getExamples();
            examples.stream()
                    .filter(example -> example.getTags().isEmpty() || tagScanner.shouldRunForTags(fromGherkinTags(example.getTags())))
                    .forEach(filteredExamples::add);
            Set<TestTag> scenarioOutlineTags = scenarioOutlineTagsIn(scenario);
            examples.forEach(
                    example -> {
                        Collection<TestTag> testTags = CucumberTagConverter.toSerenityTags(example.getTags());
                        testTags.addAll(scenarioOutlineTags);
                        exampleTags.put(example.getName() + ":" + example.getLocation(), testTags);
                    });
        }

        List<String> renderedExamples = (scenarioContainsExamples(scenario)) ?
                RenderCucumber.examples(filteredExamples, feature.getName()) : new ArrayList<>();

        List<ExampleOutcome> exampleOutcomes = new ArrayList<>();
        for (TestOutcome outcome : outcomes) {
            exampleOutcomes.addAll(ExampleOutcomes.from(outcome));
        }

        int exampleCount = (scenarioContainsExamples(scenario)) ?
                filteredExamples.stream().mapToInt(examples -> examples.getTableBody().size()).sum()
                : 0;

        Boolean isManual = (outcomes.size() == 1) ? outcomes.get(0).isManual() : hasManualTag(feature.getTags());

        Set<TestTag> outcomeTags = outcomes.stream()
                .flatMap(outcome -> outcome.getTags().stream())
                .collect(Collectors.toSet());

        Set<TestTag> scenarioTags = scenarioTagsDefinedIn(scenario);

        outcomeTags.addAll(scenarioTags);

        return new ScenarioSummaryOutcome(scenarioTitle,
                scenario.getKeyword(),
                result,
                reportBadges,
                scenarioReport.orElse(""),
                scenario.getDescription(),
                renderedSteps,
                renderedExamples,
                exampleOutcomes,
                exampleCount,
                isManual,
                feature.getName(),
                featureReport,
                outcomeTags,
                exampleTags,
                rule,
                startTimeOfFirstTestIn(outcomes),
                totalDurationOf(outcomes),
                scenarioTags,
                firstContextIn(testOutcomes));
    }

    private Set<TestTag> scenarioTagsDefinedIn(Scenario scenario) {
        if (scenarioContainsExamples(scenario)) {
            return scenarioOutlineTagsIncludingExamplesIn(scenario);
        } else {
            return scenarioTagsIn(scenario);
        }
    }

    private String firstContextIn(TestOutcomes testOutcomes) {
        return testOutcomes.getOutcomes().stream()
                .filter(testOutcome -> StringUtils.isNotEmpty(testOutcome.getContext()))
                .map(testOutcome -> testOutcome.getContext())
                .findFirst()
                .orElse("");
    }

    private boolean scenarioContainsExamples(Scenario scenario) {
        return (!scenario.getExamples().isEmpty());
    }


    private Set<TestTag> scenarioOutlineTagsIn(Scenario scenarioOutline) {
        return scenarioOutline.getTags().stream()
                .map(tag -> TestTag.withValue(tag.getName()))
                .collect(Collectors.toSet());
    }

    private Set<TestTag> scenarioOutlineTagsIncludingExamplesIn(Scenario scenarioOutline) {
        Set<TestTag> testTags = scenarioOutlineTagsIn(scenarioOutline);

        Set<TestTag> exampleTags = scenarioOutline.getExamples().stream()
                .flatMap(examples -> examples.getTags().stream())
                .map(tag -> TestTag.withValue(tag.getName()))
                .collect(Collectors.toSet());

        testTags.addAll(exampleTags);
        return testTags;
    }

    private Set<TestTag> scenarioTagsIn(Scenario scenario) {
        return scenario.getTags().stream()
                .map(tag -> TestTag.withValue(tag.getName()))
                .collect(Collectors.toSet());
    }

    private Boolean hasManualTag(List<Tag> tags) {
        return tags.stream().anyMatch(tag -> tag.getName().toLowerCase().startsWith("@manual"));
    }

    private File pathFromResourceOnClasspath(String path) {
        String pathWithFeaturesDirectory = (path.startsWith(featuresDirectory())) ? path : featuresDirectory() + "/" + path;
        URL featureFileURL = getClass().getClassLoader().getResource(pathWithFeaturesDirectory);
        String featureFilePath;

        if (featureFileURL != null) {
            featureFilePath = featureFileURL.getFile();
        } else {
            featureFilePath = new File("src/test/resources/" + featuresDirectory() + "/" + path).getAbsolutePath();
        }
        return (featureFilePath == null) ? null : new File(featureFilePath);
    }

    private String featuresDirectory() {
        return ThucydidesSystemProperty.SERENITY_FEATURES_DIRECTORY.from(environmentVariables, "features");
    }
}
