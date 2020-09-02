package net.thucydides.core.reports.html;



import io.cucumber.messages.Messages.GherkinDocument.Feature.Tag;
import net.thucydides.core.model.TestTag;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CucumberTagConverter {

    public static List<String> toStrings(Collection<TestTag> tags) {
        List<String> tagsAsStrings = tags.stream()
                .map(TestTag::toString)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> singleTermTags = tags.stream()
                .filter(tag -> tag.getType().equalsIgnoreCase("tag"))
                .map(TestTag::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        tagsAsStrings.addAll(singleTermTags);

        return tagsAsStrings;
    }

    public static List<String> fromGherkinTags(List<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .map(String::toLowerCase)
                .map(tag -> tag.replace("@",""))
                .collect(Collectors.toList());
    }

    public static Collection<TestTag> toSerenityTags(List<Tag> tags) {
        return tags.stream()
                .map(tag -> TestTag.withValue(tag.getName()))
                .collect(Collectors.toSet());
    }
}
