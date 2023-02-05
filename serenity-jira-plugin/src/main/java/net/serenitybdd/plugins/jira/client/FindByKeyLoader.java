package net.serenitybdd.plugins.jira.client;

import com.google.common.cache.CacheLoader;
import net.serenitybdd.plugins.jira.domain.IssueSummary;

import java.util.Optional;

public class FindByKeyLoader extends CacheLoader<String, Optional<IssueSummary>> {
    private final JerseyJiraClient jiraClient;

    public FindByKeyLoader(JerseyJiraClient jiraClient) {
        this.jiraClient = jiraClient;
    }

    @Override
    public Optional<IssueSummary> load(String key) throws Exception {
        return jiraClient.loadByKey(key);
    }
}
