package net.serenitybdd.screenplay;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.markers.IsSilent;

/**
 * A convenience method to allow an actor to remember things when performing tasks.
 * <p></p>
 * <p>An actor can remember values, e.g.
 * <pre>
 *     <code>
 *         actor.attemptsTo(RememberThat.theValueOf("favorite color").is("blue"))"
 *     </code>
 * </pre>
 * </p>
 * <p>
 * An actor can also remember the answers to questions, e.g.
 * <pre>
 *     <code>
 *         Question&lt;String&gt; favoriteColor = ...
 *         actor.attemptsTo(RememberThat.theValueOf("favorite color").isAnsweredBy(favoriteColor))
 *     </code>
 * </pre>
 * </p>
 */
public abstract class RememberThat implements Performable, IsSilent {

    public static MemoryBuilder theValueOf(String memoryKey) {
        return new MemoryBuilder(memoryKey);
    }

    public static class WithValue extends RememberThat {

        private final String memoryKey;
        private final Object value;

        @Override
        public <T extends Actor> void performAs(T actor) {
            actor.remember(memoryKey, value);
        }

        public WithValue(String memoryKey, Object value) {
            this.memoryKey = memoryKey;
            this.value = value;
        }
    }

    public static class WithQuestion extends RememberThat {

        private final String memoryKey;
        private final Question<?> question;

        @Override
        public <T extends Actor> void performAs(T actor) {
            actor.remember(memoryKey, question.answeredBy(actor));
        }

        public WithQuestion(String memoryKey, Question<?> question) {
            this.memoryKey = memoryKey;
            this.question = question;
        }
    }

    public static class MemoryBuilder {
        private final String memoryKey;

        MemoryBuilder(String memoryKey) {
            this.memoryKey = memoryKey;
        }

        public RememberThat is(Object value) {
            return Instrumented.instanceOf(WithValue.class).withProperties(memoryKey, value);
        }

        public RememberThat isAnsweredBy(Question<?> value) {
            return Instrumented.instanceOf(WithQuestion.class).withProperties(memoryKey, value);
        }
    }
}
