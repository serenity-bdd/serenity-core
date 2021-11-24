package net.thucydides.core.reports.html;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_REPORT_EXCLUDE_TAGS;

public class TagExclusions {
    private final EnvironmentVariables environmentVariables;
    private final List<String> excludedTags;

    public TagExclusions(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.excludedTags = EnvironmentSpecificConfiguration.from(environmentVariables).getListOfValues(SERENITY_REPORT_EXCLUDE_TAGS);
    }

    public static TagExclusions usingEnvironment(EnvironmentVariables environmentVariables) {
        return new TagExclusions(environmentVariables);
    }

    public boolean doNotExclude(TestTag tag) {
        return excludedTags.stream().noneMatch(
                excludedTag -> tag.equals(TestTag.withValue(excludedTag))
        );
    }

}
