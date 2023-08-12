package net.thucydides.core.matchers;

import net.thucydides.model.matchers.BeanCountMatcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenMatchingCollectionCounts {

    @Test
    public void should_match_list_collection() {
        List<String> list = Arrays.asList();
        
        BeanCountMatcher matcher = new BeanCountMatcher(is(0));
        
        assertThat(matcher.matches(list), is(true));
    }

    @Test
    public void should_match_non_list_collection() {
        List<String> list = Arrays.asList("bill");

        BeanCountMatcher matcher = new BeanCountMatcher(is(1));

        assertThat(matcher.matches(list), is(true));
    }

    @Test
    public void should_match_larger_non_list_collection() {
        List<String> list = Arrays.asList("bill","tim");

        BeanCountMatcher matcher = new BeanCountMatcher(is(2));

        assertThat(matcher.matches(list), is(true));
    }

    @Test
    public void should_fail_to_match_non_matching_collection() {
        List<String> list = Arrays.asList("bill","tim");

        BeanCountMatcher matcher = new BeanCountMatcher(is(10));

        assertThat(matcher.matches(list), is(false));
    }
}
