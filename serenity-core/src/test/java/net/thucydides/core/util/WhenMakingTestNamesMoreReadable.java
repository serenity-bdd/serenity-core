package net.thucydides.core.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class WhenMakingTestNamesMoreReadable {

    @Test
    public void null_should_be_converted_to_an_empty_string() {
        assertThat(NameConverter.humanize(null), is(""));
    }

    @Test
    public void an_empty_string_should_be_converted_to_an_empty_string() {
        assertThat(NameConverter.humanize(""), is(""));
    }

    @Test
    public void simple_lowercase_name_should_be_capitalized() {
        assertThat(NameConverter.humanize("sometest"), is("Sometest"));
    }

    @Test
    public void camel_cased_name_should_be_split() {
        assertThat(NameConverter.humanize("someTest"), is("Some test"));
    }

    @Test
    public void camel_cased_test_names_should_be_converted_to_human_readable_sentences() {
        assertThat(NameConverter.humanize("aTestClassName"), is("A test class name"));
    }

    @Test
    public void already_readable_titles_should_not_be_modified() {
        assertThat(NameConverter.humanize("This is a COOL test"), is("This is a COOL test"));
    }

    @Test
    public void test_names_with_parameters_should_only_modify_the_name() {
        assertThat(NameConverter.humanize("aTestMethod: ABC def bGd"), is("A test method: ABC def bGd"));
    }

    @Test
    public void test_names_should_not_convert_single_letter_words() {
        assertThat(NameConverter.humanize("a_test_method_with_a_in_it"), is("A test method with a in it"));
    }

    @Test
    public void test_names_should_not_convert_single_letter_words_in_camel_case() {
        assertThat(NameConverter.humanize("MyTestCaseWithoutAStory"), is("My test case without a story"));
    }

    @Test
    public void should_recognize_acronyms() {
        assertThat(NameConverter.humanize("aTESTMethod"), is("A TEST method"));
    }


    @Test
    public void camelCase_method_names_should_be_converted_to_human_readable_sentences() {
        assertThat(NameConverter.humanize("aTestMethod"), is("A test method"));
    }

    @Test
    public void underscored_test_names_should_be_converted_to_human_readable_sentences() {
        assertThat(NameConverter.humanize("a_test_method"), is("A test method"));
    }

    @Test
    public void acronyms_in_underscored_test_names_should_be_conserved() {
        assertThat(NameConverter.humanize("ABC_test_method"), is("ABC test method"));
    }

    @Test
    public void human_test_names_should_be_converted_to_underscore_filenames() {
        assertThat(NameConverter.underscore("A test method"), is("a_test_method"));
    }

    @Test
    public void null_should_be_converted_to_empty_string_underscore_filename() {
        assertThat(NameConverter.underscore(null), is(""));
    }

    @Test
    public void should_remove_parameters_from_method_names() {
        assertThat(NameConverter.withNoArguments("a_test_method: 1, 2"), is("a_test_method"));
    }

    @Test
    public void should_remove_parameters_from_null_method_names() {
        assertThat(NameConverter.withNoArguments(null), is(nullValue()));
    }

    @Test
    public void should_remove_indexes_from_method_names() {
        assertThat(NameConverter.withNoArguments("a_test_method[0]"), is("a_test_method"));
    }

    @Test
    public void humanized_camelCase_methods_can_contains_CSV() {
        assertThat(NameConverter.humanize("aTestMethodForCSVFormat"), is("A test method for CSV format"));
    }

    @Test
    public void humanized_camelCase_methods_can_contains_JSON() {
        assertThat(NameConverter.humanize("aTestMethodForJSONFormat"), is("A test method for JSON format"));
    }

    @Test
    public void humanized_camelCase_methods_can_contains_XML() {
        assertThat(NameConverter.humanize("aTestMethodForXMLFormat"), is("A test method for XML format"));
    }
}

