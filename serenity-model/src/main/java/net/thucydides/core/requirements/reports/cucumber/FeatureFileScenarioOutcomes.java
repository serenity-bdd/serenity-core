package net.thucydides.core.requirements.reports.cucumber;

import gherkin.ast.Examples;
import gherkin.ast.Feature;
import gherkin.ast.ScenarioDefinition;
import gherkin.ast.ScenarioOutline;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.*;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.ReportNameProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.model.cucumber.CucumberParser;
import net.thucydides.core.requirements.model.cucumber.ScenarioReport;
import net.thucydides.core.requirements.reports.*;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;
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
            List<ScenarioOutcome> scenarioOutcomes = new ArrayList<>();
            feature.get().getChildren().forEach(
                    scenarioDefinition -> scenarioOutcomes.add(
                                                scenarioOutcomeFrom(feature.get(),
                                                                     scenarioDefinition,
                                                                     requirementsOutcomes.getTestOutcomes()))
            );
            return scenarioOutcomes;
        }
    }

    private final String DETAILS_BADGE = "<a href='%s' class='badge more-details'>%s</a>";
    private final String DETAILS_WITH_CONTEXT_BADGE = "<a href='%s'class='badge more-details' style='background-color:%s;'>%s %s</a>";

    private ScenarioOutcome scenarioOutcomeFrom(Feature feature,
                                                ScenarioDefinition scenarioDefinition,
                                                TestOutcomes testOutcomes) {

        List<TestOutcome> outcomes = testOutcomes.testOutcomesWithName(scenarioDefinition.getName());

        String scenarioTitle = scenarioDefinition.getName();

        TestResult result = (outcomes.isEmpty()) ? TestResult.UNDEFINED :
                        TestResultList.overallResultFrom(outcomes.stream().map(TestOutcome::getResult).collect(Collectors.toList()));

        List<String> reportBadges = reportBadgesFrom(outcomes, scenarioDefinition.getName());

        String featureReport = new ReportNameProvider().forRequirement(feature.getName(),"feature");

        List<String> renderedSteps = scenarioDefinition.getSteps().stream()
                    .map(RenderCucumber::step)
                    .collect(Collectors.toList());

        List<String> renderedExamples = (scenarioDefinition instanceof ScenarioOutline) ?
                RenderCucumber.examples(((ScenarioOutline) scenarioDefinition).getExamples(),
                                        feature.getName(),
                                        scenarioDefinition.getName()) : Collections.EMPTY_LIST;

        int exampleCount = (scenarioDefinition instanceof ScenarioOutline) ?
                ((ScenarioOutline) scenarioDefinition).getExamples().stream().mapToInt(examples -> examples.getTableBody().size()).sum()
                : 0;


        return new ScenarioSummaryOutcome(scenarioTitle,
                                         scenarioDefinition.getKeyword(),
                                         result,
                                         reportBadges,
                                         scenarioDefinition.getDescription(),
                                         renderedSteps,
                                         renderedExamples,
                                         exampleCount,
                                         feature.getName(),
                                         featureReport);


    }

    private List<String> reportBadgesFrom(List<TestOutcome> outcomes, String scenarioName) {


        if (outcomes.size() == 1) {
            return Collections.singletonList(String.format(DETAILS_BADGE, outcomes.get(0).getHtmlReport(), "Details"));
        }

        return outcomes.stream()
                    .filter( outcome -> outcome.getName().equalsIgnoreCase(scenarioName))
                    .map(this::outcomeBadgeFor)
                    .collect(Collectors.toList());
    }

    private String outcomeBadgeFor(TestOutcome outcome) {
        String contextIcon = ContextIcon.forOutcome(outcome);

        return String.format(DETAILS_WITH_CONTEXT_BADGE,
                outcome.getHtmlReport(),
                BadgeBackground.forOutcome(outcome),
                contextIcon,
                "Details");
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
