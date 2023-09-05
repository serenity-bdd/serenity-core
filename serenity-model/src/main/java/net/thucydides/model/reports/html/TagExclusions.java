package net.thucydides.model.reports.html;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_REPORT_EXCLUDE_TAGS;

/**
 * Tags can be excluded from the reports using the serenity.report.exclude.tags property
 * The '*' symbol can be used as a wildcard for any type or any value,
 * E.g. serenity.report.exclude.tags="tag1, tag2, errors:*, *:green"
 */
public class TagExclusions {
    private final EnvironmentVariables environmentVariables;
    private final List<String> excludedTags;
    private final TestOutcomes testOutcomes;

    private final List<String> ALWAYS_HIDDEN_TAGS = Arrays.asList("manual-result","manual-test-evidence","manual-last-tested","singlebrowser");

    public TagExclusions(EnvironmentVariables environmentVariables, TestOutcomes testOutcomes) {
        this.environmentVariables = environmentVariables;
        this.testOutcomes = testOutcomes;
        this.excludedTags = new ArrayList<>(EnvironmentSpecificConfiguration.from(environmentVariables).getListOfValues(SERENITY_REPORT_EXCLUDE_TAGS));
        this.excludedTags.addAll(EnvironmentSpecificConfiguration.from(environmentVariables).getListOfValues(ThucydidesSystemProperty.HIDDEN_TAGS));
        this.excludedTags.addAll(ALWAYS_HIDDEN_TAGS);
    }

    public static TagExclusions usingEnvironment(EnvironmentVariables environmentVariables) {
        return new TagExclusions(environmentVariables, TestOutcomesContext.getCurrentTestOutcomes());
    }

    public static TagExclusions usingEnvironment(EnvironmentVariables environmentVariables, TestOutcomes testOutcomes) {
        return new TagExclusions(environmentVariables, testOutcomes);
    }

    public boolean doNotExclude(TestTag tag) {
        if (isBrowserOrPlatformContext(tag)) {
            // If this tag relates to a context, make sure we aren't excluding that context
            return !excludedContext(tag);
        } else {
            // Otherwise check the configured list of generally excluded tags
            return excludedTags.stream().noneMatch(
                    excludedTag -> matches(tag, excludedTag)
            );
        }
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

    private boolean isBrowserOrPlatformContext(TestTag tag) {
        return tag.isABrowser() || tag.isAPlatform() || tag.isABrowserPlatformCombination();
    }
    /**
     * Should we exclude this tag because it is a browser or platform context, and we are not reporting on contexts?
     * If there is only one browser or platform context, we don't need to report on it.
     */
    private boolean excludedContext(TestTag tag) {
        if (testOutcomes == null) {
            return false;
        }
        boolean reportBrowserContexts = (this.testOutcomes.getBrowserContexts().size() > 1);
        boolean reportPlatformContexts = (this.testOutcomes.getPlatformContexts().size() > 1);
        boolean reportPlatformAndBrowserContexts = reportBrowserContexts && reportPlatformContexts;

        if (tag.isABrowser() && !reportBrowserContexts) {
            return true;
        }
        if (tag.isAPlatform() && !reportPlatformContexts) {
            return true;
        }
        if (tag.isABrowserPlatformCombination() && !reportPlatformAndBrowserContexts) {
            return true;
        }
        return false;
    }


}
