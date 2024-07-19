package net.serenitybdd.plugins;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.reports.html.TagExclusions;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.stream.Collectors;

public class CapabilityTags {

    public static String[] tagsFrom(TestOutcome testOutcome, EnvironmentVariables environmentVariables) {
        TagExclusions exclusions = TagExclusions.usingEnvironment(environmentVariables);
        return testOutcome.getTags().stream()
                .filter(exclusions::doNotExclude)
                .map(TestTag::toString)
                .map(CapabilityTags::stripTagPrefixFrom)
                .collect(Collectors.toList()).toArray(new String[]{});
    }

    private static String stripTagPrefixFrom(String tagName) {
        if (tagName.toLowerCase().startsWith("tag:")) {
            return tagName.substring(4);
        } else {
            return tagName;
        }
    }
}
