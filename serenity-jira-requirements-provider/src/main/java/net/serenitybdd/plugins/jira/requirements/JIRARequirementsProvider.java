package net.serenitybdd.plugins.jira.requirements;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.serenitybdd.plugins.jira.client.JerseyJiraClient;
import net.serenitybdd.plugins.jira.client.LoadingStrategy;
import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.serenitybdd.plugins.jira.model.JQLException;
import net.serenitybdd.plugins.jira.service.JIRAConfiguration;
import net.serenitybdd.plugins.jira.service.SystemPropertiesJIRAConfiguration;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.RequirementsList;
import net.thucydides.core.requirements.RequirementsTagProvider;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static net.serenitybdd.plugins.jira.requirements.JIRARequirementsConfiguration.JIRA_CUSTOM_FIELD;
import static net.serenitybdd.plugins.jira.requirements.JIRARequirementsConfiguration.JIRA_CUSTOM_NARRATIVE_FIELD;


/**
 * Integrate Thucydides reports with requirements, epics and stories in a JIRA server.
 */
public class JIRARequirementsProvider implements RequirementsTagProvider {

    private List<Requirement> requirements = null;
    private final JerseyJiraClient jiraClient;
    private final String projectKey;
    private final EnvironmentVariables environmentVariables;

    private final String EPIC_LINK = "Epic Link";

    private final Logger logger = LoggerFactory.getLogger(JIRARequirementsProvider.class);

    public JIRARequirementsProvider() {
        this(new SystemPropertiesJIRAConfiguration(SerenityInfrastructure.getEnvironmentVariables()),
                SerenityInfrastructure.getEnvironmentVariables() );
    }

    public JIRARequirementsProvider(JIRAConfiguration jiraConfiguration) {
        this(jiraConfiguration, SerenityInfrastructure.getEnvironmentVariables());
    }

    public JIRARequirementsProvider(JIRAConfiguration jiraConfiguration, EnvironmentVariables environmentVariables) {
        logConnectionDetailsFor(jiraConfiguration);
        projectKey = jiraConfiguration.getProject();
        this.environmentVariables = environmentVariables;
        jiraClient = new ConfigurableJiraClient(jiraConfiguration.getJiraUrl(),
                jiraConfiguration.getJiraUser(),
                jiraConfiguration.getJiraPassword(),
                projectKey).usingCustomFields(customFieldsDefinedIn(environmentVariables));
    }

    private List<String> definedCustomFields() {
        List<String> customFields = Lists.newArrayList();
        int customFieldIndex = 1;
        while (addCustomFieldIfDefined(environmentVariables, customFields, customFieldNumber(customFieldIndex++))) ;
        return customFields;
    }

    private List<String> customFieldsDefinedIn(EnvironmentVariables environmentVariables) {
        List<String> customFields = Lists.newArrayList();
        addCustomFieldIfDefined(environmentVariables, customFields,
                JIRARequirementsConfiguration.JIRA_CUSTOM_NARRATIVE_FIELD.getName());
        customFields.addAll(definedCustomFields());
        return customFields;
    }

    private String customFieldNumber(int customFieldIndex) {
        return JIRA_CUSTOM_FIELD.getName() + "." + customFieldIndex;
    }

    private boolean addCustomFieldIfDefined(EnvironmentVariables environmentVariables,
                                            List<String> customFields,
                                            String customField) {
        String customFieldName = environmentVariables.getProperty(customField);
        if (StringUtils.isNotEmpty(customFieldName)) {
            customFields.add(customFieldName);
            return true;
        }
        return false;
    }

    private void logConnectionDetailsFor(JIRAConfiguration jiraConfiguration) {
        logger.debug("JIRA URL: {}", jiraConfiguration.getJiraUrl());
        logger.debug("JIRA project: {}", jiraConfiguration.getProject());
        logger.debug("JIRA user: {}", jiraConfiguration.getJiraUser());
    }

    private String getProjectKey() {
        return projectKey;
    }

