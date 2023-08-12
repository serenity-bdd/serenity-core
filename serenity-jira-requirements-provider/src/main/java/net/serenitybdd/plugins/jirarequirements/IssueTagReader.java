package net.serenitybdd.plugins.jirarequirements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.serenitybdd.plugins.jira.client.JerseyJiraClient;
import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.serenitybdd.plugins.jira.model.JQLException;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.requirements.model.Requirement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class IssueTagReader {

    private final Logger logger = LoggerFactory.getLogger(IssueTagReader.class);

    private final List<Requirement> flattenedRequirements;
    private final JerseyJiraClient jiraClient;
    private final String projectKey;
    private final List<TestTag> tags = Lists.newArrayList();

    public IssueTagReader(JerseyJiraClient jiraClient, List<Requirement> flattenedRequirements, String projectKey) {
        this.flattenedRequirements = flattenedRequirements;
        this.jiraClient = jiraClient;
        this.projectKey = projectKey;
    }

    public IssueTagReader addVersionTags(String issueKey) {
        String decodedIssueKey = decoded(issueKey);
        try {
            java.util.Optional<IssueSummary> issue = jiraClient.findByKey(issueKey);
            if (issue.isPresent()) {
                addVersionTags(issue.get().getFixVersions());
            }
        } catch (JQLException e) {
            logger.warn("Could not read versions for issue " + decodedIssueKey, e);
        }
        return this;
    }

    private void addVersionTags(List<String> versions) {
        for(String version : versions) {
            TestTag versionTag = TestTag.withName(version).andType("Version");
            tags.add(versionTag);
        }
    }

    public IssueTagReader addRequirementTags(String issueKey) {
        String decodedIssueKey = decoded(issueKey);
        List<Requirement> parentRequirements = getAssociatedRequirementsOf(decodedIssueKey);// getParentRequirementsOf(decodedIssueKey);
        for (Requirement parentRequirement : parentRequirements) {
            tags.add(parentRequirement.asTag());
        }
        return this;
    }

    public IssueTagReader addIssueTags(String issueKey) {
        String decodedIssueKey = decoded(issueKey);
        java.util.Optional<IssueSummary> behaviourIssue = java.util.Optional.empty();
        try {
            behaviourIssue = jiraClient.findByKey(decodedIssueKey);
        } catch (JQLException e) {
            logger.warn("Could not read tags for issue " + decodedIssueKey, e);
        }
        if (behaviourIssue.isPresent()) {
            tags.add(TestTag.withName(behaviourIssue.get().getSummary()).andType(behaviourIssue.get().getType()));
        }
        return this;
    }

    public List<TestTag> getTags() {
        return ImmutableList.copyOf(tags);
    }

    private List<Requirement> getAssociatedRequirementsOf(String issueKey) {
        for (Requirement requirement : flattenedRequirements) {
            if (requirement.getCardNumber().equalsIgnoreCase(issueKey)) {
                List<Requirement> associatedRequirements = Lists.newArrayList(requirement);
                associatedRequirements.addAll(getParentRequirementsOf(issueKey));
                return ImmutableList.copyOf(associatedRequirements);
            }
        }
        return ImmutableList.of();
    }

    private List<Requirement> getParentRequirementsOf(String issueKey) {
        List<Requirement> parentRequirements = Lists.newArrayList();

        Optional<Requirement> parentRequirement = getParentRequirementOf(issueKey);
        if (parentRequirement.isPresent()) {
            parentRequirements.add(parentRequirement.get());
            parentRequirements.addAll(getParentRequirementsOf(parentRequirement.get().getCardNumber()));
        }

        return parentRequirements;
    }

    private Optional<Requirement> getParentRequirementOf(String key) {
        for (Requirement requirement : flattenedRequirements) {
            if (containsRequirementWithId(key, requirement.getChildren())) {
                return Optional.of(requirement);
            }
        }
        return Optional.empty();
    }

    private boolean containsRequirementWithId(String key, List<Requirement> requirements) {
        for (Requirement requirement : requirements) {
            if (requirement.getCardNumber().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private String decoded(String issueKey) {
        if (issueKey.startsWith("#")) {
            issueKey = issueKey.substring(1);
        }
        if (StringUtils.isNumeric(issueKey)) {
            issueKey = projectKey + "-" + issueKey;
        }
        return issueKey;
    }

}
