package net.thucydides.core.requirements.model.cucumber;

import com.google.common.base.Splitter;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.gherkin.FeatureParserException;
import io.cucumber.core.gherkin.vintage.internal.gherkin.ParserException;
import io.cucumber.core.internal.gherkin.ast.*;
import io.cucumber.core.internal.gherkin.events.CucumberEvent;
import io.cucumber.core.internal.gherkin.events.GherkinDocumentEvent;
import io.cucumber.core.internal.gherkin.events.SourceEvent;
import io.cucumber.core.internal.gherkin.stream.GherkinEvents;
import io.cucumber.core.internal.gherkin.stream.SourceEvents;
import io.cucumber.core.resource.Resource;
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

import static java.lang.System.lineSeparator;
import static java.util.Arrays.stream;


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
            
            String descriptionInComments = NarrativeFromCucumberComments.in(gherkinDocument.getComments());

            if (featureFileCouldNotBeReadFor(gherkinDocument.getFeature())) {
                return Optional.empty();
            }
            return Optional.of(new AnnotatedFeature(gherkinDocument.getFeature(),
                                                    gherkinDocument.getFeature().getChildren(),
                                                    descriptionInComments));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    private List<GherkinDocument> loadCucumberFeatures(List<String> listOfFiles) {
        for(String cucumberFile : listOfFiles) {
            searchForCucumberSyntaxErrorsIn(cucumberFile);
        }
        List<GherkinDocument> loadedFeatures = new ArrayList<>();
        SourceEvents sourceEvents = new SourceEvents(listOfFiles);
        GherkinEvents gherkinEvents = new GherkinEvents(true,true,true);
        for (SourceEvent sourceEventEvent : sourceEvents) {
            for (CucumberEvent cucumberEvent : gherkinEvents.iterable(sourceEventEvent)) {
                if(cucumberEvent instanceof GherkinDocumentEvent) {
                    GherkinDocumentEvent gherkinDocumentEvent = (GherkinDocumentEvent)cucumberEvent;
                    GherkinDocument gherkinDocument = gherkinDocumentEvent.document;
                    if (gherkinDocument.getFeature() != null) {
                        loadedFeatures.add(gherkinDocument);
                        LOGGER.debug("Added feature {}", gherkinDocument.getFeature().getName());
                    } else {
                        LOGGER.warn("Couldn't read the feature file {} - it will be ignored", gherkinDocumentEvent.uri);
                    }
                }
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
        } catch(Throwable throwable) {
            reportAnyCucumberSyntaxErrorsIn(throwable);
        }
    }

    private void reportAnyCucumberSyntaxErrorsIn(Throwable possibleGherkinSyntaxError) {
        if (possibleGherkinSyntaxError instanceof FeatureParserException) {
            Throwable gherkinError = possibleGherkinSyntaxError.getCause();
            if (gherkinError instanceof ParserException) {
                throw new InvalidFeatureFileException(gherkinError.getMessage(), gherkinError);
            }
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

        Set<TestTag> requirementTags = feature.getTags().stream().map(tag -> TestTag.withValue(tag.getName())).collect(Collectors.toSet());
        requirementTags.add(TestTag.withName(title).andType("feature"));

        // Scenario Tags
        Map<String, Collection<TestTag>> scenarioTags = new HashMap<>();

        feature.getChildren().forEach(
                scenarioDefinition -> {
                    if (scenarioDefinition instanceof ScenarioOutline) {
                        List<Tag> scenarioOutlineTags = ((ScenarioOutline) scenarioDefinition).getTags();
                        scenarioTags.put(scenarioDefinition.getName(), CucumberTagConverter.toSerenityTags(scenarioOutlineTags));
                        List<Examples> examples = ((ScenarioOutline) scenarioDefinition).getExamples();
                        for(Examples currentExample :  examples) {
                            List<Tag> allExampleTags = new ArrayList<>();
                            allExampleTags.addAll(scenarioOutlineTags);
                            allExampleTags.addAll(currentExample.getTags());
                            scenarioTags.put(scenarioDefinition.getName() + "_examples_at_line:" + currentExample.getLocation().getLine(),
                                    CucumberTagConverter.toSerenityTags(allExampleTags));
                        }
                    } else {
                        scenarioTags.put(scenarioDefinition.getName(), tagsFrom(scenarioDefinition));
                    }
                }
        );

        // Scenario Names
        List<String> scenarios = feature.getChildren().stream().map(ScenarioDefinition::getName).collect(Collectors.toList());

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

    private Collection<TestTag> tagsFrom(ScenarioDefinition scenarioDefinition) {
        if (scenarioDefinition instanceof Scenario) {
            return asSerenityTags(((Scenario) scenarioDefinition).getTags());
        } else if (scenarioDefinition instanceof ScenarioOutline) {
            Set<TestTag> outlineTags = new HashSet<>(asSerenityTags(((ScenarioOutline) scenarioDefinition).getTags()));
            ((ScenarioOutline) scenarioDefinition).getExamples().forEach(
                    examples -> outlineTags.addAll(asSerenityTags(examples.getTags()))
            );
            return outlineTags;
        }
        return new ArrayList<>();
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

        return stream(feature.getDescription().split("\\r?\\n"))
                .map(line -> DescriptionWithScenarioReferences.from(feature).forText(line))
                .collect(Collectors.joining(lineSeparator()));
    }


    private String getIdFromName(String name) {
        return name.replaceAll("[\\s_]", "-").toLowerCase();
    }

    private boolean featureFileCouldNotBeReadFor(Feature feature) {
        return feature == null;
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
