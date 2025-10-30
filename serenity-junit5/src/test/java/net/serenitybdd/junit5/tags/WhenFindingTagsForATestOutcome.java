package net.serenitybdd.junit5.tags;

import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.statistics.service.*;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

public class WhenFindingTagsForATestOutcome {

    @Mock
    TestOutcome emptyTestOutcome;

    MockEnvironmentVariables environmentVariables;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
    }

    @Test
    public void should_find_the_annotation_tag_provider_by_default() {
        TagProviderService tagProviderService = new ClasspathTagProviderService();
        List<TagProvider> tagProviders = tagProviderService.getTagProviders();

        boolean containsAnnotationTagProvider = false;
        for(TagProvider provider : tagProviders) {
            if (provider instanceof AnnotationBasedTagProvider) {
                containsAnnotationTagProvider = true;
                break;
            }
        }
        assertThat(containsAnnotationTagProvider, is(true));
    }

    @Test
    public void junit_tag_provider_strategy_should_find_the_annotation_tag_provider_by_default() {

        TagProviderStrategy tagProviderStrategy = new JUnitTagProviderStrategy();
        Iterable<? extends TagProvider> tagProviders = tagProviderStrategy.getTagProviders();

        boolean containsAnnotationTagProvider = false;
        for(TagProvider provider : tagProviders) {
            if (provider instanceof AnnotationBasedTagProvider) {
                containsAnnotationTagProvider = true;
                break;
            }
        }
        assertThat(containsAnnotationTagProvider, is(true));
    }


    @Test
    public void annotation_based_tag_should_return_no_tags_if_the_test_class_is_not_defined() {
        when(emptyTestOutcome.getTestCase()).thenReturn(null);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        assertThat(tagProvider.getTagsFor(emptyTestOutcome).size(), is(0));
    }

    class SomeUnannotatedTestCase {
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tag_should_return_no_annotated_tags_if_no_tags_present_in_the_test_class() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeUnannotatedTestCase.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(0));
    }

    @WithTag(name="Car sales", type="pillar")
    class SomeTestCase {
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tag_should_return_annotated_tags_if_tags_present_in_the_test_class() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCase.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(not(0)));
        TestTag tag = tags.iterator().next();

        assertThat(tag.getName(), is("Car sales"));
        assertThat(tag.getType(), is("pillar"));
    }

    class SomeTestCaseWithTagOnMethod {
        @WithTag(name="Car sales", type="pillar")
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tag_should_return_annotated_tags_if_tags_present_on_the_test_method() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagOnMethod.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(not(0)));
        TestTag tag = tags.iterator().next();

        assertThat(tag.getName(), is("Car sales"));
        assertThat(tag.getType(), is("pillar"));
    }

    @WithTag(name="More Car sales", type="pillar")
    class SomeTestCaseWithTagOnMethodAndClass {
        @WithTag(name="Car sales", type="pillar")
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tag_should_return_annotated_tags_from_the_class_and_the_method() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagOnMethodAndClass.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(2));
    }

    @Test
    public void injected_tags_should_be_fetched_from_environment_variables() {
        // GIVEN
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setProperty("injected.tags","color:red,flavor:strawberry");

        InjectedTagProvider injectedTagProvider = new InjectedTagProvider(environmentVariables);
        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagOnMethodAndClass.class);

        // WHEN
        Set<TestTag> tags = injectedTagProvider.getTagsFor(testOutcome);

        // THEN
        assertThat(tags, hasItem(TestTag.withName("red").andType("color")));
        assertThat(tags, hasItem(TestTag.withName("strawberry").andType("flavor")));

    }

    @Test
    public void injected_tags_should_be_empty_if_none_are_defined_in_the_environment_variables() {
        // GIVEN
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

        InjectedTagProvider injectedTagProvider = new InjectedTagProvider(environmentVariables);
        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagOnMethodAndClass.class);

        // WHEN
        Set<TestTag> tags = injectedTagProvider.getTagsFor(testOutcome);

        // THEN
        assertThat(tags.size(), is(0));

    }

    class SomeTestCaseWithAShortenedTagOnAMethod {
        @Tag("pillar:Car sales")
        public void some_test_method() {}
    }

    @Test
    public void tags_can_use_a_shorthand_notation() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithAShortenedTagOnAMethod.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestTag> tags = tagProvider.getTagsFor(testOutcome);
        TestTag tag = (TestTag) tags.toArray()[0];
        assertThat(tag.getName(), is("Car sales"));
        assertThat(tag.getType(), is("pillar"));
    }

    class SomeTestCaseWithSeveralShortenedaTagOnAMethod {
        //@WithTagValuesOf({"pillar: car sales", "A tag"})
        @Tags({
            @Tag("pillar: car sales"),
            @Tag("A tag")
        })
        public void some_test_method() {}
    }

    @Test
    public void multiple_tags_can_use_a_shorthand_notation() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithSeveralShortenedaTagOnAMethod.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags, hasItem(TestTag.withName("A tag").andType("tag")));
        assertThat(tags, hasItem(TestTag.withName("car sales").andType("pillar")));
    }


    @WithTags(
            {
                    @WithTag(name="Car sales", type="pillar"),
                    @WithTag(name="Boat sales", type="pillar")
            }
    )
    class SomeTestCaseWithTagsOnClass {
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tags_should_return_multiple_annotated_tags_if_tags_present_on_the_test_class() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagsOnClass.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(2));
    }

    class SomeTestCaseWithTagsOnMethod {
        @WithTags(
                {
                        @WithTag(name="Car sales", type="pillar"),
                        @WithTag(name="Boat sales", type="pillar")
                }
        )
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tags_should_return_multiple_annotated_tags_if_tags_present_on_the_test_method() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagsOnMethod.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(2));
    }

    @WithTag(name="Online sales", type="capability")
    class SomeTestCaseWithTagsOnMethodAndClass {
        @WithTags(
                {
                        @WithTag(name="Car sales", type="pillar"),
                        @WithTag(name="Boat sales", type="pillar")
                }
        )
        public void some_test_method() {}
    }

    @Test
    public void annotation_based_tags_should_return_multiple_annotated_tags_if_tags_present_on_the_test_method_and_class() {

        TestOutcome testOutcome = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagsOnMethodAndClass.class);

        AnnotationBasedTagProvider tagProvider = new AnnotationBasedTagProvider();

        Set<TestTag> tags = tagProvider.getTagsFor(testOutcome);
        assertThat(tags.size(), is(3));
    }

}

