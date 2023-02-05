package net.serenitybdd.plugins.jira.requirements;

public enum JIRARequirementsConfiguration {

    /**
     * The custom field used to store the story narrative description.
     */
    JIRA_CUSTOM_NARRATIVE_FIELD("jira.custom.narrative.field"),
    JIRA_CUSTOM_FIELD("jira.custom.field"),
    JIRA_MAX_THREADS("jira.max.threads"),

    JIRA_ROOT_ISSUE_TYPE("jira.root.issue.type"),
    JIRA_REQUIREMENT_LINKS("jira.requirement.links");

    private final String name;

    public String getName() {
        return name;
    }

    JIRARequirementsConfiguration(String name) {
        this.name = name;
    }
}

