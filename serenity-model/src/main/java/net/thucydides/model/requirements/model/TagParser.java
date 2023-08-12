package net.thucydides.model.requirements.model;

import io.cucumber.tagexpressions.Expression;
import io.cucumber.tagexpressions.TagExpressionParser;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class TagParser {

    public static Expression parseFromTagFilters(List<String> stringList) {
        String combinedExpression = stringList.isEmpty() ? "" : stringList.stream()
            .filter(StringUtils::isNotEmpty)
            .map(tagExpression -> tagExpression.replace("~", "not "))
            .collect(joining(") and (", "(", ")"));

        return TagExpressionParser.parse(combinedExpression);
    }

    public static Collection<String> additionalTagsSuppliedFrom(EnvironmentVariables environmentVariables, List<String> existingTags) {
        String tagsExpression = ThucydidesSystemProperty.TAGS.from(environmentVariables, "");
        return Stream.of(StringUtils.split(tagsExpression, ","))
            .map(TagParser::toCucumberTag)
            .filter(tag -> !existingTags.contains(tag)).collect(toList());
    }

    private static String toCucumberTag(String from) {
        String tag = from.trim().replaceAll(":", "=");
        if (tag.startsWith("~@") || tag.startsWith("@")) {
            return tag;
        }
        if (tag.startsWith("~")) {
            return "~@" + tag.substring(1);
        }

        return "@" + tag;
    }


}
