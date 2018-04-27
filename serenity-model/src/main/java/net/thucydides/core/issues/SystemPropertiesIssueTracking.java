package net.thucydides.core.issues;

import com.google.inject.Inject;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Determine the issue tracking URL formats for a project.
 */
public class SystemPropertiesIssueTracking implements IssueTracking {

    private EnvironmentVariables environmentVariables;
    
    public SystemPropertiesIssueTracking() {
        this(ConfiguredEnvironment.getEnvironmentVariables());
    }

    @Inject
    public SystemPropertiesIssueTracking(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getIssueTrackerUrl() {
        if (jiraUrlDefined()) {

            return ThucydidesSystemProperty.JIRA_URL.from(environmentVariables)
                                         + "/browse/" + "{0}";
        } else {
            return ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL.from(environmentVariables);
        }
    }

    public String getShortenedIssueTrackerUrl() {
        if (jiraUrlDefined()) {
            return ThucydidesSystemProperty.JIRA_URL.from(environmentVariables)
                    + "/browse/" + getJiraProjectSuffix() + "{0}";
        } else {
            return ThucydidesSystemProperty.THUCYDIDES_ISSUE_TRACKER_URL.from(environmentVariables);
        }
    }

    private String getJiraProjectSuffix() {
        if (jiraProjectDefined()) {
            return ThucydidesSystemProperty.JIRA_PROJECT.from(environmentVariables) + "-";
        } else {
            return "";
        }
    }

    private boolean jiraUrlDefined() {
       return !isEmpty(ThucydidesSystemProperty.JIRA_URL.from(environmentVariables));
    }

    private boolean jiraProjectDefined() {
       return !isEmpty(ThucydidesSystemProperty.JIRA_PROJECT.from(environmentVariables));
    }
}
