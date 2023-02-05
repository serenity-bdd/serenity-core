package net.serenitybdd.plugins.jira.client;

import com.google.common.cache.CacheLoader;

public class CountByKeyLoader extends CacheLoader<String, Integer> {
    private final JerseyJiraClient jiraClient;

    public CountByKeyLoader(JerseyJiraClient jiraClient) {
        this.jiraClient = jiraClient;
    }

    @Override
    public Integer load(String query) throws Exception {
        return jiraClient.countByJQL(query);
    }
}
