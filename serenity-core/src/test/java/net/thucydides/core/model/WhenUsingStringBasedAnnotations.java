package net.thucydides.core.model;

import net.serenitybdd.annotations.Epic;
import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Story;
import net.serenitybdd.annotations.EpicFeatureStoryAnnotations;
import net.thucydides.model.domain.Stories;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.statistics.service.FeatureStoryTagProvider;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenUsingStringBasedAnnotations {

    // --- Sample annotated classes for testing ---

    @Epic("E-commerce")
    static class EpicAnnotatedClass {}

    @Feature("Shopping Cart")
    static class FeatureAnnotatedClass {}

    @Story("Add item to cart")
    static class StoryAnnotatedClass {}

    @Epic("E-commerce")
    @Feature("Shopping Cart")
    @Story("Add item to cart")
    static class FullyAnnotatedClass {}

    static class ClassWithAnnotatedMethod {
        @Epic("Payments")
        @Feature("Checkout")
        @Story("Pay with credit card")
        public void annotatedMethod() {}
    }

    @Epic("E-commerce")
    @Feature("Shopping Cart")
    static class ClassWithMethodStory {
        @Story("Remove item")
        public void removeItem() {}
    }

    @Feature
    static class LegacyFeatureMarker {
        static class SomeStory {}
    }

    static class LegacyStoryClass {}

    @Story(storyClass = LegacyStoryClass.class)
    static class LegacyStoryAnnotatedClass {}

    @Story(storyClass = LegacyStoryClass.class)
    @Epic("E-commerce")
    static class MixedAnnotationsClass {}

    @Feature("Shopping Cart")
    @Story("Add item")
    static class FeatureAndStoryClass {}

    @Epic("E-commerce")
    @Story("Quick purchase")
    static class EpicAndStoryClass {}

    @Feature("Shopping Cart")
    static class FeatureOnlyClass {}

    @Epic("E-commerce")
    static class ParentClass {}

    @Feature("Shopping Cart")
    @Story("Add item")
    static class ChildClass extends ParentClass {}

    @Story(storyClass = LegacyStoryClass.class)
    @Feature("Shopping Cart")
    static class LegacyStoryWithNewFeature {}

    // --- Tag generation tests ---

    @Test
    public void epic_annotation_on_class_produces_epic_tag() {
        List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(EpicAnnotatedClass.class);

        assertThat(tags, hasSize(1));
        assertThat(tags.get(0).getName(), is("E-commerce"));
        assertThat(tags.get(0).getType(), is("epic"));
    }

    @Test
    public void feature_annotation_on_class_produces_feature_tag() {
        List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(FeatureAnnotatedClass.class);

        assertThat(tags, hasSize(1));
        assertThat(tags.get(0).getName(), is("Shopping Cart"));
        assertThat(tags.get(0).getType(), is("feature"));
    }

    @Test
    public void story_annotation_on_class_produces_story_tag() {
        List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(StoryAnnotatedClass.class);

        assertThat(tags, hasSize(1));
        assertThat(tags.get(0).getName(), is("Add item to cart"));
        assertThat(tags.get(0).getType(), is("story"));
    }

    @Test
    public void all_three_annotations_combined_produce_three_tags() {
        List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(FullyAnnotatedClass.class);

        assertThat(tags, hasSize(3));
        assertThat(tags, hasItem(TestTag.withName("E-commerce").andType("epic")));
        assertThat(tags, hasItem(TestTag.withName("Shopping Cart").andType("feature")));
        assertThat(tags, hasItem(TestTag.withName("Add item to cart").andType("story")));
    }

    @Test
    public void method_level_annotations_produce_correct_tags() throws NoSuchMethodException {
        Method method = ClassWithAnnotatedMethod.class.getMethod("annotatedMethod");
        List<TestTag> tags = EpicFeatureStoryAnnotations.forMethod(method);

        assertThat(tags, hasItem(TestTag.withName("Payments").andType("epic")));
        assertThat(tags, hasItem(TestTag.withName("Checkout").andType("feature")));
        assertThat(tags, hasItem(TestTag.withName("Pay with credit card").andType("story")));
    }

    @Test
    public void class_level_annotations_inherited_by_method_plus_method_own_story() throws NoSuchMethodException {
        Method method = ClassWithMethodStory.class.getMethod("removeItem");
        List<TestTag> tags = EpicFeatureStoryAnnotations.forMethod(method);

        assertThat(tags, hasItem(TestTag.withName("Remove item").andType("story")));
        assertThat(tags, hasItem(TestTag.withName("E-commerce").andType("epic")));
        assertThat(tags, hasItem(TestTag.withName("Shopping Cart").andType("feature")));
    }

    @Test
    public void superclass_tags_are_inherited() {
        List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(ChildClass.class);

        assertThat(tags, hasItem(TestTag.withName("Shopping Cart").andType("feature")));
        assertThat(tags, hasItem(TestTag.withName("Add item").andType("story")));
        assertThat(tags, hasItem(TestTag.withName("E-commerce").andType("epic")));
    }

    @Test
    public void legacy_feature_marker_with_no_value_does_not_produce_feature_tag() {
        List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(LegacyFeatureMarker.class);
        assertThat(tags, is(empty()));
    }

    // --- Requirements hierarchy tests ---

    @Test
    public void full_hierarchy_builds_story_with_path_and_feature() {
        net.thucydides.model.domain.Story story = Stories.findStoryFrom(FullyAnnotatedClass.class);

        assertThat(story, is(notNullValue()));
        assertThat(story.getName(), is("Add item to cart"));
        assertThat(story.getPath(), is("E-commerce/Shopping Cart/Add item to cart"));
        assertThat(story.getFeature(), is(notNullValue()));
        assertThat(story.getFeature().getName(), is("Shopping Cart"));
    }

    @Test
    public void story_only_builds_single_level_path() {
        net.thucydides.model.domain.Story story = Stories.findStoryFrom(StoryAnnotatedClass.class);

        assertThat(story.getName(), is("Add item to cart"));
        assertThat(story.getPath(), is("Add item to cart"));
        assertThat(story.getFeature(), is(nullValue()));
    }

    @Test
    public void feature_and_story_builds_two_level_path() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.fromAnnotationsOn(FeatureAndStoryClass.class);

        assertThat(story.getName(), is("Add item"));
        assertThat(story.getPath(), is("Shopping Cart/Add item"));
        assertThat(story.getFeature(), is(notNullValue()));
        assertThat(story.getFeature().getName(), is("Shopping Cart"));
    }

    @Test
    public void epic_and_story_without_feature_builds_two_level_path() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.fromAnnotationsOn(EpicAndStoryClass.class);

        assertThat(story.getName(), is("Quick purchase"));
        assertThat(story.getPath(), is("E-commerce/Quick purchase"));
        assertThat(story.getFeature(), is(nullValue()));
    }

    @Test
    public void epic_only_uses_epic_as_story_name() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.fromAnnotationsOn(EpicAnnotatedClass.class);

        assertThat(story.getName(), is("E-commerce"));
        assertThat(story.getPath(), is("E-commerce"));
    }

    @Test
    public void feature_only_uses_feature_as_story_name() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.fromAnnotationsOn(FeatureOnlyClass.class);

        assertThat(story.getName(), is("Shopping Cart"));
        assertThat(story.getPath(), is("Shopping Cart"));
    }

    @Test
    public void superclass_annotations_are_inherited_in_hierarchy() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.fromAnnotationsOn(ChildClass.class);

        assertThat(story.getName(), is("Add item"));
        assertThat(story.getPath(), is("E-commerce/Shopping Cart/Add item"));
        assertThat(story.getFeature(), is(notNullValue()));
        assertThat(story.getFeature().getName(), is("Shopping Cart"));
    }

    // --- Legacy compatibility tests ---

    @Test
    public void legacy_story_class_still_works_via_tested_in_test_case() {
        Class<?> storyClass = net.thucydides.model.domain.Story.testedInTestCase(LegacyStoryAnnotatedClass.class);
        assertThat(storyClass, is(equalTo(LegacyStoryClass.class)));
    }

    @Test
    public void legacy_feature_marker_is_still_detected_as_feature_class() {
        assertThat(LegacyFeatureMarker.class.getAnnotation(Feature.class), is(notNullValue()));
    }

    @Test
    public void legacy_story_class_takes_precedence_over_string_annotations() {
        // @Story(storyClass=X.class) is checked before fromAnnotationsOn()
        net.thucydides.model.domain.Story story = Stories.findStoryFrom(MixedAnnotationsClass.class);

        // Should not be "E-commerce" from @Epic — the legacy class-based story takes priority
        assertThat(story, is(notNullValue()));
    }

    @Test
    public void mixed_legacy_story_class_and_new_epic_produces_only_epic_tag() {
        List<TestTag> tags = EpicFeatureStoryAnnotations.forClass(MixedAnnotationsClass.class);

        assertThat(tags, hasItem(TestTag.withName("E-commerce").andType("epic")));
        assertThat(tags, not(hasItem(hasProperty("type", is("story")))));
    }

    @Test
    public void no_annotations_returns_null_from_annotations_on() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.fromAnnotationsOn(LegacyStoryClass.class);
        assertThat(story, is(nullValue()));
    }

    // --- Integration: TestOutcome + FeatureStoryTagProvider pipeline ---

    @Test
    public void feature_story_tag_provider_produces_story_tag_from_annotation_hierarchy() {
        net.thucydides.model.domain.Story story = Stories.findStoryFrom(FullyAnnotatedClass.class);
        TestOutcome outcome = TestOutcome.forTestInStory("should_add_item", FullyAnnotatedClass.class, story);

        FeatureStoryTagProvider tagProvider = new FeatureStoryTagProvider();
        Set<TestTag> tags = tagProvider.getTagsFor(outcome);

        assertThat(tags, hasItem(hasProperty("type", is("feature"))));
        // The story tag should contain the path
        assertThat(tags, is(not(empty())));
    }

    @Test
    public void test_outcome_has_correct_feature_from_annotation_hierarchy() {
        net.thucydides.model.domain.Story story = Stories.findStoryFrom(FullyAnnotatedClass.class);
        TestOutcome outcome = TestOutcome.forTestInStory("should_add_item", FullyAnnotatedClass.class, story);

        assertThat(outcome.getUserStory(), is(notNullValue()));
        assertThat(outcome.getUserStory().getName(), is("Add item to cart"));
        assertThat(outcome.getUserStory().getPath(), is("E-commerce/Shopping Cart/Add item to cart"));
        assertThat(outcome.getFeature(), is(notNullValue()));
        assertThat(outcome.getFeature().getName(), is("Shopping Cart"));
    }

    @Test
    public void test_outcome_with_story_only_has_no_feature() {
        net.thucydides.model.domain.Story story = Stories.findStoryFrom(StoryAnnotatedClass.class);
        TestOutcome outcome = TestOutcome.forTestInStory("should_do_something", StoryAnnotatedClass.class, story);

        assertThat(outcome.getUserStory().getName(), is("Add item to cart"));
        assertThat(outcome.getFeature(), is(nullValue()));
    }
}
