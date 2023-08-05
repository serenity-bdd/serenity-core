package net.thucydides.core.matchers;

import net.thucydides.model.matchers.BeanMatcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.thucydides.model.matchers.BeanMatcherAsserts.*;
import static net.thucydides.model.matchers.BeanMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class WhenMatchingPropertyValueCollections {

    public class Person {
        private final String firstName;
        private final String lastName;
        private final DateTime birthday;
        private final int age;
        
        Person(String firstName, String lastName, DateTime birthday, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthday = birthday;
            this.age = age;
        }

        Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthday = new DateTime();
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public DateTime getBirthday() {
            return birthday;
        }
        
        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", birthday=" + birthday +
                    ", age=" + age +
                    '}';
        }
    }

    Person billoddie;
    Person billkidd;
    Person graeme;
    Person tim;

    @Before
    public void setupPeople() {
        tim = new Person("Tim", "Brooke-Taylor", 25);
        billoddie = new Person("Bill", "Oddie", 30);
        billkidd = new Person("Bill", "Kidd", 35);
        graeme = new Person("Graeme", "Garden", 40);
    }

    @Test
    public void should_filter_list_of_beans_by_matchers() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = the("lastName", is("Oddie"));

        assertThat(matches(persons, firstNameIsBill, lastNameIsOddie)).isTrue();
    }

    @Test
    public void should_check_count_and_field_constraints() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = the("lastName", is("Oddie"));
        BeanMatcher billIsUnique = the_count(is(1));
        assertThat(matches(persons, firstNameIsBill, lastNameIsOddie, billIsUnique)).isTrue();
    }

    @Test
    public void can_check_for_no_matching_entries() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher firstNameIsBilly = the("firstName", is("Billy"));
        BeanMatcher lastNameIsTheKid = the("lastName", is("the Kid"));
        BeanMatcher isNotPresent = the_count(is(0));
        assertThat(matches(persons, firstNameIsBilly, lastNameIsTheKid, isNotPresent)).isTrue();
    }

    @Test
    public void should_fail_filter_if_no_matching_elements_found() {
        List<Person> persons = Arrays.asList(tim, graeme);

        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = the("lastName", is("Oddie"));

        assertThat(matches(persons, firstNameIsBill, lastNameIsOddie)).isFalse();
    }

    @Test
    public void should_return_matching_element() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = the("lastName", is("Oddie"));

        assertThat(filterElements(persons, firstNameIsBill, lastNameIsOddie)).contains(billoddie);
    }

    @Test
    public void should_return_matching_elements_with_count_restrictions() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = the("lastName", is("Oddie"));
        BeanMatcher countIsOne = the_count(is(1));

        assertThat(filterElements(persons, firstNameIsBill, lastNameIsOddie, countIsOne)).contains(billoddie);
    }

    @Test
    public void should_match_elements_with_uniqueness_restrictions() {
        List<Person> persons = Arrays.asList(billoddie, billkidd, tim, graeme);

        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));
        BeanMatcher lastNamesAreDifferent = each("lastName").isDifferent();

        matches(persons, firstNameIsBill, lastNamesAreDifferent);
    }

    @Test
    public void should_return_no_elements_if_no_matching_elements_found() {
        List<Person> persons = Arrays.asList(billoddie, billkidd, tim, graeme);

        BeanMatcher firstNameIsJoe = the("firstName", is("Joe"));

        assertThat(filterElements(persons, firstNameIsJoe)).isEmpty();
    }

    @Test
    public void should_return_multiple_matching_elements() {
        List<Person> persons = Arrays.asList(billoddie, billkidd, tim, graeme);

        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));

        assertThat(filterElements(persons, firstNameIsBill)).contains(billkidd, billoddie);
    }

    @Test
    public void should_check_the_size_of_a_collection() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher containsThreeEntries = the_count(is(3));

        shouldMatch(persons, containsThreeEntries);
    }

    @Test
    public void should_check_the_min_value_of_a_collection() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        shouldMatch(persons, min("age", is(25)));
    }

    @Test(expected = AssertionError.class)
    public void should_check_the_min_value_of_a_collection_with_failing_case() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        shouldMatch(persons, min("age", is(30)));
    }

    @Test
    public void should_use_natural_order_for_non_numerical_min() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        shouldMatch(persons, min("firstName", is("Bill")));
    }



    @Test
    public void should_check_the_max_value_of_a_collection() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher maxAgeIs40 = max("age",is(40));

        shouldMatch(persons, maxAgeIs40);
    }

    @Test(expected = AssertionError.class)
    public void should_check_the_max_value_of_a_collection_with_failing_case() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher maxAgeIs30 = max("age",is(30));

        shouldMatch(persons, maxAgeIs30);
    }

    @Test
    public void should_use_natural_order_for_non_numerical_max() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher maxAgeIs40 = max("firstName",is("Tim"));

        shouldMatch(persons, maxAgeIs40);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_detect_invalid_fieldname_when_checking_the_max_value_of_a_collection() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher maxAgeIs30 = max("doesNotExist",is(30));

        shouldMatch(persons, maxAgeIs30);
    }

    @Test(expected = AssertionError.class)
    public void should_fail_if_the_size_of_a_collection_is_incorrect() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme);

        BeanMatcher containsTwoEntries = the_count(is(2));

        shouldMatch(persons, containsTwoEntries);
    }

    @Test
    public void should_check_the_size_of_a_collection_and_its_contents() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher containsTwoEntries = the_count(is(2));
        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));

        shouldMatch(persons, containsTwoEntries, firstNameIsBill);
    }

    @Test
    public void should_check_field_uniqueness() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher containsTwoEntries = the_count(is(2));
        BeanMatcher lastNamesAreDifferent = each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));

        shouldMatch(persons, containsTwoEntries, firstNameIsBill, lastNamesAreDifferent);
    }

    @Test(expected = AssertionError.class)
    public void should_check_field_uniqueness_when_not_unique() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billoddie);

        BeanMatcher containsTwoEntries = the_count(is(2));
        BeanMatcher lastNamesAreDifferent = each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));

        shouldMatch(persons, containsTwoEntries, firstNameIsBill, lastNamesAreDifferent);
    }

    @Test
    public void should_check_field_similarities() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billoddie);

        BeanMatcher containsTwoEntries = the_count(is(2));
        BeanMatcher lastNamesAreDifferent = each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));

        shouldNotMatch(persons, containsTwoEntries, firstNameIsBill, lastNamesAreDifferent);
    }

    @Test(expected = AssertionError.class)
    public void should_check_field_similarities_when_all_unique() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher containsTwoEntries = the_count(is(2));
        BeanMatcher lastNamesAreDifferent = each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));

        shouldNotMatch(persons, containsTwoEntries, firstNameIsBill, lastNamesAreDifferent);
    }

    @Test
    public void should_check_element_is_not_present() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billoddie);

        BeanMatcher firstNameIsJohn = the("firstName", is("John"));

        shouldNotMatch(persons, firstNameIsJohn);
    }

    @Test(expected = AssertionError.class)
    public void should_check_element_is_not_present_with_failing_case() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));

        shouldNotMatch(persons, firstNameIsBill);
    }

    @Test
    public void should_check_multiple_different_types_of_matches() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher containsTwoEntries = the_count(is(2));
        BeanMatcher lastNamesAreDifferent = each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));
        BeanMatcher maxAgeIs35 = max("age", is(35));
        shouldMatch(persons,
                containsTwoEntries,
                firstNameIsBill,
                lastNamesAreDifferent,
                maxAgeIs35);
    }



    @Test(expected = AssertionError.class)
    public void should_fail_correctly_when_checking_multiple_different_types_of_matches() {
        List<Person> persons = Arrays.asList(billoddie, tim, graeme, billkidd);

        BeanMatcher containsTwoEntries = the_count(is(2));
        BeanMatcher lastNamesAreDifferent = each("lastName").isDifferent();
        BeanMatcher firstNameIsBill = the("firstName", is("Bill"));
        BeanMatcher maxAgeIs35 = max("age", is(45));
        shouldMatch(persons,
                containsTwoEntries,
                firstNameIsBill,
                lastNamesAreDifferent,
                maxAgeIs35);
    }

    @Test
    public void should_allow_fields_with_dots_for_maps() {
        Map<String, String> map = new HashMap();
        map.put("test.a", "result1");
        BeanMatcher matcher1 = the("test.a", equalTo("result1"));
        matcher1.matches(map);
    }

    @Test
    public void should_allow_fields_with_brackets_for_maps() {
        Map<String, String> map = new HashMap();
        map.put("test[0]", "result1");
        BeanMatcher matcher1 = the("test[0]", equalTo("result1"));
        matcher1.matches(map);
    }

}
