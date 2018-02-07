package net.thucydides.core.requirements.model.cucumber;

import com.google.common.base.Splitter;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.model.CucumberFeature;
import gherkin.ast.GherkinDocument;
import gherkin.ast.Tag;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.requirements.model.Narrative;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


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
        this(ThucydidesSystemProperty.FEATURE_FILE_LANGUAGE.from(environmentVariables,"en"), environmentVariables);
    }

    public CucumberParser(String locale, EnvironmentVariables environmentVariables) {
        this.locale = locale;
        this.encoding = ThucydidesSystemProperty.FEATURE_FILE_ENCODING.from(environmentVariables, Charset.defaultCharset().name());
    }

    public java.util.Optional<Narrative> loadFeatureNarrative(File narrativeFile)  {

        List<String> listOfFiles = new ArrayList<>();
        listOfFiles.add(narrativeFile.getAbsolutePath());
        List<CucumberFeature> cucumberFeatures = CucumberFeature.load(new MultiLoader(CucumberParser.class.getClassLoader()), listOfFiles);
        try {
            if (cucumberFeatures.size() == 0) {
                return java.util.Optional.empty();
            }
            CucumberFeature cucumberFeature = cucumberFeatures.get(0);
            GherkinDocument gherkinDocument = cucumberFeature.getGherkinFeature();
            if (featureFileCouldNotBeReadFor(gherkinDocument)) {
                return java.util.Optional.empty();
            }

            String cardNumber = findCardNumberInTags(tagsDefinedIn(cucumberFeature));
            List<String> versionNumbers = findVersionNumberInTags(tagsDefinedIn(cucumberFeature));
            String title = gherkinDocument.getFeature().getName();
            String text = gherkinDocument.getFeature().getDescription();
            String id = getIdFromName(title);

            return java.util.Optional.of(new Narrative(Optional.ofNullable(title),
                    Optional.ofNullable(id),
                    Optional.ofNullable(cardNumber),
                    versionNumbers,
                    "feature",
                    text !=null ? text : ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return java.util.Optional.empty();
    }

    private String getIdFromName(String name) {
        return name.replaceAll("[\\s_]", "-").toLowerCase();
    }

    private boolean featureFileCouldNotBeReadFor(GherkinDocument gherkinStructure) {
        return gherkinStructure.getFeature() == null;
    }

    private String filterOutCommentsFrom(String gherkin) {
        StringBuilder filteredGherkin = new StringBuilder();

        Scanner scanner = new Scanner(gherkin);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.trim().startsWith("#")) {
                filteredGherkin.append(line).append(System.lineSeparator());
            }
        }
        scanner.close();
        return filteredGherkin.toString();
    }


    private List<Tag> tagsDefinedIn(CucumberFeature cucumberFeature) {
        return  cucumberFeature.getGherkinFeature().getFeature().getTags();
    }

    private String findCardNumberInTags(List<Tag> tags) {

        for(Tag tag : tags) {
            if (tag.getName().toLowerCase().startsWith("@issue:")) {
                return tag.getName().replaceAll("@issue:","");
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
        for(Tag tag : tags) {
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