    @Override
    public List<Requirement> getRequirements() {

        requirements = persisted(requirements);
        if ((requirements == null) && providerActivated()) {

            List<IssueSummary> rootRequirementIssues;
            logger.debug("Loading root requirements: " + rootRequirementsJQL());
            try {
                rootRequirementIssues = jiraClient.findByJQL(rootRequirementsJQL(), LoadingStrategy.LOAD_IN_BATCHES);
            } catch (JQLException e) {
                logger.debug("No root requirements found (JQL = " + rootRequirementsJQL(), e);
                rootRequirementIssues = Lists.newArrayList();
            }
            logger.debug("Loading root requirements done: " + rootRequirementIssues.size());

            RequirementsLoader requirementsLoader = new ConcurrentRequirementsLoader(environmentVariables, this);
            requirements = requirementsLoader.loadFrom(rootRequirementIssues);
            requirements = addParentsTo(requirements);
            persist(requirements);

        }
        return requirements;
    }

    private List<Requirement> persisted(List<Requirement> requirements) {
        if (requirements != null) {
            return requirements;
        }
        return null;
    }

    private void persist(List<Requirement> requirements) {
        // TODO: Implement me
    }

    private boolean providerActivated() {
        return environmentVariables.getPropertyAsBoolean("serenity.providers.jira-requirements-provider", true);
    }

    private List<Requirement> addParentsTo(List<Requirement> requirements) {
        return addParentsTo(requirements, null);
    }

    private final List<Requirement> NO_REQUIREMENTS = ImmutableList.of();

    private List<Requirement> addParentsTo(List<Requirement> requirements, String parent) {
        List<Requirement> augmentedRequirements = Lists.newArrayList();
        for(Requirement requirement : requirements) {
            List<Requirement> children = requirement.hasChildren()
                    ? addParentsTo(requirement.getChildren(),requirement.getName()) : NO_REQUIREMENTS;
            augmentedRequirements.add(requirement.withParent(parent).withChildren(children));
        }
        return augmentedRequirements;
    }

    private Requirement requirementFrom(IssueSummary issue) {

        Requirement baseRequirement = Requirement.named(issue.getSummary())
                .withOptionalCardNumber(issue.getKey())
                .withType(issue.getType())
                .withNarrative(narativeTextFrom(issue))
                .withReleaseVersions(issue.getFixVersions());

        for (String fieldName : definedCustomFields()) {
            if (issue.customField(fieldName).isPresent()) {
                String value = issue.customField(fieldName).get().asString();
                String renderedValue = issue.getRendered().customField(fieldName).get();
                baseRequirement = baseRequirement.withCustomField(fieldName).setTo(value, renderedValue);
            }
        }
        return baseRequirement;
    }

    private String narativeTextFrom(IssueSummary issue) {
        Optional<String> customFieldName = Optional.ofNullable(environmentVariables.getProperty(JIRA_CUSTOM_NARRATIVE_FIELD.getName()));
        if (customFieldName.isPresent()) {
            return customFieldNameFor(issue, customFieldName.get()).orElse(ObjectUtils.firstNonNull(issue.getRendered().getDescription(), ""));
        } else {
            return issue.getRendered().getDescription();
        }

    }

    private Optional<String> customFieldNameFor(IssueSummary issue, String customFieldName) {
        if (issue.customField(customFieldName).isPresent()) {
            return Optional.of(issue.customField(customFieldName).get().asString());
        } else {
            return Optional.empty();
        }
    }


    protected List<Requirement> findChildrenFor(Requirement parent, final int level) {
        List<IssueSummary> children;

        long t0 = System.currentTimeMillis();
        try {
            logger.debug("Loading child requirements for: " + parent.getName());
            children = jiraClient.findByJQL(childIssuesJQL(parent, level), LoadingStrategy.LOAD_IN_SINGLE_QUERY);

            logger.debug("Loading child requirements for " + parent.getName() + " done: " + children.size());
        } catch (JQLException e) {
            logger.warn("No children found for requirement " + parent, e);
            return NO_REQUIREMENTS;
        }

        final List<Requirement> childRequirements = Collections.synchronizedList(new ArrayList<Requirement>());
        for(IssueSummary childIssue : children) {
            Requirement childRequirement = requirementFrom(childIssue);
            if (moreRequirements(level)) {
                List<Requirement> grandChildren = findChildrenFor(childRequirement, level + 1);
                childRequirement = childRequirement.withChildren(grandChildren);
            }
            childRequirements.add(childRequirement);
        }
        logger.debug("{} child requirements loaded in: {} ms", childRequirements.size(), System.currentTimeMillis() - t0);
        logger.debug("Child requirements: {}", childRequirements);
        return childRequirements;
    }

