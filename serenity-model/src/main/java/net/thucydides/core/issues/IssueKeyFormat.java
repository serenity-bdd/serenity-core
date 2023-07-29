package net.thucydides.core.issues;

import net.serenitybdd.core.strings.Joiner;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

public class IssueKeyFormat {

    private final EnvironmentVariables environmentVariables;

    private IssueKeyFormat(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    private String getProjectPrefix() {
        return ThucydidesSystemProperty.SERENITY_PROJECT_KEY.from(environmentVariables);
    }

    public String andKey(String issueNumber) {
        String issueKey = issueNumber;
        if (issueKey.startsWith("#")) {
            issueKey = issueKey.substring(1);
        }
        if (StringUtils.isNumeric(issueKey) && (getProjectPrefix() != null)) {
            Joiner joiner = Joiner.on("-");
            issueKey = joiner.join(getProjectPrefix(), issueKey);
        }
        return issueKey;
    }

    public static IssueKeyFormat forEnvironment(EnvironmentVariables environmentVariables) {
        return new IssueKeyFormat(environmentVariables);
    }
}
