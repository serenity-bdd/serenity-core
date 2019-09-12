package net.serenitybdd.screenplay.waits;

import net.serenitybdd.screenplay.Performable;

public interface WithTimeUnits {
    Performable seconds();
    Performable milliseconds();
}
