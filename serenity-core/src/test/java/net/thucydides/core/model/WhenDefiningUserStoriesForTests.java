package net.thucydides.core.model;


import net.serenitybdd.annotations.Story;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenDefiningUserStoriesForTests {

    class MyUserStories {
         class PurchaseNewWidget{}

        class SearchWidgets{}

        class DisplayWidgets{}
    }

    @Story(MyUserStories.PurchaseNewWidget.class)
    class WhenUserPurchasesNewWidgets {
        public void shouldDoThis(){}
        public void shouldDoThat(){}
    }

    @Story(MyUserStories.PurchaseNewWidget.class)
    class WhenUserPurchasesLotsOfNewWidgets {

    }

    class WhenDoingSomethingElse {

    }

    @Test
    public void a_test_case_should_be_validating_a_user_story() {
        Class<?> userStoryClass = net.thucydides.model.domain.Story.testedInTestCase(WhenUserPurchasesNewWidgets.class);
        assertThat(userStoryClass.getName(), is(MyUserStories.PurchaseNewWidget.class.getName()));
    }

    @Test
    public void but_not_all_test_cases_have_a_user_story() {
        Class<?> userStoryClass = net.thucydides.model.domain.Story.testedInTestCase(WhenDoingSomethingElse.class);
        assertThat(userStoryClass, is(nullValue()));
    }

    @Test
    public void several_test_cases_can_test_the_same_user_story() {
        Class<?> userStoryClass = net.thucydides.model.domain.Story.testedInTestCase(WhenUserPurchasesNewWidgets.class);
        Class<?> userStoryClass2 = net.thucydides.model.domain.Story.testedInTestCase(WhenUserPurchasesLotsOfNewWidgets.class);
        assertThat(userStoryClass.getName(), is(userStoryClass2.getName()));
    }

    @Test
    public void a_user_story_is_identified_by_the_story_class() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(MyUserStories.PurchaseNewWidget.class);
        assertThat(story.getId(), is(MyUserStories.PurchaseNewWidget.class.getCanonicalName()));
    }

    @Test
    public void a_user_story_name_is_derived_from_the_story_class() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(MyUserStories.PurchaseNewWidget.class);
        assertThat(story.getName(), is("PurchaseNewWidget"));
    }

    @Test
    public void a_user_story_can_be_created_using_a_class_name_that_no_longer_exists() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.withId("net.thucydides.core.model.ThisClassNoLongerExists", "This class no longer exists");

        assertThat(story.getId(), is("net.thucydides.core.model.ThisClassNoLongerExists"));
    }

    @Test
    public void a_user_story_created_using_a_class_name_that_no_longer_exists_retains_the_story_name() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.withId("net.thucydides.core.model.ThisClassNoLongerExists", "This class no longer exists");

        assertThat(story.getName(), is("This class no longer exists"));
    }

    @Test
    public void stories_with_the_same_story_class_are_identical() {
        net.thucydides.model.domain.Story story1 = net.thucydides.model.domain.Story.from(MyUserStories.PurchaseNewWidget.class);
        net.thucydides.model.domain.Story story2 = net.thucydides.model.domain.Story.from(MyUserStories.PurchaseNewWidget.class);
        assertThat(story1, is(story2));
    }

    @Test
    public void stories_with_different_story_classes_are_not_identical() {
        net.thucydides.model.domain.Story story1 = net.thucydides.model.domain.Story.from(MyUserStories.DisplayWidgets.class);
        net.thucydides.model.domain.Story story2 = net.thucydides.model.domain.Story.from(MyUserStories.PurchaseNewWidget.class);
        assertThat(story1, is(not(story2)));
    }


    @Test
    public void a_story_should_know_what_feature_it_belongs_to() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(MyUserStories.PurchaseNewWidget.class);

    }

    @Test
    public void a_story_can_be_defined_using_a_name_and_a_path() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.withIdAndPath("storyId","story name","a.b.c");
        assertThat(story.getPath(), is("a.b.c"));
    }

    @Test
    public void a_story_path_for_a_class_is_derived_from_the_package() {
        net.thucydides.model.domain.Story story = net.thucydides.model.domain.Story.from(MyUserStories.PurchaseNewWidget.class);
        assertThat(story.getPath(), is("net/thucydides/core/model/WhenDefiningUserStoriesForTests/MyUserStories/PurchaseNewWidget"));

    }

}
