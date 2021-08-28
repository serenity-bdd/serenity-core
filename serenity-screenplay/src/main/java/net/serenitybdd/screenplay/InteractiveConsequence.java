package net.serenitybdd.screenplay;

import org.hamcrest.Matcher;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

/**
 * An InteractiveConsequence is a convenience class designed to make it easier to write consequences that need to be
 * preceded by actions, for example when performing a number of soft assertions on a page.
 */
public class InteractiveConsequence {

        private Performable[] actions;

        public InteractiveConsequence(Performable[] actions) {
            this.actions = actions;
        }

        public static InteractiveConsequence attemptTo(Performable... actions) {
            return new InteractiveConsequence(actions);
        }

        public <T> Consequence<T> thenCheckThat(Question<? extends T> actual, Matcher<T> expected) {
            return seeThat(actual, expected).after(actions);
        }
    }
