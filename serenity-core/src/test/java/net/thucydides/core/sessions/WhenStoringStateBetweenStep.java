package net.thucydides.core.sessions;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenStoringStateBetweenStep {

    @Mock
    Pages pages;

    class SampleSteps extends ScenarioSteps {

        public SampleSteps(final Pages pages) {
            super(pages);
        }

        @Step
        public void storeName(String value) {
            Serenity.getCurrentSession().put("name", value);
        }

        @Step
        public String retrieveName() {
            Serenity.getCurrentSession().shouldContainKey("name");
            return (String) Serenity.getCurrentSession().get("name");
        }

        @Step
        public boolean checkName() {
            return Serenity.getCurrentSession().containsKey("name");
        }

    }


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        Serenity.initializeTestSession();
    }

    @Test
    public void should_be_able_to_store_variables_between_steps() {
        SampleSteps steps = new SampleSteps(pages);

        steps.storeName("joe");

        assertThat(steps.retrieveName(), is("joe"));
    }

    @Test(expected = AssertionError.class)
    public void should_throw_an_exception_if_no_variable_is_found() {
        SampleSteps steps = new SampleSteps(pages);
        steps.retrieveName();
    }

    @Test
    public void should_be_able_to_ask_if_a_session_variable_has_been_set() {
        SampleSteps steps = new SampleSteps(pages);

        assertThat(steps.checkName(), is(false));

        steps.storeName("joe");

        assertThat(steps.checkName(), is(true));
    }

    @Test
    public void should_clear_session_at_the_start_of_each_test() {
        SampleSteps steps = new SampleSteps(pages);

        steps.storeName("joe");
        assertThat(steps.retrieveName(), is("joe"));

        Serenity.initializeTestSession();

        assertThat(steps.checkName(), is(false));
    }

    @Test
    public void should_clear_session_variable_if_null_is_provided() {
        SampleSteps steps = new SampleSteps(pages);

        steps.storeName("joe");
        assertThat(steps.retrieveName(), is("joe"));

        steps.storeName(null);

        assertThat(steps.checkName(), is(false));
    }

}
