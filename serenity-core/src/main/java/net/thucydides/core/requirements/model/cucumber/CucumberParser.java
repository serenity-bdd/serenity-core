package net.thucydides.core.requirements.model.cucumber;

import com.google.common.base.Optional;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.model.CucumberFeature;
import gherkin.ast.GherkinDocument;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.requirements.model.Narrative;
import net.thucydides.core.util.EnvironmentVariables;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//import gherkin.pickles.;
//import gherkin.parser.Parser;

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

        //CucumberFeatureListener gherkinStructure = new CucumberFeatureListener();
        //Parser parser = new Parser(gherkinStructure, true, "root", false, locale);
        List<String> listOfFiles = new ArrayList<>();
        listOfFiles.add(narrativeFile.getAbsolutePath());
        List<CucumberFeature> cucumberFeatures = CucumberFeature.load(new MultiLoader(CucumberParser.class.getClassLoader()), listOfFiles);
        try {
            /*String gherkinScenarios = filterOutCommentsFrom(FileUtils.readFileToString(narrativeFile, encoding));
            parser.parse(gherkinScenarios, narrativeFile.getName(),0);

            if (featureFileCouldNotBeReadFor(gherkinStructure)) {
                return java.util.Optional.empty();
            }*/

            if (cucumberFeatures.size() == 0) {
                return java.util.Optional.empty();
            }
            CucumberFeature cucumberFeature = cucumberFeatures.get(0);

            /* TODO
            String cardNumber = findCardNumberInTags(tagsDefinedIn(gherkinStructure));
            List<String> versionNumbers = findVersionNumberInTags(tagsDefinedIn(gherkinStructure));
            String title = gherkinStructure.getFeature().getName();
            String text = gherkinStructure.getFeature().getDescription();
            String id = gherkinStructure.getFeature().getId();*/

            List<String> versionNumbers = new ArrayList<>(); //TODO
            GherkinDocument gherkinDocument = cucumberFeature.getGherkinFeature();
            String title = gherkinDocument.getFeature().getName();
            String text = gherkinDocument.getFeature().getDescription().trim();
            String id = "";//TODO
            String cardNumber = "";//TODO


            return java.util.Optional.of(new Narrative(Optional.fromNullable(title),
                    Optional.fromNullable(id),
                    Optional.fromNullable(cardNumber),
                    versionNumbers,
                    "feature",
                    text));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return java.util.Optional.empty();
    }

    private boolean featureFileCouldNotBeReadFor(CucumberFeatureListener gherkinStructure) {
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

    /** TODO
    private List<Tag> tagsDefinedIn(CucumberFeatureListener gherkinStructure) {
        return(gherkinStructure.getFeature() != null) ?  gherkinStructure.getFeature().getTags() : Lists.<Tag>newArrayList();
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
    */
}
