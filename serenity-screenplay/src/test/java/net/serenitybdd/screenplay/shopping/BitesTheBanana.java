package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.annotations.Step;

public class BitesTheBanana implements Performable {
    @Override
    @Step("{0} bites the banana")
    public <T extends Actor> void performAs(T actor) {}
}
