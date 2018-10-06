package net.thucydides.core.requirements.model.cucumber;

import com.google.common.base.Splitter;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.FeatureLoader;
import gherkin.ast.Feature;
import gherkin.ast.GherkinDocument;
import gherkin.ast.Tag;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.requirements.model.Narrative;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.nio.charset.Charset;
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

    public Optional<Feature> loadFeature(File narrativeFile) {
        if (narrativeFile == null) {
            return Optional.empty();
        }
        if (!narrativeFile.exists()) {
            return Optional.empty();
        }

        List<String> listOfFiles = new ArrayList<>();
        listOfFiles.add(narrativeFile.getAbsolutePath());
        MultiLoader multiLoader = new MultiLoader(CucumberParser.class.getClassLoader());
        List<CucumberFeature> cucumberFeatures = new FeatureLoader(multiLoader).load(listOfFiles);
        try {
            if (cucumberFeatures.size() == 0) {
                return Optional.empty();
            }
            CucumberFeature cucumberFeature = cucumberFeatures.get(0);
            GherkinDocument gherkinDocument = cucumberFeature.getGherkinFeature();
            if (featureFileCouldNotBeReadFor(gherkinDocument.getFeature())) {
                return Optional.empty();
            }
            return Optional.of(gherkinDocument.getFeature());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public java.util.Optional<Narrative> loadFeatureNarrative(File narrativeFile) {

        Optional<Feature> loadedFeature = loadFeature(narrativeFile);

        if (!loadedFeature.isPresent()) {
            return Optional.empty();
        }

        Feature feature = loadedFeature.get();

        String cardNumber = findCardNumberInTags(tagsDefinedIn(feature));
        List<String> versionNumbers = findVersionNumberInTags(tagsDefinedIn(feature));
        String title = feature.getName();
        String text = descriptionWithScenarioReferencesFrom(feature);

        String id = getIdFromName(title);

        return Optional.of(new Narrative(Optional.ofNullable(title),
                Optional.ofNullable(id),
                Optional.ofNullable(cardNumber),
                versionNumbers,
                "feature",
                text != null ? text : ""));

    }

    private String descriptionWithScenarioReferencesFrom(Feature feature) {
        if (feature.getDescription() == null) { return ""; }

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
