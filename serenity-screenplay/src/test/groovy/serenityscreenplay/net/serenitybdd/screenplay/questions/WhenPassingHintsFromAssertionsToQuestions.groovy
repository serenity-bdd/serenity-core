package serenityscreenplay.net.serenitybdd.screenplay.questions

import serenitycore.net.serenitybdd.core.pages.WebElementState
import serenityscreenplay.net.serenitybdd.screenplay.Actor
import serenityscreenplay.net.serenitybdd.screenplay.Question
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import spock.lang.Specification

class WhenPassingHintsFromAssertionsToQuestions extends Specification {

    interface SucceedsIfNotVisible extends QuestionHint {}

    class CannotBeSeenMatcher<T extends WebElementState> extends TypeSafeMatcher<T> implements SucceedsIfNotVisible {

        @Override
        protected boolean matchesSafely(T element) {
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("an element that is not visible");
        }

        @Override
        protected void describeMismatchSafely(T item, Description mismatchDescription) {
            mismatchDescription.appendText(WebElementStateDescription.forElement(item)).appendText(" was visible");
        }
    }

    class CannotBeSeen implements Question<Boolean>, AcceptsHints{

        boolean answer = false;

        @Override
        Boolean answeredBy(Actor actor) {
            return answer
        }

        @Override
        void apply(Set<Class<? extends QuestionHint>> hints) {
            answer = hints.contains(SucceedsIfNotVisible.class)
        }
    }

    class BasicQuestion implements Question<Boolean> {

        @Override
        Boolean answeredBy(Actor actor) {
            return false
        }
    }

    def "Some matchers provide hints to questions to help them execute more efficiently"() {
        when:
            Matcher<WebElementState> isNotVisible = new CannotBeSeenMatcher<>();

        then:
            QuestionHints.fromAssertion(isNotVisible).containsAll([SucceedsIfNotVisible.class])
    }

    def "Some questions can be passed hints from the assertions"() {
        given:
            CannotBeSeen question = new CannotBeSeen();
            Matcher<WebElementState> isNotVisible = new CannotBeSeenMatcher<>();

        when:
            QuestionHints.addHints(QuestionHints.fromAssertion(isNotVisible)).to(question);

        then:
            question.answeredBy(Actor.named("Actor"))
    }

    def "Normal questions can be passed hints with no effect"() {
        given:
        BasicQuestion question = new BasicQuestion();
        Matcher<WebElementState> isNotVisible = new CannotBeSeenMatcher<>();

        when:
        QuestionHints.addHints(QuestionHints.fromAssertion(isNotVisible)).to(question);

        then:
        !question.answeredBy(Actor.named("Actor"))
    }
}
