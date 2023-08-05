package net.thucydides.core.matchers;

import net.thucydides.model.matchers.*;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import static net.thucydides.model.matchers.dates.DateMatchers.isAfter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WhenMatchingPropertyValues {

    public class Person {
        private final String firstName;
        private final String lastName;
        private final DateTime birthday;
        private final BigDecimal favoriteNumber;

        Person(String firstName, String lastName, DateTime birthday) {
            this(firstName, lastName, birthday, new BigDecimal("0.0"));
        }

        Person(String firstName, String lastName, DateTime birthday, BigDecimal favoriteNumber) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthday = birthday;
            this.favoriteNumber = favoriteNumber;
        }

        Person(String firstName, String lastName) {
            this(firstName, lastName, new DateTime(), new BigDecimal("0.0"));
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

        public BigDecimal getFavoriteNumber() {
            return favoriteNumber;
        }
    }

    @Test
    public void should_match_field_by_name() {
        BeanFieldMatcher matcher = new BeanPropertyMatcher("firstName", is("Bill"));
        Person person = new Person("Bill", "Oddie");

        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void should_match_date_fields() {
        Person bill = new Person("Bill", "Oddie", new DateTime(1950, 1, 1, 12, 1));

        BeanFieldMatcher birthdayAfter1900 = new BeanPropertyMatcher("birthday", isAfter(new DateTime(1900, 1, 1, 1, 0)));
        assertThat(birthdayAfter1900.matches(bill)).isTrue();
    }

    @Test
    public void should_match_number_fields() {
        Person bill = new Person("Bill", "Oddie", new DateTime(1950, 1, 1, 12, 1), new BigDecimal("42.1"));

        BeanFieldMatcher favoriteNumberIsNot42 = new BeanPropertyMatcher("favoriteNumber", greaterThan(new BigDecimal("42")));
        assertThat(favoriteNumberIsNot42.matches(bill)).isTrue();
    }
    @Test
    public void should_fail_if_match_is_not_successful() {
        BeanFieldMatcher matcher = new BeanPropertyMatcher("firstName", is("Bill"));
        Person person = new Person("Graeam", "Garden");

        assertThat(matcher.matches(person)).isFalse();
    }

    @Test
    public void should_display_expected_values_when_printed() {
        BeanMatcher matcher = new BeanPropertyMatcher("firstName", is("Bill"));
        assertThat(matcher.toString()).isEqualTo("firstName is 'Bill'");
    }

    @Test
    public void should_obtain_matcher_from_fluent_static_method() {
        BeanFieldMatcher matcher = (BeanFieldMatcher) BeanMatchers.the("firstName", is("Bill"));
        Person person = new Person("Bill", "Oddie");
        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void should_obtain_instanciated_matcher_from_matcher() {
        Matcher<Object> matcher = ((BeanFieldMatcher) BeanMatchers.the("firstName", is("Bill"))).getMatcher();
        Person person = new Person("Bill", "Oddie");
        assertThat(matcher.matches(person)).isTrue();
    }

    @Test
    public void instanciated_matcher_should_provide_meaningful_description() {
        Matcher<Object> matcher = ((BeanFieldMatcher)BeanMatchers.the("firstName", is("Bill"))).getMatcher();
        assertThat(matcher.toString()).isEqualTo("firstName is 'Bill'");
    }


    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void should_match_against_a_single_bean() {
        Person person = new Person("Bill", "Oddie");

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatcherAsserts.matches(person, firstNameIsBill, lastNameIsOddie)).isTrue();
    }

    @Test
    public void should_not_match_against_non_matching_single_bean() {
        Person person = new Person("Bill", "Kidd");

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        assertThat(BeanMatcherAsserts.matches(person, firstNameIsBill, lastNameIsOddie)).isFalse();
    }

    @Test
    public void should_display_detailed_diagnostics_when_a_single_bean_fails_to_match() {
        Person person = new Person("Bill", "Kidd");

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
        assertThat(assertionThrown, is(true));
        assertThat(exceptionMessage,
                        allOf(containsString("Expected [firstName is 'Bill', lastName is 'Oddie'] but was"),
                                containsString("firstName = 'Bill'"),
                                containsString("lastName = 'Kidd")));
    }

    @Test
    public void should_fail_test_if_field_does_not_exist() {
        DodgyBean person = new DodgyBean("Bill", "Oddie");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(containsString("Could not find property value for field-does-not-exist"));

        BeanMatcher lastNameIsOddie = BeanMatchers.the("field-does-not-exist", is("Oddie"));

        BeanMatcherAsserts.shouldMatch(person, lastNameIsOddie);
    }

    public class DodgyBean {
        private final String firstName;
        private final String lastName;

        DodgyBean(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            throw new IllegalAccessError();
        }

        public String getLastName() {
            return lastName;
        }
    }

    @Test
    public void should_report_dodgy_field_if_cant_read_field_value() {
        DodgyBean person = new DodgyBean("Bill", "Kidd");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(containsString("Could not find property value for firstName"));

        BeanMatcher firstNameIsBill = BeanMatchers.the("firstName", is("Bill"));
        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        BeanMatcherAsserts.shouldMatch(person, firstNameIsBill, lastNameIsOddie);
    }

    @Test
    public void should_raise_issue_if_fields_cant_be_introspected() {
        DodgyBean person = new DodgyBean("Bill", "Kidd");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(containsString("Could not read bean properties"));

        BeanMatcher lastNameIsOddie = BeanMatchers.the("lastName", is("Oddie"));

        BeanMatcherAsserts.shouldMatch(person, lastNameIsOddie);
    }


}
