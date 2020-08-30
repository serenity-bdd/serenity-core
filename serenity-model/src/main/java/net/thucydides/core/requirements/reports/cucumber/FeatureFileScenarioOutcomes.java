package net.thucydides.core.requirements.reports.cucumber;


import io.cucumber.messages.Messages.GherkinDocument;
import io.cucumber.messages.Messages.GherkinDocument.Feature;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario.Examples;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.CucumberTagConverter;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.cucumber.AnnotatedFeature;
import net.thucydides.core.requirements.model.cucumber.CucumberParser;
import net.thucydides.core.requirements.reports.ReportBadges;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.reports.ScenarioOutcome;
import net.thucydides.core.requirements.reports.ScenarioSummaryOutcome;
import net.thucydides.core.tags.TagScanner;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static net.thucydides.core.reports.html.CucumberTagConverter.fromGherkinTags;


public class FeatureFileScenarioOutcomes {


    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureFileScenarioOutcomes.class);

    private Requirement requirement;
    private EnvironmentVariables environmentVariables;
    private TagScanner tagScanner;

    public FeatureFileScenarioOutcomes(Requirement requirement, EnvironmentVariables environmentVariables) {
        this.requirement = requirement;
        this.environmentVariables = environmentVariables;
        this.tagScanner = new TagScanner(environmentVariables);
    }

    public FeatureFileScenarioOutcomes(Requirement requirement) {
        this(requirement, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public static FeatureFileScenarioOutcomes from(Requirement requirement) {
        return new FeatureFileScenarioOutcomes(requirement);
    }

    public List<ScenarioOutcome> forOutcomesIn(RequirementsOutcomes requirementsOutcomes) {
        CucumberParser parser = new CucumberParser();
        Optional<AnnotatedFeature> feature = parser.loadFeature(pathFromResourceOnClasspath(requirement.getPath()));

        if (!feature.isPresent()) {
            return Collections.emptyList();
        } else {
            List<ScenarioOutcome> scenarioOutcomes = new ArrayList<>();
            feature.get().getFeature().getChildrenList().forEach(
                    featureChildren -> scenarioOutcomes.add(
                            scenarioOutcomeFrom(feature.get().getFeature(),
                                    featureChildren.getScenario(),
                                    requirementsOutcomes.getTestOutcomes()))
            );
            return scenarioOutcomes;
        }
    }

    private ScenarioOutcome scenarioOutcomeFrom(Feature feature,
                                                Scenario scenario,
                                                TestOutcomes testOutcomes) {

        List<TestOutcome> outcomes = testOutcomes.testOutcomesWithName(scenario.getName());

        String scenarioTitle = scenario.getName();

        TestResult result = (outcomes.isEmpty()) ? TestResult.UNDEFINED :
                TestResultList.overallResultFrom(outcomes.stream().map(TestOutcome::getResult).collect(Collectors.toList()));

        List<String> reportBadges = ReportBadges.from(outcomes, scenario.getName());

        String featureReport = new ReportNameProvider().forRequirement(feature.getName(),"feature");
        Optional<String> scenarioReport = (outcomes.isEmpty()) ? Optional.empty() : Optional.of(outcomes.get(0).getHtmlReport());

        List<String> renderedSteps = scenario.getStepsList().stream()
                .map(RenderCucumber::step)
                .collect(Collectors.toList());

        List<GherkinDocument.Feature.Scenario.Examples> filteredExamples = new ArrayList<Examples>();
        Map<String, Collection<TestTag>> exampleTags = new HashMap<>();

        //if((scenarioDefinition instanceof ScenarioOutline)) {
        if(scenarioContainsExamples(scenario)) {
            List<Examples> examples =  scenario.getExamplesList();
            examples.stream()
                    .filter(example -> example.getTagsList().isEmpty() || tagScanner.shouldRunForTags(fromGherkinTags(example.getTagsList())))
                    .forEach(filteredExamples::add);
            Set<TestTag> scenarioOutlineTags = scenarioOutlineTagsIn(scenario);
            examples.stream().forEach(
                    example -> {
                        Collection<TestTag> testTags = CucumberTagConverter.toSerenityTags(example.getTagsList());
                        testTags.addAll(scenarioOutlineTags);
                        exampleTags.put(example.getName() + ":" + example.getLocation(), testTags);
                    });
        }

        List<Examples> examplesList = filteredExamples;
        List<String> renderedExamples = (scenarioContainsExamples(scenario)) ?
                RenderCucumber.examples(examplesList,
                        feature.getName(),
                        scenario.getName()) : Collections.EMPTY_LIST;

        int exampleCount = (scenarioContainsExamples(scenario)) ?
                examplesList.stream().mapToInt(examples -> examples.getTableBodyList().size()).sum()
                : 0;

        Boolean isManual = (outcomes.size() == 1) ? outcomes.get(0).isManual() : hasManualTag(feature.getTagsList());

        Set<TestTag> scenarioTags = outcomes.stream()
                                        .flatMap(outcome -> outcome.getTags().stream())
                                        .collect(Collectors.toSet());

        scenarioTags.addAll(scenarioTagsDefinedIn(scenario));

        return new ScenarioSummaryOutcome(scenarioTitle,
                scenario.getKeyword(),
                result,
                reportBadges,
                scenarioReport.orElse(""),
                scenario.getDescription(),
                renderedSteps,
                renderedExamples,
                exampleCount,
                isManual,
                feature.getName(),
                featureReport,
                scenarioTags,
                exampleTags);
    }

    private Set<TestTag> scenarioTagsDefinedIn(Scenario scenario) {
        if (scenarioContainsExamples(scenario)) {
            return scenarioOutlineTagsIncludingExamplesIn(scenario);
        } else  {
            return scenarioTagsIn(scenario);
        }
    }

    private boolean scenarioContainsExamples(Scenario scenario) {
        return(scenario.getExamplesCount() > 0);
    }

    private Set<TestTag> scenarioOutlineTagsIn(Scenario scenarioOutline) {
        Set<TestTag> testTags = scenarioOutline.getTagsList().stream()
                .map(tag -> TestTag.withValue(tag.getName()))
                .collect(Collectors.toSet());
        return testTags;
    }

    private Set<TestTag> scenarioOutlineTagsIncludingExamplesIn(Scenario scenarioOutline) {
        Set<TestTag> testTags = scenarioOutlineTagsIn(scenarioOutline);

        Set<TestTag> exampleTags = scenarioOutline.getExamplesList().stream()
                .flatMap(examples -> examples.getTagsList().stream())
                .map(tag -> TestTag.withValue(tag.getName()))
                .collect(Collectors.toSet());

        testTags.addAll(exampleTags);
        return testTags;
    }

    private Set<TestTag> scenarioTagsIn(Scenario scenario) {
        return scenario.getTagsList().stream()
                .map(tag -> TestTag.withValue(tag.getName()))
                .collect(Collectors.toSet());
    }

    private Boolean hasManualTag(List<Feature.Tag> tags) {
        return tags.stream().anyMatch(tag -> tag.getName().toLowerCase().startsWith("@manual"));
    }

    private File pathFromResourceOnClasspath(String path) {
        URL featureFileURL = getClass().getClassLoader().getResource(featuresDirectory() + "/" + path);
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
