package net.thucydides.core.requirements.reports.cucumber;

import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.cucumber.CucumberParser;
import net.thucydides.core.requirements.model.cucumber.ScenarioReport;
import net.thucydides.core.requirements.reports.RequirementsOutcomes;
import net.thucydides.core.requirements.reports.ScenarioOutcome;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FeatureFileScenarioOutcomes {

    private Requirement requirement;
    private EnvironmentVariables environmentVariables;

    public FeatureFileScenarioOutcomes(Requirement requirement, EnvironmentVariables environmentVariables) {
        this.requirement = requirement;
        this.environmentVariables = environmentVariables;
    }

    public FeatureFileScenarioOutcomes(Requirement requirement) {
        this(requirement, Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public static FeatureFileScenarioOutcomes from(Requirement requirement) {//}, RequirementsOutcomes requirementsOutcomes) {
        return new FeatureFileScenarioOutcomes(requirement);
    }

    public List<ScenarioOutcome> forOutcomesIn(RequirementsOutcomes requirementsOutcomes) {
        CucumberParser parser = new CucumberParser();
        Optional<Feature> feature = parser.loadFeature(pathFromResourceOnClasspath(requirement.getPath()));

        if (!feature.isPresent()) {
            return Collections.emptyList();
        } else {
            return feature.get().getChildren().stream()
                    .map(scenarioDefinition -> scenarioOutcomeFrom(feature.get(), scenarioDefinition, requirementsOutcomes.getTestOutcomes()))
                    .collect(Collectors.toList());
        }
    }

    private ScenarioOutcome scenarioOutcomeFrom(Feature feature, ScenarioDefinition scenarioDefinition, TestOutcomes testOutcomes) {
        Optional<? extends TestOutcome> outcome = testOutcomes.testOutcomeWithName(scenarioDefinition.getName());
        TestResult testResult = (outcome.isPresent() ? outcome.get().getResult() : TestResult.UNDEFINED);
        ZonedDateTime startTime = outcome.map(TestOutcome::getStartTime).orElse(null);
        Long duration = (outcome.isPresent()) ? outcome.get().getDuration() : 0;
        boolean isManual = outcome.map(TestOutcome::isManual).orElse(false);
        String scenarioReport = ScenarioReport.forScenario(scenarioDefinition.getName()).inFeature(feature);
        String featureReport = new ReportNameProvider().forRequirement(feature.getName(),"feature");

        List<String> renderedSteps = scenarioDefinition.getSteps().stream()
                                                       .map(RenderCucumber::step)
                                                       .collect(Collectors.toList());

        List<String> renderedExamples = (scenarioDefinition instanceof ScenarioOutline) ?
                RenderCucumber.examples(((ScenarioOutline) scenarioDefinition).getExamples()) : Collections.EMPTY_LIST;

        int exampleCount = (scenarioDefinition instanceof ScenarioOutline) ?
                ((ScenarioOutline) scenarioDefinition).getExamples().stream().mapToInt(examples -> examples.getTableBody().size()).sum()
                : 0;
        return new ScenarioOutcome(scenarioDefinition.getName(),
                                    scenarioDefinition.getKeyword(),
                                    testResult,
                                    scenarioReport,
                                    startTime,
                                    duration,
                                    isManual,
                                    scenarioDefinition.getDescription(),
                                    renderedSteps,
                                    renderedExamples,
                                    exampleCount,
                                    feature.getName(),
                                    featureReport
        );

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