    private String childIssuesJQL(Requirement parent, int level) {
        String linkType = getRequirementsLinks().get(level);
        if (linkType.equals(EPIC_LINK)) {
            return "'" + getRequirementsLinks().get(level) + "' = " + parent.getCardNumber();
        } else {
            return "issue in linkedIssues(" + parent.getCardNumber() + ",\"" + linkType + "\")";
        }
    }

    private boolean moreRequirements(int level) {
        return level < getRequirementsLinks().size() - 1;
    }



    private String rootRequirementsJQL() {
        return "issuetype = " + getRootIssueType() + " and project=" + getProjectKey();
    }

    private String getRootIssueType() {
        return environmentVariables.getProperty(JIRARequirementsConfiguration.JIRA_ROOT_ISSUE_TYPE.getName(), "epic");
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(TestOutcome testOutcome) {
        logger.debug("Find parent requirement in JIRA for " + testOutcome.getTitle());
        List<String> issueKeys = testOutcome.getIssueKeys();
        if (!issueKeys.isEmpty() && providerActivated()) {
            try {
                java.util.Optional<IssueSummary> parentIssue = jiraClient.findByKey(issueKeys.get(0));
                if (parentIssue.isPresent()) {
                    logger.debug("Parent found: " + parentIssue.get());
                    return Optional.of(requirementFrom(parentIssue.get()));
                } else {
                    return Optional.empty();
                }
            } catch (JQLException e) {
                if (noSuchIssue(e)) {
                    return Optional.empty();
                } else {
                    throw new IllegalArgumentException(e);
                }
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Requirement> getParentRequirementOf(Requirement requirement) {
        for (Requirement candidateParent : RequirementsList.of(getRequirements()).asFlattenedList()) {
            if (candidateParent.getChildren().contains(requirement)) {
                return Optional.of(candidateParent);
            }
        }
        return Optional.empty();
    }

    private boolean noSuchIssue(JQLException e) {
        return e.getMessage().contains("error 400");
    }

    @Override
    public Optional<Requirement> getRequirementFor(TestTag testTag) {
        for (Requirement requirement : getFlattenedRequirements()) {
            if (requirement.getType().equals(testTag.getType()) && requirement.getName().equals(testTag.getName())) {
                return Optional.of(requirement);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<TestTag> getTagsFor(TestOutcome testOutcome) {
        List<String> issues = testOutcome.getIssueKeys();
        Set<TestTag> tags = Sets.newHashSet();
        for (String issue : issues) {
            tags.addAll(tagsFromIssue(issue));
        }
        return ImmutableSet.copyOf(tags);
    }

    private Collection<? extends TestTag> tagsFromIssue(String issueKey) {
        if (providerActivated()) {
            IssueTagReader tagReader = new IssueTagReader(jiraClient, getFlattenedRequirements(), projectKey);
            return tagReader.addIssueTags(issueKey)
                    .addRequirementTags(issueKey)
                    .addVersionTags(issueKey).getTags();
        } else {
            return ImmutableList.of();
        }
    }

    private List<Requirement> getFlattenedRequirements() {
        return getFlattenedRequirements(getRequirements());
    }

    private List<Requirement> getFlattenedRequirements(List<Requirement> someRequirements) {
        List<Requirement> flattenedRequirements = Lists.newArrayList();
        for (Requirement requirement : someRequirements) {
            flattenedRequirements.add(requirement);
            flattenedRequirements.addAll(getFlattenedRequirements(requirement.getChildren()));
        }
        return flattenedRequirements;
    }

    public List<String> getRequirementsLinks() {
        String requirementLinks = environmentVariables.getProperty(JIRARequirementsConfiguration.JIRA_REQUIREMENT_LINKS.getName(),
                                                                   "Epic Link");
        return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(requirementLinks);
    }
}
