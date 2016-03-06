package net.serenitybdd.screenplay;

import com.beust.jcommander.internal.Lists;
import org.hamcrest.Matcher;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class GivenWhenThen {
    public static <T extends PerformsTasks> T givenThat(T actor) {
        return actor;
    }
    public static Actor andThat(Actor actor) {return actor; }

    public static Actor when(Actor actor) {  return actor; }
    public static Actor then(Actor actor) { return actor; }
    public static Actor and(Actor actor) { return actor; }
    public static Actor but(Actor actor) { return actor; }

    public static <T> void then(T actual, Matcher<? super T> matcher) {
        assertThat(actual, matcher);
    }

    public static <T> Consequence<T> seeThat(Question<? extends T> actual, Matcher<T> expected) {
        return new QuestionConsequence(actual, expected);
    }

    public static <T> Consequence<T>[] seeThat(Question<? extends T> actual, Matcher<T>... expectedMatchers) {
        List<Consequence<T>> consequences = Lists.newArrayList();
        for(Matcher<T> matcher : expectedMatchers) {
            consequences.add(new QuestionConsequence(actual, matcher));
        }
        return consequences.toArray(new Consequence[]{});
    }

}
