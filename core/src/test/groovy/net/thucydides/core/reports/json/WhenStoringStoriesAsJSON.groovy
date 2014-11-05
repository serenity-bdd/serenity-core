package net.thucydides.core.reports.json

import net.thucydides.core.annotations.Feature
import net.thucydides.core.reports.json.jackson.JacksonJSONConverter
import net.thucydides.core.util.MockEnvironmentVariables
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Specification

class WhenStoringStoriesAsJSON extends Specification {


    def environmentVars = new MockEnvironmentVariables();

    class AUserStory {
    }

    @Feature
    class AFeature {
        class AUserStoryInAFeature {
        }
    }

    def "should generate an JSON report for a story"() {
        given:
        def story = new net.thucydides.core.model.Story(AUserStory.class)

        when:
        StringWriter writer = new StringWriter();
        def converter = new JacksonJSONConverter(environmentVars)
        converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, story);

        then:
        def expectedJson = """
{
    "id" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON.AUserStory",
    "storyName" : "A user story",
    "storyClassName" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON\$AUserStory",
    "path" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON"
}
"""
        def serializedStory = writer.toString()
        JSONCompare.compareJSON(expectedJson, serializedStory, JSONCompareMode.LENIENT).passed();
    }

    def "should read a story from a simple JSON string"() {
        given:
        def serializedStory = """
{
    "id" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON.AUserStory",
    "storyName" : "A user story",
    "storyClassName" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON\$AUserStory",
    "path" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON"
}
"""
        def reader = new StringReader(serializedStory)

        when:
        def converter = new JacksonJSONConverter(environmentVars)
        def story = converter.mapper.readValue(reader, net.thucydides.core.model.Story)

        then:
        story == new net.thucydides.core.model.Story(AUserStory.class)
    }


    def "should generate an JSON report for a story with a feature"() {
        given:
        def story = new net.thucydides.core.model.Story(AFeature.AUserStoryInAFeature.class)

        when:
        StringWriter writer = new StringWriter();
        def converter = new JacksonJSONConverter(environmentVars)
        converter.mapper.writerWithDefaultPrettyPrinter().writeValue(writer, story);

        then:
        def expectedJson = """
{
    "id" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON.AFeature.AUserStoryInAFeature",
    "storyName" : "A user story in a feature",
    "storyClassName" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON\$AFeature\$AUserStoryInAFeature",
    "path" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON.AFeature",
     "feature" : {
        "id" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON.AFeature",
        "name" : "A feature"
  }
}
"""
        def serializedStory = writer.toString()
        JSONCompare.compareJSON(expectedJson, serializedStory, JSONCompareMode.LENIENT).passed();
    }


    def "should read a story with a feature"() {
        given:
        def serializedStory = """
{
    "id" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON.AFeature.AUserStoryInAFeature",
    "storyName" : "A user story in a feature",
    "storyClassName" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON\$AFeature\$AUserStoryInAFeature",
    "path" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON.AFeature",
     "feature" : {
        "id" : "net.thucydides.core.reports.json.WhenStoringStoriesAsJSON.AFeature",
        "name" : "A feature"
     }
  }
}
"""
        def reader = new StringReader(serializedStory)

        when:
        def converter = new JacksonJSONConverter(environmentVars)
        def story = converter.mapper.readValue(reader, net.thucydides.core.model.Story)

        then:
        story == new net.thucydides.core.model.Story(AFeature.AUserStoryInAFeature.class)
    }

}