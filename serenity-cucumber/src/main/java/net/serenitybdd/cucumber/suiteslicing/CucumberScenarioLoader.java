package net.serenitybdd.cucumber.suiteslicing;

import com.google.common.collect.FluentIterable;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.feature.Options;
import io.cucumber.core.gherkin.messages.internal.gherkin.GherkinDocumentBuilder;
import io.cucumber.core.gherkin.messages.internal.gherkin.Parser;
import io.cucumber.core.gherkin.messages.internal.gherkin.TokenMatcher;
import io.cucumber.core.runtime.FeaturePathFeatureSupplier;
import io.cucumber.messages.IdGenerator;
import io.cucumber.messages.types.*;
import net.serenitybdd.cucumber.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Reads cucumber feature files and breaks them down into a collection of scenarios (WeightedCucumberScenarios).
 */
public class CucumberScenarioLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberScenarioLoader.class);
    private final Supplier<ClassLoader> classLoader = CucumberScenarioLoader.class::getClassLoader;
    private final FeatureParser parser = new FeatureParser(UUID::randomUUID);
    private final List<URI> featurePaths;
    private final TestStatistics statistics;
    private Map<Feature, URI> mapsForFeatures = new HashMap<>();

    public CucumberScenarioLoader(List<URI> featurePaths, TestStatistics statistics) {
        this.featurePaths = featurePaths;
        this.statistics = statistics;
    }

    public WeightedCucumberScenarios load() {
        LOGGER.debug("Feature paths are {}", featurePaths);
        Options featureOptions = () -> featurePaths;
        //Parser<GherkinDocument> gherkinParser = new Parser(new AstBuilder());
        Parser gherkinParser = new Parser(new GherkinDocumentBuilder(new IdGenerator.UUID()));
        TokenMatcher matcher = new TokenMatcher();
        FeaturePathFeatureSupplier supplier =
            new FeaturePathFeatureSupplier(classLoader, featureOptions, parser);

        IntStream.range(0, supplier.get().size())
            .forEach(i -> mapsForFeatures.put(
                    ((GherkinDocument)gherkinParser.parse(supplier.get().get(i).getSource(), matcher)).getFeature(),
                supplier.get().get(i).getUri())
            );

        List<WeightedCucumberScenario> weightedCucumberScenarios = mapsForFeatures.keySet().stream()
            .map(getScenarios()).flatMap(List::stream).collect(toList());

        return new WeightedCucumberScenarios(weightedCucumberScenarios);
    }

    private Function<Feature, List<WeightedCucumberScenario>> getScenarios() {
        return cucumberFeature -> {
            try {
                return (cucumberFeature == null) ? Collections.emptyList() : cucumberFeature.getChildren()
                    .stream()
                    //.filter(child -> asList(ScenarioOutline.class, Scenario.class).contains(child.getClass()))
                        .filter(child -> child.getScenario() != null).map(FeatureChild::getScenario)
                    .map(scenarioDefinition -> new WeightedCucumberScenario(
                        PathUtils.getAsFile(mapsForFeatures.get(cucumberFeature)).getName(),
                        cucumberFeature.getName(),
                        scenarioDefinition.getName(),
                        scenarioWeightFor(cucumberFeature, scenarioDefinition),
                        tagsFor(cucumberFeature, scenarioDefinition),
                        scenarioCountFor(scenarioDefinition)))
                    .collect(toList());
            } catch (Exception e) {
                throw new IllegalStateException(String.format("Could not extract scenarios from %s", mapsForFeatures.get(cucumberFeature)), e);
            }
        };
    }

    private int scenarioCountFor(Scenario scenarioDefinition) {
        if (scenarioDefinition.getExamples().size() > 0) {
            return (scenarioDefinition).getExamples().stream().map(examples -> examples.getTableBody().size()).mapToInt(Integer::intValue).sum();
        } else {
            return 1;
        }
    }

    private Set<String> tagsFor(Feature feature, Scenario scenarioDefinition) {
        return FluentIterable.concat(feature.getTags(), scenarioTags(scenarioDefinition)).stream().map(Tag::getName).collect(toSet());
    }

    private List<Tag> scenarioTags(Scenario scenario) {
        if (scenario.getExamples().isEmpty()) {
            return scenario.getTags();
        } else {
            return Stream.of(scenario.getTags(), scenario.getExamples()
                            .stream().flatMap(e -> e.getTags().stream()).collect(toList())).flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
    }

    private BigDecimal scenarioWeightFor(Feature feature, Scenario scenarioDefinition) {
        return statistics.scenarioWeightFor(feature.getName(), scenarioDefinition.getName());
    }

}
