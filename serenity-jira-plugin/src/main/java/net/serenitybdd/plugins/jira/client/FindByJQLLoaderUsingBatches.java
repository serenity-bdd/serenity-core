package net.serenitybdd.plugins.jira.client;

import com.google.common.cache.CacheLoader;
import net.serenitybdd.plugins.jira.domain.IssueSummary;

import java.util.List;

public class FindByJQLLoaderUsingBatches extends CacheLoader<String, List<IssueSummary>> {
    private final JerseyJiraClient jiraClient;

    public FindByJQLLoaderUsingBatches(JerseyJiraClient jiraClient) {
        this.jiraClient = jiraClient;
    }

    @Override
    public List<IssueSummary> load(String query) throws Exception {
        return jiraClient.loadByJQLBatches(query);
    }
}
