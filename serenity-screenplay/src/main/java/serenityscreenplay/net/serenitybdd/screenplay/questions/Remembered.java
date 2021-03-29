package serenityscreenplay.net.serenitybdd.screenplay.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenityscreenplay.net.serenitybdd.screenplay.annotations.Subject;

/**
 * A question that returns a value previously remembered by the actor.
 * @param <T>
 */
@Subject("#key")
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
