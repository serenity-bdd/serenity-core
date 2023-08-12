package net.thucydides.model.reports.html;

import io.cucumber.tagexpressions.Expression;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.requirements.reports.ScenarioOutcome;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.List;

import static net.thucydides.model.ThucydidesSystemProperty.TAGS;

public class TestOutcomeFilter extends CucumberCompatibleFilter {

    public TestOutcomeFilter(EnvironmentVariables environmentVariables) {
        super(environmentVariables);
    }

    public boolean matches(TestOutcome testOutcome) {
        Expression expectedTags = cucumberTagExpressionUsing(TAGS);

        List<String> tagValues = CucumberTagConverter.toStrings(testOutcome.getTags());
        return expectedTags.evaluate(tagValues);
    }

    public boolean matches(ScenarioOutcome testOutcome) {
        Expression expectedTags = cucumberTagExpressionUsing(TAGS);

        List<String> tagValues = CucumberTagConverter.toStrings(testOutcome.getTags());
        return (expectedTags.evaluate(tagValues) || examplesHaveMatchingTagsFor(testOutcome, expectedTags));
    }

    private boolean examplesHaveMatchingTagsFor(ScenarioOutcome testOutcome, Expression expectedTags) {
        return testOutcome.getExampleTags().values().stream()
                .anyMatch(
                    exampleTags -> expectedTags.evaluate(CucumberTagConverter.toStrings(exampleTags))
                );
    }

}
