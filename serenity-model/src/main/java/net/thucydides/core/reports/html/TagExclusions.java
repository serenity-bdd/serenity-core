package net.thucydides.core.reports.html;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_REPORT_EXCLUDE_TAGS;

/**
 * Tags can be excluded from the reports using the serenity.report.exclude.tags property
 * The '*' symbol can be used as a wildcard for any type or any value,
 * E.g. serenity.report.exclude.tags="tag1, tag2, errors:*, *:green"
 */
public class TagExclusions {
    private final EnvironmentVariables environmentVariables;
    private final List<String> excludedTags;

    private final List<String> ALWAYS_HIDDEN_TAGS = Arrays.asList("manual-result","manual-test-evidence","manual-last-tested","singlebrowser");

    public TagExclusions(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.excludedTags = new ArrayList<>(EnvironmentSpecificConfiguration.from(environmentVariables).getListOfValues(SERENITY_REPORT_EXCLUDE_TAGS));
        this.excludedTags.addAll(EnvironmentSpecificConfiguration.from(environmentVariables).getListOfValues(ThucydidesSystemProperty.HIDDEN_TAGS));
        this.excludedTags.addAll(ALWAYS_HIDDEN_TAGS);
    }

    public static TagExclusions usingEnvironment(EnvironmentVariables environmentVariables) {
        return new TagExclusions(environmentVariables);
    }

    public boolean doNotExclude(TestTag tag) {
        return excludedTags.stream().noneMatch(
                excludedTag -> matches(tag, excludedTag)
        );
    }

    private boolean matches(TestTag tag, String excludedTag) {
        if (tag.equalsIgnoreCase(TestTag.withValue(excludedTag))) {
            return true;
        }
        if (excludedTag.endsWith(":*")) {
            String tagType = excludedTag.replace(":*","");
            return tag.getType().equalsIgnoreCase(tagType);
        }
        if (excludedTag.startsWith("*:")) {
            String tagValue = excludedTag.replace("*:","");
            return tag.getName().equalsIgnoreCase(tagValue);
        }
        return false;
    }

}
