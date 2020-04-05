package net.thucydides.core.reports.html;

import io.cucumber.tagexpressions.Expression;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.function.Predicate;
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
        if (new RequirementTagMatcher(expectedTags).test(requirement)) {
            return true;
        }
        return hasChildWithMatchingTag(requirement, expectedTags);
    }

    private boolean matchExistsInScenarios(Expression expectedTags, Requirement requirement) {
        return requirement.getScenarioTags().values().stream()
                .map(CucumberTagConverter::toStrings)
                .anyMatch(expectedTags::evaluate);
    }

    private boolean hasChildWithMatchingTag(Requirement requirement, Expression expectedTags) {
        if (!requirement.hasChildren()) {
            return false;
        }
        return requirement.getNestedChildren().stream().anyMatch(new RequirementTagMatcher(expectedTags));
    }

    private class RequirementTagMatcher implements Predicate<Requirement> {

        private Expression expectedTags;

        public RequirementTagMatcher(Expression expectedTags) {
            this.expectedTags = expectedTags;
        }

        @Override
        public boolean test(Requirement requirement) {
            return expectedTags.evaluate(CucumberTagConverter.toStrings(requirement.getTags())) ||  matchExistsInScenarios(expectedTags, requirement);
        }
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
