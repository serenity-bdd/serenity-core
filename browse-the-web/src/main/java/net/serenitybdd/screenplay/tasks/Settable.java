package net.serenitybdd.screenplay.tasks;

import net.serenitybdd.screenplay.Performable;

/**
 * A specialised Performable task that sets the value of something.
 */
public interface Settable extends Performable {
    Performable to(String newValue);
}
