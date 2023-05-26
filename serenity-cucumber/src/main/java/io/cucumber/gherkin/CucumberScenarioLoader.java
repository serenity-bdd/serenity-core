package io.cucumber.gherkin;

import com.google.common.collect.FluentIterable;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.feature.Options;
import io.cucumber.core.runtime.FeaturePathFeatureSupplier;
import io.cucumber.messages.types.*;
import net.serenitybdd.cucumber.suiteslicing.TestStatistics;
import net.serenitybdd.cucumber.suiteslicing.WeightedCucumberScenario;
import net.serenitybdd.cucumber.suiteslicing.WeightedCucumberScenarios;
import net.serenitybdd.cucumber.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final List<URI> featurePaths;
    private final TestStatistics statistics;
    private Map<Feature, URI> mapsForFeatures = new HashMap<>();

    public CucumberScenarioLoader(List<URI> featurePaths, TestStatistics statistics) {
        this.featurePaths = featurePaths;
        this.statistics = statistics;
    }

    public WeightedCucumberScenarios load() {
        LOGGER.debug("Feature paths are {}", featurePaths);
        FeatureParser parser = new FeatureParser(UUID::randomUUID);
        Options featureOptions = () -> featurePaths;

        FeaturePathFeatureSupplier supplier =
            new FeaturePathFeatureSupplier(classLoader, featureOptions, parser);

        IntStream.range(0, supplier.get().size())
            .forEach(i ->
            {
                io.cucumber.core.gherkin.Feature feature = supplier.get().get(i);
                Parser<GherkinDocument> gherkinParser = new Parser<>(new GherkinDocumentBuilder(new IncrementingIdGenerator(),feature.getUri().toString()));
                mapsForFeatures.put(gherkinParser.parse(feature.getSource(),feature.getUri().toString()).getFeature().get(),
                                    feature.getUri());
            });


        List<Feature> features = new ArrayList<>();
        List<io.cucumber.core.gherkin.Feature> gherkinFeatures = supplier.get();
        for(io.cucumber.core.gherkin.Feature gherkinFeature  : gherkinFeatures) {
            Parser gherkinParser = new Parser(new GherkinDocumentBuilder(new IncrementingIdGenerator(),gherkinFeature.getUri().toString()));
            GherkinDocument gherkinDocument = (GherkinDocument)gherkinParser.parse(gherkinFeature.getSource(),gherkinFeature.getUri().toString());
            features.add(gherkinDocument.getFeature().get());
        }

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
                        .filter(child -> child.getScenario() != null && child.getScenario().isPresent())
                        .map(FeatureChild::getScenario)
                        .map(scenarioDefinition -> new WeightedCucumberScenario(
                        PathUtils.getAsFile(mapsForFeatures.get(cucumberFeature)).getName(),
                        cucumberFeature.getName(),
                        scenarioDefinition.get().getName(),
                        scenarioWeightFor(cucumberFeature, scenarioDefinition.get()),
                        tagsFor(cucumberFeature, scenarioDefinition.get()),
                        scenarioCountFor(scenarioDefinition.get())))
                    .collect(toList());
            } catch (Throwable e) {
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

    private static Envelope readEnvelopeFromPath(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            String data = new String(bytes, StandardCharsets.UTF_8);
            return Envelope.of(new Source(path.toString(), data, SourceMediaType.TEXT_X_CUCUMBER_GHERKIN_PLAIN));
        } catch (IOException e) {
            throw new GherkinException(e.getMessage(), e);
        }
    }

}
