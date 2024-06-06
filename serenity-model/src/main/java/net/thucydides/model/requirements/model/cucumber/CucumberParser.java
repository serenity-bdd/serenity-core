package net.thucydides.model.requirements.model.cucumber;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import io.cucumber.core.gherkin.FeatureParserException;
import io.cucumber.core.gherkin.messages.GherkinMessagesFeatureParser;
import io.cucumber.core.resource.Resource;
import io.cucumber.messages.types.*;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.reports.html.CucumberTagConverter;
import net.thucydides.model.requirements.model.FeatureBackgroundNarrative;
import net.thucydides.model.requirements.model.RequirementDefinition;
import net.thucydides.model.util.EnvironmentVariables;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static net.thucydides.model.requirements.model.cucumber.FeatureFileAnaysisErrors.*;


/**
 * Created by john on 5/03/15.
 */
public class CucumberParser {

    private final String locale;
    private final String encoding;
    private final static Supplier<UUID> ID_SUPPLIER = UUID::randomUUID;

    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberParser.class);

    public CucumberParser() {
        this(ConfiguredEnvironment.getEnvironmentVariables());
    }


    public CucumberParser(EnvironmentVariables environmentVariables) {
        this(ThucydidesSystemProperty.FEATURE_FILE_LANGUAGE.from(environmentVariables, "en"), environmentVariables);
    }

    public CucumberParser(String locale, EnvironmentVariables environmentVariables) {
        this.locale = locale;
        this.encoding = ThucydidesSystemProperty.FEATURE_FILE_ENCODING.from(environmentVariables, Charset.defaultCharset().name());
    }

    public Optional<AnnotatedFeature> loadFeature(File featureFile) {
        LOGGER.debug("Loading feature {}", featureFile.toString());
        if (featureFile == null || !featureFile.exists()) {
            return Optional.empty();
        }

        Optional<GherkinDocument> possibleGherkinDocument = loadCucumberFeature(featureFile.toPath());
        try {
            if (possibleGherkinDocument.isPresent() && possibleGherkinDocument.get().getFeature().isPresent()) {
                GherkinDocument gherkinDocument = possibleGherkinDocument.get();

                String descriptionInComments = NarrativeFromCucumberComments.in(gherkinDocument.getComments());

                if (featureFileCouldNotBeReadFor(gherkinDocument.getFeature())) {
                    return Optional.empty();
                }
                Feature feature = gherkinDocument.getFeature().get();
                List<Scenario> scenarioList = feature.getChildren().stream()
                        .map(FeatureChild::getScenario)
                        .filter(Objects::nonNull)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());

                feature.getChildren().stream()
                        .map(FeatureChild::getRule)
                        .filter(Objects::nonNull)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .flatMap(rule -> rule.getChildren().stream().map(RuleChild::getScenario))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(scenarioList::add);

                performGherkinQualityChecks(featureFile, feature, scenarioList);

                return Optional.of(new AnnotatedFeature(feature, scenarioList, descriptionInComments));
            }
            return Optional.empty();
        } catch (InvalidFeatureFileException invalidFeatureFile) {
            throw invalidFeatureFile;
        } catch (Throwable ex) {
            LOGGER.error("Invalid feature ", ex);
            return Optional.empty();
        }
    }

    private void performGherkinQualityChecks(File featureFile, Feature feature, List<Scenario> scenarioList) {
        Map<String, List<Scenario>> allScenariosByName = scenarioList.stream().collect(Collectors.groupingBy(Scenario::getName));

        List<String> errors = new ArrayList<>();

        // FEATURE NAME
        if (feature.getName().isEmpty()) {
            errors.add(String.format(EMPTY_FEATURE_NAME, featureFile.getName()));
        }

        // DUPLICATE SCENARIO NAMES
        allScenariosByName.forEach(
                (scenarioName, scenarios)
                        -> analyseDuplicateScenarioNames(scenarioName, scenarios, featureFile)
                        .ifPresent(errors::add)
        );

        // EMPTY SCENARIO NAMES
        allScenariosByName.forEach((scenarioName, scenarios)
                -> analyseEmptyScenarioNames(scenarioName, scenarios, featureFile)
                .ifPresent(errors::add)
        );

        // EMPTY RULE NAMES
        analyseEmptyRuleNames(feature, featureFile).ifPresent(errors::add);

        if (!errors.isEmpty()) {
            throw new InvalidFeatureFileException(errors.stream().collect(Collectors.joining(System.lineSeparator())));
        }
    }

    private Optional<String> analyseEmptyRuleNames(Feature feature, File featureFile) {
        List<Rule> ruleList = feature.getChildren().stream()
                .map(FeatureChild::getRule)
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (ruleList.stream().anyMatch(rule -> rule.getName().isEmpty())) {
            return Optional.of(String.format(EMPTY_RULE_NAME, featureFile.getName()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> analyseDuplicateScenarioNames(String scenarioName, List<Scenario> scenarios, File featureFile) throws InvalidFeatureFileException {
        if (scenarios.size() > 1) {
            return Optional.of(String.format(DUPLICATE_SCENARIO_NAME, scenarioName, featureFile.getName()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> analyseEmptyScenarioNames(String scenarioName, List<Scenario> scenarios, File featureFile) throws InvalidFeatureFileException {
        if (scenarioName.isEmpty()) {
            return Optional.of(String.format(EMPTY_SCENARIO_NAME, featureFile.getName()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<GherkinDocument> loadCucumberFeature(Path cucumberFile) {
        Optional<io.cucumber.core.gherkin.Feature> possibleFeature = getFeature(cucumberFile);

        List<Envelope> envelopes = new ArrayList<>();

        possibleFeature.ifPresent(
                feature -> feature.getParseEvents().forEach(
                        parseEvent -> envelopes.add((Envelope) parseEvent)
                )
        );

        return envelopes.stream()
                .map(Envelope::getGherkinDocument)
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(gherkinDocument -> gherkinDocument.getFeature() != null && gherkinDocument.getFeature().isPresent())
                .findFirst();
    }

    private Optional<io.cucumber.core.gherkin.Feature> getFeature(Path cucumberFilePath) {
        Resource cucumberResource = new URIResource(cucumberFilePath);
        try {
            return parseResource(cucumberResource);
        } catch (Throwable throwable) {
            throw new InvalidFeatureFileException("Failed to read feature file at " + cucumberFilePath, throwable);
        }
    }

    public Optional<io.cucumber.core.gherkin.Feature> parseResource(Resource resource) {
        requireNonNull(resource);
        URI uri = resource.getUri();
        io.cucumber.core.gherkin.FeatureParser featureParser = new GherkinMessagesFeatureParser();
        try (InputStream source = resource.getInputStream()) {
            return featureParser.parse(uri, source, ID_SUPPLIER);
        } catch (IOException e) {
            throw new FeatureParserException("Failed to parse resource at: " + uri, e);
        }
    }

    private void reportAnyCucumberSyntaxErrorsIn(Throwable gherkinError) {
        if (gherkinError instanceof FeatureParserException) {
            throw new InvalidFeatureFileException(gherkinError.getMessage(), gherkinError);
        }
    }

    public Optional<RequirementDefinition> loadFeatureDefinition(File featureFile) {

        Optional<AnnotatedFeature> loadedFeature = loadFeature(featureFile);

        if (!loadedFeature.isPresent()) {
            return Optional.empty();
        } else {
            return getRequirementsDefinitionFrom(loadedFeature);
        }
    }

    @NotNull
    private synchronized Optional<RequirementDefinition> getRequirementsDefinitionFrom(Optional<AnnotatedFeature> loadedFeature) {
        Feature feature = loadedFeature.get().getFeature();

        String cardNumber = findCardNumberInTags(tagsDefinedIn(feature));
        List<String> versionNumbers = findVersionNumberInTags(tagsDefinedIn(feature));
        String title = feature.getName();
        String text = descriptionWithScenarioReferencesFrom(feature);

        String id = getIdFromName(title);

        Set<TestTag> requirementTags = feature.getTags().stream().map(tag -> TestTag.withValue(tag.getName())).collect(Collectors.toSet());
        requirementTags.add(TestTag.withName(title).andType("feature"));

        // Scenario Tags
        Map<String, Collection<TestTag>> scenarioTags = new HashMap<>();

        List<Scenario> scenarios = scenariosIn(feature);
        scenarios.forEach(scenarioDefinition ->
        {
            if (!scenarioDefinition.getExamples().isEmpty()) {
                List<Tag> scenarioOutlineTags = scenarioDefinition.getTags();
                scenarioTags.put(scenarioDefinition.getName(), CucumberTagConverter.toSerenityTags(scenarioOutlineTags));
                List<Examples> examples = scenarioDefinition.getExamples();
                for (Examples currentExample : examples) {
                    List<Tag> allExampleTags = new ArrayList<>();
                    allExampleTags.addAll(scenarioOutlineTags);
                    allExampleTags.addAll(currentExample.getTags());
                    scenarioTags.put(scenarioDefinition.getName() + "_examples_at_line:" + currentExample.getLocation().getLine(),
                            CucumberTagConverter.toSerenityTags(allExampleTags));
                }
            } else {
                scenarioTags.put(scenarioDefinition.getName(), tagsFrom(scenarioDefinition));
            }
        });
        List<String> scenarioNames = scenarios.stream().map(Scenario::getName).collect(Collectors.toList());

        FeatureBackgroundNarrative background = null;

        if (backgroundChildIn(feature.getChildren()).isPresent()) {
            background = backgroundElementFrom(backgroundChildIn(feature.getChildren()).get().getBackground());
        }

        // TODO: Find all the rules in the feature children and collect the backgrounds
        Map<String, FeatureBackgroundNarrative> ruleBackgrounds = new HashMap<>();
        rulesIn(feature.getChildren())
                .forEach(
                        rule -> {
                            Optional<Background> ruleBackground = rule.getChildren()
                                    .stream()
                                    .map(RuleChild::getBackground)
                                    .filter(Objects::nonNull)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .findFirst();
                            ruleBackground.ifPresent(
                                    value -> ruleBackgrounds.put(rule.getName(), backgroundElementFrom(Optional.ofNullable(value)))
                            );
                        }
                );

        return Optional.of(new RequirementDefinition(Optional.of(title),
                Optional.of(id),
                Optional.ofNullable(cardNumber),
                versionNumbers,
                "feature",
                text != null ? text : "",
                new ArrayList<>(requirementTags),
                scenarioNames,
                scenarioTags)
                .withBackground(background)
                .withRuleBackgrounds(ruleBackgrounds));
    }

    private List<Scenario> scenariosIn(Feature feature) {
        List<Scenario> scenarios = new ArrayList<>();
        feature.getChildren().forEach(
                child -> {
                    if (child.getRule() != null && child.getRule().isPresent()) {
                        scenarios.addAll(scenariosIn(child.getRule().get()));
                    } else if (child.getScenario() != null && child.getScenario().isPresent()) {
                        scenarios.add(child.getScenario().get());
                    }
                }
        );
        return scenarios;
    }

    private List<Scenario> scenariosIn(Rule rule) {
        return rule.getChildren().stream()
                .filter(child -> child.getScenario() != null)
                .map(RuleChild::getScenario)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Stream<Rule> rulesIn(List<FeatureChild> childrenList) {
        return childrenList.stream()
                .map(FeatureChild::getRule)
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Optional<FeatureChild> backgroundChildIn(List<FeatureChild> featureChildren) {
        return featureChildren.stream()
                .filter(featureChild -> featureChild.getBackground() != null)
                .findFirst();
    }

    private FeatureBackgroundNarrative backgroundElementFrom(Optional<Background> background) {
        if (background.isPresent()) {
            return new FeatureBackgroundNarrative(background.get().getName(), background.get().getDescription());
        } else {
            return new FeatureBackgroundNarrative("", "");
        }
    }

    private Collection<TestTag> tagsFrom(Scenario scenarioDefinition) {
        if (scenarioDefinition.getExamples().size() == 0) {
            return asSerenityTags(scenarioDefinition.getTags());
        } else {
            Set<TestTag> outlineTags = new HashSet<>(asSerenityTags(scenarioDefinition.getTags()));
            scenarioDefinition.getExamples().forEach(
                    examples -> outlineTags.addAll(asSerenityTags(examples.getTags()))
            );
            return outlineTags;
        }
    }

    private Set<TestTag> asSerenityTags(List<Tag> gherkinTags) {
        return gherkinTags.stream()
                .map(tag -> TestTag.withValue(tag.getName()))
                .collect(Collectors.toSet());
    }

    private String descriptionWithScenarioReferencesFrom(Feature feature) {
        if (feature.getDescription() == null) {
            return "";
        }

        return Arrays.stream(feature.getDescription().split("\\r?\\n"))
                .map(line -> DescriptionWithScenarioReferences.from(feature).forText(line))
                .collect(Collectors.joining(lineSeparator()));
    }


    private String getIdFromName(String name) {
        return name.replaceAll("[\\s_]", "-").toLowerCase();
    }

    private boolean featureFileCouldNotBeReadFor(Optional<Feature> feature) {
        return feature == null || !feature.isPresent();
    }

    private List<Tag> tagsDefinedIn(Feature feature) {
        return feature.getTags();
    }

    private String findCardNumberInTags(List<Tag> tags) {

        for (Tag tag : tags) {
            if (tag.getName().toLowerCase().startsWith("@issue:")) {
                return tag.getName().replaceAll("@issue:", "");
            } else if (tag.getName().toLowerCase().startsWith("@issues:")) {
                String issueNumberList = tag.getName().replaceAll("@issues:", "");
                List<String> issueNumberTags = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(issueNumberList);
                return issueNumberTags.stream().findFirst().orElse("");
            }
        }
        return null;
    }

    private List<String> findVersionNumberInTags(List<Tag> tags) {
        List<String> versionNumbers = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getName().toLowerCase().startsWith("@version:")) {
                versionNumbers.add(tag.getName().replaceAll("@version:", ""));
            } else if (tag.getName().toLowerCase().startsWith("@versions:")) {
                String versionNumberList = tag.getName().replaceAll("@versions:", "");
                List<String> versionNumberTags = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(versionNumberList);
                versionNumbers.addAll(versionNumberTags);
            }
        }
        return versionNumbers;
    }
}
