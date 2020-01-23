package net.thucydides.core.reports.html;

import io.cucumber.tagexpressions.Expression;
import io.cucumber.tagexpressions.TagExpressionParser;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.stream.Collectors;

import static net.thucydides.core.ThucydidesSystemProperty.TAGS;

public class RequirementsFilter extends CucumberCompatibleFilter {

    public RequirementsFilter(EnvironmentVariables environmentVariables) {
        super(environmentVariables);
    }

    public boolean inDisplayOnlyTags(Requirement requirement) {
        Expression expectedTags = cucumberTagExpressionUsing(TAGS);
        return requirementMatchesAnyTagIn(requirement, expectedTags);
    }


    private boolean requirementMatchesAnyTagIn(Requirement requirement, Expression expectedTags) {
        List<String> requirementTags = tagsAsStrings(requirement.getTags());

        if (expectedTags.evaluate(requirementTags)) {
            return true;
        }
        return hasChildWithMatchingTag(requirement, expectedTags);
    }

    private boolean hasChildWithMatchingTag(Requirement requirement, Expression expectedTags) {
        if (!requirement.hasChildren()) {
            return false;
        }
        return requirement.getNestedChildren().stream().anyMatch(
                child -> expectedTags.evaluate(tagsAsStrings(child.getTags()))
        );
    }


    public List<Requirement> filteredByDisplayTag(List<Requirement> requirements) {
        return requirements.stream()
                .filter(this::inDisplayOnlyTags)
                .map(this::withFilteredChildren)
                .collect(Collectors.toList());
    }

    private Requirement withFilteredChildren(Requirement requirement) {
        if (!requirement.hasChildren()) { return requirement; }
        return requirement.withChildren(filteredByDisplayTag(requirement.getChildren()));
    }
}
