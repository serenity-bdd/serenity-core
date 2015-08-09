package net.serenitybdd.screenplay;

import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;

public class GivenWhenThen {
    public static <T extends PerformsTasks> T givenThat(T actor) {
        actor.start();
        return actor;
    }
    public static Actor andThat(Actor actor) {return actor; }

    public static Actor when(Actor actor) { return actor; }
    public static Actor then(Actor actor) { return actor; }

    public static <T> void then(T actual, Matcher<? super T> matcher) {
        assertThat(actual, matcher);
    }

    public static <T> Consequence<T> that(T actual, Matcher<? super T> expected) {
        return new Consequence(actual, expected);
    }
}
