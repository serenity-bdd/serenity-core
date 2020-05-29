package net.thucydides.core.reports.html;

import io.cucumber.tagexpressions.Expression;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.requirements.reports.ScenarioOutcome;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.List;

import static net.thucydides.core.ThucydidesSystemProperty.TAGS;

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
