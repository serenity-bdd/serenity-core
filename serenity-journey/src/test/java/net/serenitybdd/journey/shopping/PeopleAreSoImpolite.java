package net.serenitybdd.journey.shopping;

/**
 * Created by john on 11/11/2015.
 */
public class PeopleAreSoImpolite extends AssertionError {

    public PeopleAreSoImpolite(Throwable cause) {
        super(cause);
    }

    public PeopleAreSoImpolite(String message, Throwable cause) {
        super(message, cause);
    }
}
