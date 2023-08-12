package net.thucydides.core.model;


import net.serenitybdd.annotations.Feature;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.features.ApplicationFeature;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SimpleTestCase {}

public class WhenGroupingUserStoriesByFeature {

    @Feature
    class WidgetFeature {
         class PurchaseNewWidget{}

        class SearchWidgets{}

        class DisplayWidgets{}
    }

    @Feature
    class GizmoFeature {
         class PurchaseNewGizmo{}

        class SearchGizmos{}

        class DisplayGizmos{}
    }

    class MyApp {
         class PurchaseNewWidget{}

        class SearchWidgets{}

        class DisplayWidgets{}
    }

    @net.serenitybdd.annotations.Story(WidgetFeature.PurchaseNewWidget.class)
    class WhenUserPurchasesNewWidgetsTestCase {
        public void shouldDoThis(){}
        public void shouldDoThat(){}
    }

    @net.serenitybdd.annotations.Story(WidgetFeature.PurchaseNewWidget.class)
    class WhenUserPurchasesLotsOfNewWidgetsTestCase {
        public void shouldDoSomethingElse(){}
    }

    @Test
    public void a_user_story_can_belong_to_a_feature() {
        Class<?> userStoryClass = WidgetFeature.PurchaseNewWidget.class;

        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(userStoryClass);
        ApplicationFeature feature = story.getFeature();
        assertThat(feature.getId(), is(WidgetFeature.class.getCanonicalName()));
    }

    @Test
    public void a_user_story_does_not_have_to_belong_to_a_feature() {
        Class<?> userStoryClass = SimpleTestCase.class;

        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(userStoryClass);
        ApplicationFeature feature = story.getFeature();
        assertThat(feature, is(nullValue()));
    }

    @Test
    public void a_feature_can_be_defined_by_providing_a_feature_class() {
        ApplicationFeature feature = ApplicationFeature.from(WidgetFeature.class);

        assertThat(feature.getId(), is(WidgetFeature.class.getCanonicalName()));
        assertThat(feature.getName(), is("Widget feature"));
    }

    @Test
    public void a_feature_can_also_be_defined_by_providing_the_class_path_and_name() {
        ApplicationFeature feature = new ApplicationFeature(WidgetFeature.class.getCanonicalName(), "Widget feature");

        assertThat(feature.getId(), is(WidgetFeature.class.getCanonicalName()));
        assertThat(feature.getName(), is("Widget feature"));
    }

    @Test
    public void the_feature_name_is_by_default_the_human_readable_form_of_the_feature_class() {
        ApplicationFeature feature = ApplicationFeature.from(WidgetFeature.class);
        assertThat(feature.getName(), is("Widget feature"));
    }

    @Test
    public void the_feature_name_can_be_ovverriden() {
        ApplicationFeature feature = new ApplicationFeature(WidgetFeature.class.getCanonicalName(), "My special widget feature");
        assertThat(feature.getName(), is("My special widget feature"));
    }

    @Test
    public void a_user_story_can_be_nested_in_a_class_that_is_not_a_feature() {
        Class<?> userStoryClass = MyApp.PurchaseNewWidget.class;

        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(userStoryClass);
        assertThat(story.getFeature(), is(nullValue()));
//        Class<?> featureClass = story.getFeatureClass();
//        assertThat(featureClass, is(nullValue()));
    }

    @Test
    public void a_user_story_can_return_the_name_of_its_feature() {
        Class<?> userStoryClass = WidgetFeature.PurchaseNewWidget.class;

        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(userStoryClass);

        assertThat(story.getFeature().getName(), is("Widget feature"));
    }

    @Test
    public void a_feature_is_equal_to_itself() {
        ApplicationFeature feature = ApplicationFeature.from(WidgetFeature.class);
        assertThat(feature.equals(feature), is(true));
    }

    class SubvertedFeature extends ApplicationFeature {
        protected SubvertedFeature(final Class<?> featureClass) {
            super(featureClass);
        }
    }

    @Test
    public void a_feature_can_be_equal_to_another_subclassed_feature_instance() {
        ApplicationFeature feature = ApplicationFeature.from(WidgetFeature.class);
        SubvertedFeature subvertedFeature = new SubvertedFeature(WidgetFeature.class);
        assertThat(feature.equals(subvertedFeature), is(true));
    }

    @Test
    public void a_feature_can_only_be_equal_to_another_feature_instance() {
        ApplicationFeature feature = ApplicationFeature.from(WidgetFeature.class);
        Object notAFeature = new Object();
        assertThat(feature.equals(notAFeature), is(false));
    }

    @Test
    public void features_referring_to_the_same_feature_class_are_identical() {
        ApplicationFeature feature1 = ApplicationFeature.from(WidgetFeature.class);
        ApplicationFeature feature2 = ApplicationFeature.from(WidgetFeature.class);

        assertThat(feature1, is(feature2));
        assertThat(feature1.hashCode(), is(feature2.hashCode()));
    }

    @Test
    public void features_referring_to_different_feature_classes_are_different() {
        ApplicationFeature feature1 = ApplicationFeature.from(WidgetFeature.class);
        ApplicationFeature feature2 = ApplicationFeature.from(GizmoFeature.class);

        assertThat(feature1, is(not(feature2)));
        assertThat(feature1.hashCode(), is(not(feature2.hashCode())));
    }

    @Test
    public void features_referring_to_the_same_feature_id_and_name_are_identical() {
        ApplicationFeature feature1 = new ApplicationFeature("id","name");
        ApplicationFeature feature2 = new ApplicationFeature("id","name");

        assertThat(feature1, is(feature2));
        assertThat(feature1.hashCode(), is(feature2.hashCode()));
    }

    @Test
    public void features_referring_to_different_feature_id_are_different() {
        ApplicationFeature feature1 = new ApplicationFeature("id","name");
        ApplicationFeature feature2 = new ApplicationFeature("id2","name");

        assertThat(feature1, is(not(feature2)));
        assertThat(feature1.hashCode(), is(not(feature2.hashCode())));
    }

    @Test
    public void features_referring_to_different_feature_names_are_different() {
        ApplicationFeature feature1 = new ApplicationFeature("id","name");
        ApplicationFeature feature2 = new ApplicationFeature("id","name2");

        assertThat(feature1, is(not(feature2)));
        assertThat(feature1.hashCode(), is(not(feature2.hashCode())));
    }

    @Test
    public void a_user_story_can_return_the_corresponding_feature_class() {
        Class<?> userStoryClass = WidgetFeature.PurchaseNewWidget.class;

        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(userStoryClass);
        ApplicationFeature feature = ApplicationFeature.from(WidgetFeature.class);

        assertThat(story.getFeature(), is(feature));
    }

    @Test
    public void a_user_story_can_return_the_corresponding_feature_class_using_id_and_name() {
        net.thucydides.model.domain.Story story = Story.withId("story.class","AStory", "feature.class","AFeature");

        ApplicationFeature feature = new ApplicationFeature("feature.class","AFeature");

        assertThat(story.getFeature(), is(feature));
    }
}
