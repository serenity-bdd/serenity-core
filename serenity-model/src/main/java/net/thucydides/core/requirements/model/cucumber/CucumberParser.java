package net.thucydides.core.requirements.model.cucumber;

import com.google.common.base.Splitter;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.gherkin.FeatureParserException;
import io.cucumber.core.gherkin.messages.internal.gherkin.Gherkin;
import io.cucumber.core.resource.Resource;
import io.cucumber.messages.IdGenerator;
import io.cucumber.messages.Messages.Envelope;
import io.cucumber.messages.Messages.GherkinDocument;
import io.cucumber.messages.Messages.GherkinDocument.Feature;
import io.cucumber.messages.Messages.GherkinDocument.Feature.FeatureChild;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario.Examples;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Tag;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.html.CucumberTagConverter;
import net.thucydides.core.requirements.model.Narrative;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.System.lineSeparator;


/**
 * Created by john on 5/03/15.
 */
public class CucumberParser {

    private final String locale;
    private final String encoding;

    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberParser.class);

    private final static String CUCUMBER_4_FEATURE_LOADER = "cucumber.runtime.model.FeatureLoader";
    private final static String CUCUMBER_2_FEATURE_LOADER = "cucumber.runtime.model.CucumberFeature";


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

    public Optional<AnnotatedFeature> loadFeature(File narrativeFile) {
        if (narrativeFile == null) {
            return Optional.empty();
        }
        if (!narrativeFile.exists()) {
            return Optional.empty();
        }

        List<String> listOfFiles = new ArrayList<>();
        listOfFiles.add(narrativeFile.getAbsolutePath());

        List<GherkinDocument> gherkinDocuments = loadCucumberFeatures(listOfFiles);
        try {
            if (gherkinDocuments.size() == 0) {
                return Optional.empty();
            }
            GherkinDocument gherkinDocument = gherkinDocuments.get(0);

            String descriptionInComments = NarrativeFromCucumberComments.in(gherkinDocument.getCommentsList());

            if (featureFileCouldNotBeReadFor(gherkinDocument.getFeature())) {
                return Optional.empty();
            }
            List<Scenario> scenarioList = gherkinDocument.getFeature().getChildrenList().stream().filter(Feature.FeatureChild::hasScenario).map(Feature.FeatureChild::getScenario).collect(Collectors.toList());
            return Optional.of(new AnnotatedFeature(gherkinDocument.getFeature(),
                    scenarioList,
                    descriptionInComments));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    private List<GherkinDocument> loadCucumberFeatures(List<String> listOfFiles) {
        for (String cucumberFile : listOfFiles) {
            searchForCucumberSyntaxErrorsIn(cucumberFile);
        }
        List<GherkinDocument> loadedFeatures = new ArrayList<>();
        List<?> envelopes = getFeatures(listOfFiles)
                .stream()
                .flatMap(feature -> StreamSupport.stream(feature.getParseEvents().spliterator(), false))
                .collect(Collectors.toList());

        List<GherkinDocument> gherkinDocuments = envelopes
                .stream()
                .filter(o -> ((Envelope) o).hasGherkinDocument())
                .map(o -> ((Envelope) o).getGherkinDocument())
                .collect(Collectors.toList());

        for (GherkinDocument gherkinDocument : gherkinDocuments) {
            if (gherkinDocument.hasFeature()) {
                loadedFeatures.add(gherkinDocument);
                LOGGER.debug("Added feature {}", gherkinDocument.getFeature().getName());
            } else {
                LOGGER.warn("Couldn't read the feature file {} - it will be ignored", gherkinDocument.getUri());
            }
        }
        return loadedFeatures;
    }

    private void searchForCucumberSyntaxErrorsIn(String cucumberFile) {
        FeatureParser featureParser = new FeatureParser(UUID::randomUUID);
        Path cucumberFilePath = new File(cucumberFile).toPath();

        Resource cucumberResource = new URIResource(cucumberFilePath);
        try {
            featureParser.parseResource(cucumberResource);
        } catch (Throwable throwable) {
            reportAnyCucumberSyntaxErrorsIn(throwable);
        }
    }

    private List<io.cucumber.core.gherkin.Feature> getFeatures(List<String> paths) {
        FeatureParser featureParser = new FeatureParser(UUID::randomUUID);
        List<io.cucumber.core.gherkin.Feature> results = new ArrayList<>();
        paths.forEach(path -> {
            Path cucumberFilePath = new File(path).toPath();
            Resource cucumberResource = new URIResource(cucumberFilePath);
            Optional<io.cucumber.core.gherkin.Feature> maybeFeature = featureParser.parseResource(cucumberResource);
            maybeFeature.ifPresent(results::add);
        });
        return results;
    }

    private void reportAnyCucumberSyntaxErrorsIn(Throwable gherkinError) {
        if (gherkinError instanceof FeatureParserException) {
            throw new InvalidFeatureFileException(gherkinError.getMessage(), gherkinError);
        }
    }


    public Optional<Narrative> loadFeatureNarrative(File narrativeFile) {

        Optional<AnnotatedFeature> loadedFeature = loadFeature(narrativeFile);

        if (!loadedFeature.isPresent()) {
            return Optional.empty();
        }

        Feature feature = loadedFeature.get().getFeature();

        String cardNumber = findCardNumberInTags(tagsDefinedIn(feature));
        List<String> versionNumbers = findVersionNumberInTags(tagsDefinedIn(feature));
        String title = feature.getName();
        String text = descriptionWithScenarioReferencesFrom(feature);

        String id = getIdFromName(title);

        Set<TestTag> requirementTags = feature.getTagsList().stream().map(tag -> TestTag.withValue(tag.getName())).collect(Collectors.toSet());
        requirementTags.add(TestTag.withName(title).andType("feature"));

        // Scenario Tags
        Map<String, Collection<TestTag>> scenarioTags = new HashMap<>();

        feature.getChildrenList().forEach(
                child -> {
                    if (child.hasScenario()) {
                        Scenario scenarioDefinition = child.getScenario();
                        if (scenarioDefinition.getExamplesCount() > 0) {
                            List<Tag> scenarioOutlineTags = scenarioDefinition.getTagsList();
                            scenarioTags.put(scenarioDefinition.getName(), CucumberTagConverter.toSerenityTags(scenarioOutlineTags));
                            List<Examples> examples = scenarioDefinition.getExamplesList();
                            for (Examples currentExample : examples) {
                                List<Tag> allExampleTags = new ArrayList<>();
                                allExampleTags.addAll(scenarioOutlineTags);
                                allExampleTags.addAll(currentExample.getTagsList());
                                scenarioTags.put(scenarioDefinition.getName() + "_examples_at_line:" + currentExample.getLocation().getLine(),
                                        CucumberTagConverter.toSerenityTags(allExampleTags));
                            }
                        } else {
                            scenarioTags.put(scenarioDefinition.getName(), tagsFrom(scenarioDefinition));
                        }
                    }
                }
        );

        // Scenario Names
        List<String> scenarios = feature.getChildrenList().stream().filter(FeatureChild::hasScenario).map(FeatureChild::getScenario).map(Scenario::getName).collect(Collectors.toList());

        return Optional.of(new Narrative(Optional.ofNullable(title),
                Optional.ofNullable(id),
                Optional.ofNullable(cardNumber),
                versionNumbers,
                "feature",
                text != null ? text : "",
                new ArrayList<>(requirementTags),
                scenarios,
                scenarioTags));

    }

    private Collection<TestTag> tagsFrom(Scenario scenarioDefinition) {
        if (scenarioDefinition.getExamplesCount() == 0) {
            return asSerenityTags(scenarioDefinition.getTagsList());
        } else {
            Set<TestTag> outlineTags = new HashSet<>(asSerenityTags(scenarioDefinition.getTagsList()));
            scenarioDefinition.getExamplesList().forEach(
                    examples -> outlineTags.addAll(asSerenityTags(examples.getTagsList()))
            );
            return outlineTags;
        }
    }

    private Set<TestTag> asSerenityTags(List<Feature.Tag> gherkinTags) {
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

    private boolean featureFileCouldNotBeReadFor(GherkinDocument.Feature feature) {
        return feature == null;
    }

    private List<Tag> tagsDefinedIn(Feature feature) {
        return feature.getTagsList();
    }

    private String findCardNumberInTags(List<Tag> tags) {

        for (Tag tag : tags) {
            if (tag.getName().toLowerCase().startsWith("@issue:")) {
                return tag.getName().replaceAll("@issue:", "");
            } else if (tag.getName().toLowerCase().startsWith("@issues:")) {
                String issueNumberList = tag.getName().replaceAll("@issues:", "");
                List<String> issueNumberTags = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(issueNumberList);
                return issueNumberTags.get(0);
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
