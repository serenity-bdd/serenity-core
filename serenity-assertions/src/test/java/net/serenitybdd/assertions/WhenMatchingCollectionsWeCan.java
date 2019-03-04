package net.serenitybdd.assertions;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static net.serenitybdd.assertions.CollectionMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class WhenMatchingCollectionsWeCan {
    @Test
    public void find_subsets_in_a_collection() {
        List<String> allColors = Arrays.asList("red", "green", "blue", "yellow");
        List<String> primaryColors = Arrays.asList("red", "blue", "yellow");

        assertThat(allColors, containsAll(primaryColors));
    }

    @Test
    public void report_missing_subsets_in_a_collection() {
        List<String> allColors = Arrays.asList("red", "green", "blue", "yellow");
        List<String> otherColors = Arrays.asList("red", "blue", "orange");

        assertThat(allColors, not(containsAll(otherColors)));
    }

    @Test
    public void check_that_all_elements_match_a_predicate() {
        List<String> allColors = Arrays.asList("color:red", "color:green", "color:blue", "color:yellow");

        assertThat(allColors, allMatch( color -> color.startsWith("color:")));
    }

    @Test
    public void check_that_all_elements_match_a_predicate_with_a_description() {
        List<String> allColors = Arrays.asList("color:red", "color:green", "color:blue", "color:yellow");

        assertThat(allColors, allMatch( color -> color.startsWith("color:"), "start with 'color:'"));
    }

    @Test
    public void check_that_all_elements_satisfy_a_condition() {
        List<String> allColors = Arrays.asList("color:red", "color:green", "color:blue", "color:yellow");

        assertThat(allColors, allSatisfy( color -> Assertions.assertThat(color).startsWith("color:")));
    }

    @Test
    public void check_that_the_collection_satisfy_a_condition() {
        List<String> allColors = Arrays.asList("color:red", "color:green", "color:blue", "color:yellow");

        assertThat(allColors, satisfies( colors -> Assertions.assertThat(colors).hasSize(4)));
    }

    @Test
    public void check_that_an_element_does_not_match_a_predicate() {
        List<String> allColors = Arrays.asList("red", "color:green", "color:blue", "color:yellow");

        assertThat(allColors, not(allMatch( color -> color.startsWith("color:"))));
    }

    @Test
    public void check_that_any_elements_match_a_predicate() {
        List<String> allColors = Arrays.asList("color:red", "color:green", "color:blue", "color:yellow");

        assertThat(allColors, anyMatch( color -> color.endsWith("green")));
    }

    @Test
    public void check_that_any_elements_match_a_predicate_in_an_empty_list() {
        List<String> allColors = Arrays.asList();

        assertThat(allColors, not(anyMatch( color -> color.endsWith("green"))));
    }

    @Test
    public void check_that_any_element_match_a_predicate_with_a_description() {
        List<String> allColors = Arrays.asList("color:red", "color:green", "color:blue", "color:yellow");

        assertThat(allColors, anyMatch(color -> color.endsWith("green"), "ends with green"));
    }

}
