package net.thucydides.core.matchers;

import net.thucydides.model.matchers.*;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenMatchingPropertyValuesWithMaps {

    @Test
    public void should_match_field_by_name() {
        BeanPropertyMatcher matcher = new BeanPropertyMatcher("firstName", is("Bill"));
        Map<String, String> person = mappedPerson("Bill", "Oddie");

        assertThat(matcher.matches(person)).isTrue();
    }

    private Map<String,String> mappedPerson(String firstname, String lastname) {
        Map<String, String> person = new HashMap<String, String>();
        person.put("firstName", firstname);
        person.put("lastName", lastname);
        return person;
    }

    @Test
    public void should_fail_if_match_is_not_successful() {
        BeanPropertyMatcher matcher = new BeanPropertyMatcher("firstName", is("Bill"));
        Map<String, String> person = mappedPerson("Graeam", "Garden");

        assertThat(matcher.matches(person)).isFalse();
    }

    @Test
    public void should_obtain_matcher_from_fluent_static_method() {
        BeanFieldMatcher matcher = (BeanFieldMatcher) BeanMatchers.the("firstName", is("Bill"));
        Map<String, String> person = mappedPerson("Bill", "Oddie");
        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void should_obtain_instanciated_matcher_from_matcher() {
        Matcher<Object> matcher = ((BeanFieldMatcher) BeanMatchers.the("firstName", is("Bill"))).getMatcher();
        Map<String, String> person = mappedPerson("Bill", "Oddie");
        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void instanciated_matcher_should_provide_meaningful_description() {
        Matcher<Object> matcher = ((BeanFieldMatcher) BeanMatchers.the("firstName", is("Bill"))).getMatcher();
        assertThat(matcher.toString()).isEqualTo("firstName is 'Bill'");
    }

    @Test
    public void should_filter_list_of_beans_by_matchers() {
        List<Map<String, String>> persons = Arrays.asList(mappedPerson("Bill", "Oddie"),
                mappedPerson("Graeam", "Garden"),
                mappedPerson("Tim", "Brooke-Taylor"));

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatcherAsserts.matches(persons, firstNameIsBill, lastNameIsOddie)).isTrue();
    }

    @Test
    public void should_fail_filter_if_no_matching_elements_found() {
        List<Map<String,String>> persons = Arrays.asList(mappedPerson("Bill", "Kidd"),
                mappedPerson("Graeam", "Garden"),
                mappedPerson("Tim", "Brooke-Taylor"));

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatcherAsserts.matches(persons, firstNameIsBill, lastNameIsOddie)).isFalse();
    }

    @Test
    public void should_return_matching_element() {
        Map<String,String> bill = mappedPerson("Bill", "Oddie");
        Map<String,String> graham = mappedPerson("Graeam", "Garden");
        Map<String,String> tim = mappedPerson("Tim", "Brooke-Taylor");
        List<Map<String,String>> persons = Arrays.asList(bill, graham, tim);

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatcherAsserts.filterElements(persons, firstNameIsBill, lastNameIsOddie)).contains(bill);
    }

    @Test
    public void should_return_no_elements_if_no_matching_elements_found() {
        Map<String,String> billoddie = mappedPerson("Bill", "Oddie");
        Map<String,String> billkidd = mappedPerson("Bill", "Kidd");
        Map<String,String> graham = mappedPerson("Graeam", "Garden");
        Map<String,String> tim = mappedPerson("Tim", "Brooke-Taylor");
        List<Map<String,String>> persons = Arrays.asList(billoddie, billkidd, graham, tim);

        BeanMatcher firstNameIsJoe = BeanMatchers.the("firstName", is("Joe"));

        assertThat(BeanMatcherAsserts.filterElements(persons, firstNameIsJoe)).isEmpty();
    }

    @Test
    public void should_return_multiple_matching_elements() {
        Map<String,String> billoddie = mappedPerson("Bill", "Oddie");
        Map<String,String> billkidd = mappedPerson("Bill", "Kidd");
        Map<String,String> graham = mappedPerson("Graeam", "Garden");
        Map<String,String> tim = mappedPerson("Tim", "Brooke-Taylor");
        List<Map<String,String>> persons = Arrays.asList(billoddie, billkidd, graham, tim);

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));

        assertThat(BeanMatcherAsserts.filterElements(persons, firstNameIsBill)).contains(billkidd, billoddie);
    }

    @Test
    public void should_match_against_a_single_bean() {
        Map<String, String> person = mappedPerson("Bill", "Oddie");

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatcherAsserts.matches(person, firstNameIsBill, lastNameIsOddie)).isTrue();
    }

    @Test
    public void should_not_match_against_non_matching_single_bean() {
        Map<String, String> person = mappedPerson("Bill", "Kidd");

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatcherAsserts.matches(person, firstNameIsBill, lastNameIsOddie)).isFalse();
    }

    @Test
    public void should_display_detailed_diagnostics_when_a_single_bean_fails_to_match() {
        Map<String, String> person = mappedPerson("Bill", "Kidd");

        boolean assertionThrown = false;
        String exceptionMessage = null;
        try {
            BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
            BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

            BeanMatcherAsserts.shouldMatch(person, firstNameIsBill, lastNameIsOddie);
        } catch(AssertionError e) {
            assertionThrown = true;
            exceptionMessage = e.getMessage();
        }

        MatcherAssert.assertThat(assertionThrown, is(true));
        MatcherAssert.assertThat(exceptionMessage,
                allOf(containsString("Expected [firstName is 'Bill', lastName is 'Oddie'] but was"),
                        containsString("firstName = 'Bill'"),
                        containsString("lastName = 'Kidd")));

    }


    @Test
    public void should_check_the_size_of_a_collection_and_its_contents() {
        List<Map<String,String>> persons = Arrays.asList(mappedPerson("Bill", "Oddie"),
                mappedPerson("Bill", "Kidd"),
                mappedPerson("Graeam", "Garden"),
                mappedPerson("Tim", "Brooke-Taylor"));

        BeanMatcher containsTwoEntries = BeanMatchers.the_count(is(2));
        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));

        BeanMatcherAsserts.shouldMatch(persons, containsTwoEntries, firstNameIsBill);
    }

    @Test
    public void should_check_field_uniqueness() {
        List<Map<String,String>> persons = Arrays.asList(mappedPerson("Bill", "Oddie"),
                mappedPerson("Bill", "Kidd"),
                mappedPerson("Graeam", "Garden"),
                mappedPerson("Tim", "Brooke-Taylor"));

        BeanMatcher containsTwoEntries = BeanMatchers.the_count(is(2));
        BeanMatcher lastNamesAreDifferent = BeanMatchers.each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));

        BeanMatcherAsserts.shouldMatch(persons, containsTwoEntries, firstNameIsBill, lastNamesAreDifferent);
    }

    @Test
    public void should_check_field_absence() {
        List<Map<String,String>> persons = Arrays.asList(mappedPerson("Bill", "Oddie"),
                mappedPerson("Bill", "Kidd"),
                mappedPerson("Graeam", "Garden"),
                mappedPerson("Tim", "Brooke-Taylor"));

        BeanMatcher firstNameIsJohn = BeanMatchers.the("firstName", is("John"));
        BeanMatcherAsserts.shouldNotMatch(persons, firstNameIsJohn);
    }

    @Test(expected = AssertionError.class)
    public void should_check_field_absence_with_failing_case() {
        List<Map<String,String>> persons = Arrays.asList(mappedPerson("Bill", "Oddie"),
                mappedPerson("Bill", "Kidd"),
                mappedPerson("Graeam", "Garden"),
                mappedPerson("Tim", "Brooke-Taylor"));

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcherAsserts.shouldNotMatch(persons, firstNameIsBill);
    }

}
