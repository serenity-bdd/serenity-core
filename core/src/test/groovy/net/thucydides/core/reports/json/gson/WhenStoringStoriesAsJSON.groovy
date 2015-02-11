package net.thucydides.core.reports.json.gson

import net.thucydides.core.annotations.Feature
import net.thucydides.core.model.Story
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
        def converter = new GsonJSONConverter(environmentVars)

        then:
        def expectedJson = """
{
    "id" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON.AUserStory",
    "storyName" : "A user story",
    "storyClassName" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON\$AUserStory",
    "path" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON"
}
"""
        def serializedStory = converter.gson.toJson(story)
        JSONCompare.compareJSON(expectedJson, serializedStory, JSONCompareMode.LENIENT).passed();
    }

    def "should read a story from a simple JSON string"() {
        given:
        def serializedStory = """
{
    "id" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON.AUserStory",
    "storyName" : "A user story",
    "storyClassName" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON\$AUserStory",
    "path" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON"
}
"""
        def converter = new GsonJSONConverter(environmentVars)

        when:
        def story = converter.gson.fromJson(serializedStory, Story)

        then:
        story == new net.thucydides.core.model.Story(AUserStory.class)
    }


    def "should generate an JSON report for a story with a feature"() {
        given:
        def story = new net.thucydides.core.model.Story(AFeature.AUserStoryInAFeature.class)

        when:
        def converter = new GsonJSONConverter(environmentVars)

        then:
        def expectedJson = """
{
    "id" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON.AFeature.AUserStoryInAFeature",
    "storyName" : "A user story in a feature",
    "storyClassName" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON\$AFeature\$AUserStoryInAFeature",
    "path" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON.AFeature",
     "feature" : {
        "id" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON.AFeature",
        "name" : "A feature"
  }
}
"""
        def serializedStory = converter.gson.toJson(story)
        JSONCompare.compareJSON(expectedJson, serializedStory, JSONCompareMode.LENIENT).passed();
    }


    def "should read a story with a feature"() {
        given:
        def serializedStory = """
{
    "id" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON.AFeature.AUserStoryInAFeature",
    "storyName" : "A user story in a feature",
    "storyClassName" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON\$AFeature\$AUserStoryInAFeature",
    "path" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON.AFeature",
     "feature" : {
        "id" : "net.thucydides.core.reports.json.gson.WhenStoringStoriesAsJSON.AFeature",
        "name" : "A feature"
     }
  }
"""

        when:
        def converter = new GsonJSONConverter(environmentVars)
        def story = converter.gson.fromJson(serializedStory, Story)

        then:
        story == new net.thucydides.core.model.Story(AFeature.AUserStoryInAFeature.class)
    }

}