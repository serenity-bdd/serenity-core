package serenityscreenplay.net.serenitybdd.screenplay;

public class ExternalValueQuestion {

    public static <T> Question<T> valueOf(final T value) {
        return new Question<T>() {
            @Override
            public T answeredBy(Actor actor) {
                return value;
            }
        };
    }
}
