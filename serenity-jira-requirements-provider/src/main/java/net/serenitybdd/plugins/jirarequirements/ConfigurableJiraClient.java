package net.serenitybdd.plugins.jirarequirements;

import com.google.common.collect.Lists;
import net.serenitybdd.plugins.jira.client.JIRAAuthenticationError;
import net.serenitybdd.plugins.jira.client.JIRAConfigurationError;
import net.serenitybdd.plugins.jira.client.JerseyJiraClient;
import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.serenitybdd.plugins.jira.model.JQLException;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ConfigurableJiraClient extends JerseyJiraClient {

    private static final String FAIL_ON_JIRA_ERROR = "thucydides.fail.on.jira.error";
    private final EnvironmentVariables environmentVariables;

    private final Logger logger = LoggerFactory.getLogger(JIRARequirementsProvider.class);

    public ConfigurableJiraClient(String url, String username, String password, String project) {
        super(url, username, password, project, customFields());
        environmentVariables = SerenityInfrastructure.getEnvironmentVariables();
    }

    private static List<String> customFields() {
        EnvironmentVariables environmentVariables = SerenityInfrastructure.getEnvironmentVariables();
        // TODO: Are the custom fields supposed to come from the environment variables?
        return Lists.newArrayList();
    }

    @Override
    public List<IssueSummary> findByJQL(String query) {
        try {
            return super.findByJQL(query);
        } catch(JIRAAuthenticationError authenticationError) {
            if (failOnJiraErrors()) {
                throw authenticationError;
            } else {
                logger.error("Could not connect to JIRA", authenticationError);
            }
        } catch(JIRAConfigurationError configurationError) {
            if (failOnJiraErrors()) {
                throw configurationError;
            } else {
                logger.error("Could not connect to JIRA", configurationError);
            }

        }
        return Lists.newArrayList();
    }

    private boolean failOnJiraErrors() {
        return environmentVariables.getPropertyAsBoolean(FAIL_ON_JIRA_ERROR,false);
    }

    @Override
    public Optional<IssueSummary> findByKey(String key) throws JQLException {
        try {
            return super.findByKey(key);
        } catch(JIRAAuthenticationError authenticationError) {

        } catch(JIRAConfigurationError configurationError) {

        }
        return Optional.empty();
    }
}
