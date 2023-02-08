package net.serenitybdd.plugins.jira.requirements;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import net.serenitybdd.plugins.jira.domain.IssueSummary;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static net.serenitybdd.plugins.jira.requirements.JIRARequirementsConfiguration.JIRA_CUSTOM_FIELD;
import static net.serenitybdd.plugins.jira.requirements.JIRARequirementsConfiguration.JIRA_CUSTOM_NARRATIVE_FIELD;

public class RequirementsAdaptor {

    private final EnvironmentVariables environmentVariables;

    public RequirementsAdaptor(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public Requirement requirementFrom(IssueSummary issue) {

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
        Optional<String> customFieldName = Optional.fromNullable(environmentVariables.getProperty(JIRA_CUSTOM_NARRATIVE_FIELD.getName()));
        if (customFieldName.isPresent()) {
            return customFieldNameFor(issue, customFieldName.get()).or(ObjectUtils.firstNonNull(issue.getRendered().getDescription(), ""));
        } else {
            return issue.getRendered().getDescription();
        }

    }

    private List<String> definedCustomFields() {
        List<String> customFields = Lists.newArrayList();
        int customFieldIndex = 1;
        while (addCustomFieldIfDefined(environmentVariables, customFields, customFieldNumber(customFieldIndex++))) ;
        return customFields;
    }


    private Optional<String> customFieldNameFor(IssueSummary issue, String customFieldName) {
        if (issue.customField(customFieldName).isPresent()) {
            return Optional.of(issue.customField(customFieldName).get().asString());
        } else {
            return Optional.absent();
        }
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

    private String customFieldNumber(int customFieldIndex) {
        return JIRA_CUSTOM_FIELD.getName() + "." + customFieldIndex;
    }

}
