package io.cucumber.gherkin;

import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.feature.Options;
import io.cucumber.core.runtime.FeaturePathFeatureSupplier;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Scenario;
import net.serenitybdd.cucumber.gherkin.IncrementingIdGenerator;
import net.serenitybdd.cucumber.suiteslicing.TestScenarioResult;
import net.serenitybdd.cucumber.suiteslicing.TestStatistics;
import net.thucydides.model.util.Inflector;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class ScenarioLineCountStatistics implements TestStatistics {

    private final Supplier<ClassLoader> classLoader = CucumberScenarioLoader.class::getClassLoader;
    private final List<TestScenarioResult> results;

    private ScenarioLineCountStatistics(List<URI> featurePaths) {
        FeatureParser parser = new FeatureParser(UUID::randomUUID);
        Options featureOptions = () -> featurePaths;

        FeaturePathFeatureSupplier supplier =
            new FeaturePathFeatureSupplier(classLoader, featureOptions, parser);
        List<Feature> features = new ArrayList<>();
        List<io.cucumber.core.gherkin.Feature> gherkinFeatures = supplier.get();
        for(io.cucumber.core.gherkin.Feature gherkinFeature  : gherkinFeatures) {
            Parser gherkinParser = new Parser(new GherkinDocumentBuilder(new IncrementingIdGenerator(),gherkinFeature.getUri().toString()));
            GherkinDocument gherkinDocument = (GherkinDocument)gherkinParser.parse(gherkinFeature.getSource(),gherkinFeature.getUri().toString());
            features.add(gherkinDocument.getFeature().get());
        }
        this.results = features.stream()
            .map(featureToScenarios())
            .flatMap(List::stream)
            .collect(toList());
    }

    public static ScenarioLineCountStatistics fromFeaturePath(URI featurePaths) {
        return fromFeaturePaths(asList(featurePaths));
    }

    public static ScenarioLineCountStatistics fromFeaturePaths(List<URI> featurePaths) {
        return new ScenarioLineCountStatistics(featurePaths);
    }

    private Function<Feature, List<TestScenarioResult>> featureToScenarios() {
        return cucumberFeature -> {
            try {
                return (cucumberFeature == null) ? Collections.emptyList() : cucumberFeature.getChildren()
                    .stream()
                        .filter(child -> child.getScenario() != null && child.getScenario().isPresent())
                        .map(FeatureChild::getScenario)
                        .map(Optional::get)
                        .map(scenarioToResult(cucumberFeature))
                        .collect(toList());
            } catch (Exception e) {
                throw new IllegalStateException(String.format("Could not extract scenarios from %s", cucumberFeature.getName()), e);
            }
        };
    }

    private Function<Scenario, TestScenarioResult> scenarioToResult(Feature feature) {
        return scenarioDefinition -> {
            try {
                return new TestScenarioResult(
                    feature.getName(),
                    scenarioDefinition.getName(),
                    scenarioStepCountFor(backgroundStepCountFor(feature), scenarioDefinition));
            } catch (Exception e) {
                throw new IllegalStateException(String.format("Could not determine step count for scenario '%s'", scenarioDefinition.getDescription()), e);
            }
        };
    }

    private BigDecimal scenarioStepCountFor(int backgroundStepCount, Scenario scenarioDefinition) {
        final int stepCount;
        if (scenarioDefinition.getExamples().size() > 0) {
            Integer exampleCount = scenarioDefinition.getExamples().stream()
                    .map(examples -> examples.getTableBody().size())
                    .mapToInt(Integer::intValue).sum();
            stepCount = exampleCount * (backgroundStepCount + scenarioDefinition.getSteps().size());
        } else {
            stepCount = backgroundStepCount + scenarioDefinition.getSteps().size();
        }
        return BigDecimal.valueOf(stepCount);
    }

    private int backgroundStepCountFor(Feature feature) {
        FeatureChild scenarioDefinition = feature.getChildren().get(0);
        if (scenarioDefinition.getBackground() != null && scenarioDefinition.getBackground().isPresent()) {
            return scenarioDefinition.getBackground().get().getSteps().size();
        } else {
            return 0;
        }
    }

    @Override
    public BigDecimal scenarioWeightFor(String feature, String scenario) {
        return results.stream()
            .filter(record -> record.feature.equals(feature) && record.scenario.equals(scenario))
            .map(TestScenarioResult::duration)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("no result found for scenario '%s' in feature '%s'", scenario, feature)));
    }

    @Override
    public List<TestScenarioResult> records() {
        return results;
    }

    public String toString() {
        return Inflector.getInstance().kebabCase(this.getClass().getSimpleName());
    }
}
