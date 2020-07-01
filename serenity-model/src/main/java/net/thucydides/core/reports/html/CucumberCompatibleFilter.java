package net.thucydides.core.reports.html;

import io.cucumber.tagexpressions.Expression;
import io.cucumber.tagexpressions.TagExpressionParser;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.TAGS;

public class CucumberCompatibleFilter {
    protected final EnvironmentVariables environmentVariables;

    public CucumberCompatibleFilter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    protected Expression cucumberTagExpressionUsing(ThucydidesSystemProperty tagProperty) {
        String tagExpression = cucumberTagOptions()
                .orElse(tagProperty.optionalFrom(environmentVariables)
                        .orElse(""))
                .replace("@", "")
                .replace("=", ":")
                .toLowerCase();
        return TagExpressionParser.parse(tagExpression);
    }

    protected Optional<String> cucumberTagOptions() {
        String cucumberFilterTags = environmentVariables.getProperty("cucumber.filter.tags");
        if (StringUtils.isNotEmpty(cucumberFilterTags)) {
            return Optional.of(StringUtils.strip(cucumberFilterTags, "'"));
        }
        String cucumberOptions = environmentVariables.getProperty("cucumber.options");
        if (StringUtils.isNotEmpty(cucumberOptions) && (cucumberOptions.contains("--tags "))) {
            return getTagsFromCucumberOptions(cucumberOptions);
        }
        return Optional.empty();
    }

    private Optional<String> getTagsFromCucumberOptions(String cucumberOptions) {
        int tagsFlag = cucumberOptions.indexOf("--tags ");
        int tagsOptionStart = tagsFlag + 7;
        int nextTagOptionStart = cucumberOptions.indexOf("--", tagsOptionStart);
        String tagOption = (nextTagOptionStart > 0) ?
                cucumberOptions.substring(tagsOptionStart, nextTagOptionStart) : cucumberOptions.substring(tagsOptionStart);

        String tagOptionsWithoutAtSigns = StringUtils.strip(tagOption, "'");
        return Optional.of(tagOptionsWithoutAtSigns);
    }

    public boolean matchesTags(List<TestTag> testTags) {
        Expression expectedTags = cucumberTagExpressionUsing(TAGS);

        List<String> tagValues = CucumberTagConverter.toStrings(testTags);
        return expectedTags.evaluate(tagValues);
    }

    public boolean matches(List<String> testTags) {
        Expression expectedTags = cucumberTagExpressionUsing(TAGS);
        return expectedTags.evaluate(testTags);
    }

}
