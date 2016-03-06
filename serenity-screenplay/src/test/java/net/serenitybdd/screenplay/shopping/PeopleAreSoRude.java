package net.serenitybdd.screenplay.shopping;

/**
 * Created by john on 11/11/2015.
 */
public class PeopleAreSoRude extends AssertionError {

    public PeopleAreSoRude(String message, Throwable cause) {
        super(message, cause);
    }

    public PeopleAreSoRude(String message) {
        super(message);
    }

    public PeopleAreSoRude(Throwable cause) {
        super(cause);
    }
}
