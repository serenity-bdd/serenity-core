package net.thucydides.core.requirements.model.cucumber;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import gherkin.formatter.model.Tag;
import gherkin.parser.Parser;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.requirements.model.Narrative;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
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

    public Optional<Narrative> loadFeatureNarrative(File narrativeFile)  {

        CucumberFeatureListener gherkinStructure = new CucumberFeatureListener();
        Parser parser = new Parser(gherkinStructure, true, "root", false, locale);
        try {
            String gherkinScenarios = filterOutCommentsFrom(FileUtils.readFileToString(narrativeFile, encoding));
            parser.parse(gherkinScenarios, narrativeFile.getName(),0);

            if (featureFileCouldNotBeReadFor(gherkinStructure)) {
                return Optional.absent();
            }

            String cardNumber = findCardNumberInTags(tagsDefinedIn(gherkinStructure));
            List<String> versionNumbers = findVersionNumberInTags(tagsDefinedIn(gherkinStructure));
            String title = gherkinStructure.getFeature().getName();
            String text = gherkinStructure.getFeature().getDescription();
            String id = gherkinStructure.getFeature().getId();

            return Optional.of(new Narrative(Optional.fromNullable(title),
                    Optional.fromNullable(id),
                    Optional.fromNullable(cardNumber),
                    versionNumbers,
                    "feature",
                    text));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Optional.absent();
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
        List<String> versionNumbers = Lists.newArrayList();
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
