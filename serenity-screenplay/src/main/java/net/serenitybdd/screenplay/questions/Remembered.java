package net.serenitybdd.screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

/**
 * A question that returns a value previously remembered by the actor.
 * @param <T>
 */
public class Remembered<T> implements Question<T> {

    private final String key;

    private Remembered(String key) {
        this.key = key;
    }

    public static Remembered valueOf(String key) {
        return new Remembered(key);
    }

    @Override
    public T answeredBy(Actor actor) {
        return actor.recall(key);
    }
}
