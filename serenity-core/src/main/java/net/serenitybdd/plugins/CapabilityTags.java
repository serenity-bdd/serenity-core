package net.serenitybdd.plugins;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.html.TagExclusions;
import net.thucydides.core.util.EnvironmentVariables;

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
