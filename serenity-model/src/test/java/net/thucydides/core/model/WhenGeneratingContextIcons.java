package net.thucydides.core.model;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenGeneratingContextIcons {

    @Test
    public void should_produce_no_icons_when_there_is_no_context_specified() {
        String icon = ContextIcon.forOutcome(outcomeWithContextSetTo(""));

        assertThat(icon, is(""));
    }

    @Test
    public void should_produce_an_icon_when_the_context_specifies_a_recognised_name() {
        String icon = ContextIcon.forOutcome(outcomeWithContextSetTo("linux"));

        assertThat(icon, is("<span class='context-icon'><i class='fa fa-linux' aria-hidden='true'></i></span>"));
    }

    @Test
    public void should_produce_two_icons_when_the_context_specifies_two_recognised_names() {
        String icon = ContextIcon.forOutcome(outcomeWithContextSetTo("ios,safari"));

        assertThat(icon, is("<span class='context-icon'><i class='fa fa-apple' aria-hidden='true'></i> <i class='fa fa-safari' aria-hidden='true'></i></span>"));
    }

    @Test
    public void should_use_the_name_provided_if_it_did_not_match_any_recognised_names() {
        String icon = ContextIcon.forOutcome(outcomeWithContextSetTo("foo,bar"));

        assertThat(icon, is("<span class='context-icon'>FOO BAR</span>"));
    }

    private TestOutcome outcomeWithContextSetTo(String value) {
        EnvironmentVariables env = new MockEnvironmentVariables();
        env.setProperty("context", value);

        TestOutcome outcome = new TestOutcome("success");
        outcome.setEnvironmentVariables(env);

        return outcome;
    }
}
