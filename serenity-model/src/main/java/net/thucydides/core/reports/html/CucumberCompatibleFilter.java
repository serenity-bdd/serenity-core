package net.thucydides.core.reports.html;

import io.cucumber.tagexpressions.Expression;
import io.cucumber.tagexpressions.TagExpressionParser;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.thucydides.core.ThucydidesSystemProperty.TAGS;

public class CucumberCompatibleFilter {
    protected final EnvironmentVariables environmentVariables;

    public CucumberCompatibleFilter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    protected Expression cucumberTagExpressionUsing(ThucydidesSystemProperty tagProperty) {
        String tagExpression = tagProperty.optionalFrom(environmentVariables)
                .orElse(cucumberTagOptions().orElse(""))
                .replace("@","")
                .toLowerCase();
        TagExpressionParser parser = new TagExpressionParser();
        return parser.parse(tagExpression);
    }

    protected Optional<String> cucumberTagOptions() {
        String cucumberOptions = environmentVariables.getProperty("cucumber.options");
        if (StringUtils.isNotEmpty(cucumberOptions) && (cucumberOptions.contains("--tags "))) {
            int tagsFlag = cucumberOptions.indexOf("--tags ");
            int tagsOptionStart = tagsFlag + 7;
            int nextTagOptionStart = cucumberOptions.indexOf("--", tagsOptionStart);
            String tagOption =(nextTagOptionStart > 0) ?
                cucumberOptions.substring(tagsOptionStart, nextTagOptionStart) : cucumberOptions.substring(tagsOptionStart);

            String tagOptionsWithoutAtSigns = StringUtils.strip(tagOption, "'");
            return Optional.of(tagOptionsWithoutAtSigns);
        }
        return Optional.empty();
    }


    protected List<String> tagsAsStrings(Collection<TestTag> tags) {
        return tags.stream()
                .map(TestTag::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public boolean matchesTags(List<TestTag> testTags) {
        Expression expectedTags = cucumberTagExpressionUsing(TAGS);

        List<String> tagValues = tagsAsStrings(testTags);
        return expectedTags.evaluate(tagValues);
    }

    public boolean matches(List<String> testTags) {
        Expression expectedTags = cucumberTagExpressionUsing(TAGS);
        return expectedTags.evaluate(testTags);
    }

}
