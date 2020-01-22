package net.thucydides.core.reports.html;

import io.cucumber.tagexpressions.Expression;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.model.Requirement;
import net.thucydides.core.requirements.reports.ScenarioOutcome;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;
import java.util.stream.Collectors;

import static net.thucydides.core.ThucydidesSystemProperty.TAGS;

public class TestOutcomeFilter extends CucumberCompatibleFilter {

    public TestOutcomeFilter(EnvironmentVariables environmentVariables) {
        super(environmentVariables);
    }

    public boolean matches(TestOutcome testOutcome) {
        Expression expectedTags = cucumberTagExpressionUsing(TAGS);

        List<String> tagValues = tagsAsStrings(testOutcome.getTags());
        return expectedTags.evaluate(tagValues);
    }

    public boolean matches(ScenarioOutcome testOutcome) {
        Expression expectedTags = cucumberTagExpressionUsing(TAGS);

        List<String> tagValues = testOutcome.getTags().stream()
                .map(TestTag::toString)
                .collect(Collectors.toList());
        return expectedTags.evaluate(tagValues);
    }

}
