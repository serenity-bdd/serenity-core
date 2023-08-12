package net.thucydides.core.model;

import net.serenitybdd.annotations.Feature;
import net.thucydides.model.domain.Story;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class WhenAggregatingStoryResults {

    @Feature
    class WidgetFeature {
         class PurchaseNewWidget{}

        class SearchWidgets{}

        class DisplayWidgets{}
    }

    @Test
    public void a_story_can_have_an_associated_feature_class() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        assertThat(story.getFeature().getId(), is(WidgetFeature.class.getCanonicalName()));
    }

    @Test
    public void a_story_can_have_an_associated_feature() {
        Story story = Story.withId("storyId","storyName","featureId","featureName");
        assertThat(story.getFeature(), is(notNullValue()));
    }

    @Test
    public void a_story_does_not_have_to_have_an_associated_feature() {
        Story story = Story.withId("storyId","storyName");
        assertThat(story.getFeature(), is(nullValue()));
    }

    @Test
    public void a_story_can_be_defined_by_a_name() {
        Story story = Story.called("storyName");
        assertThat(story.getName(), is("storyName"));
        assertThat(story.getId(), is("storyName"));
    }
}
