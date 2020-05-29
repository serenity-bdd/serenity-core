package net.thucydides.core.reports;

import net.serenitybdd.core.collect.NewSet;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static net.thucydides.core.reports.matchers.TestOutcomeMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class WhenMatchingTags {

    @Mock
    TestOutcome testOutcome;

    TestTag storyTag = TestTag.withName("a story").andType("story");

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_match_test_outcome_with_tag_name() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        assertThat(havingTagName("a story").matches(testOutcome), is(true));
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Test
    public void should_describe_missing_tag_name_if_not_found() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        try {
            assertThat(testOutcome, havingTagName("unknown tag"));
        } catch (AssertionError error) {
            assertThat(error.getMessage(), containsString("a test outcome with a tag \"unknown tag\""));
            return;
        }
        fail();
    }

    @Test
    public void should_describe_missing_tag_type_if_not_found() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        try {
            assertThat(testOutcome, havingTagType("unknown tag type"));
        } catch (AssertionError error) {
            assertThat(error.getMessage(), containsString("a test outcome with a tag of type \"unknown tag type\""));
            return;
        }
        fail();
    }

    @Test
    public void should_not_match_test_outcome_if_tag_name_is_not_present() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        assertThat(havingTagName("a feature").matches(testOutcome), is(false));
    }

    @Test
    public void should_match_test_outcome_with_tag_type() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        assertThat(havingTagType("story").matches(testOutcome), is(true));
    }

    @Test
    public void should_not_match_test_outcome_if_tag_type_name_is_not_present() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        assertThat(havingTagType("feature").matches(testOutcome), is(false));
    }

    @Test
    public void should_match_test_outcome_with_tag() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        assertThat(havingTag(TestTag.withName("a story").andType("story")).matches(testOutcome), is(true));
    }

    @Test
    public void should_not_match_test_outcome_with_tag_if_name_is_different() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        assertThat(havingTag(TestTag.withName("another story").andType("story")).matches(testOutcome), is(false));
    }

    @Test
    public void should_not_match_test_outcome_with_tag_if_type_is_different() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        assertThat(havingTag(TestTag.withName("a story").andType("feature")).matches(testOutcome), is(false));
    }

    @Test
    public void should_describe_expected_tag_if_not_found() {
        when(testOutcome.getTags()).thenReturn(NewSet.of(storyTag));

        try {
            assertThat(testOutcome, havingTag(TestTag.withName("a different story").andType("feature")));
        } catch (AssertionError error) {
            assertThat(error.getMessage(), containsString("Expected: a test outcome with a tag <feature:a different story>"));
            return;
        }
        fail();
    }


    @Test
    public void should_match_test_outcome_with_an_expected_result() {
        when(testOutcome.getResult()).thenReturn(TestResult.SUCCESS);

        assertThat(withResult(TestResult.SUCCESS).matches(testOutcome), is(true));
    }

    @Test
    public void should_not_match_test_outcome_with_an_unexpected_result() {
        when(testOutcome.getResult()).thenReturn(TestResult.SUCCESS);

        assertThat(withResult(TestResult.FAILURE).matches(testOutcome), is(false));
    }

    @Test
    public void should_define_tags_using_a_shorthand_notation() {
        TestTag storyTag = TestTag.withValue("story:a story");
        assertThat(storyTag.getName(), is("a story"));
        assertThat(storyTag.getType(), is("story"));
    }

    @Test
    public void should_define_default_feature_tags_using_a_shorthand_notation() {
        TestTag storyTag = TestTag.withValue("a tag");
        assertThat(storyTag.getName(), is("a tag"));
        assertThat(storyTag.getType(), is("tag"));
    }

}
