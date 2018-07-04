package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;

import java.util.function.Consumer;

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
