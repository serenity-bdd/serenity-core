package serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions;

import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;

/**
 * A specialised Performable task that sets the value of something.
 */
public interface Settable extends Interaction {
    Performable to(String newValue);
}
