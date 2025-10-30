package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

public class ChewsTheBanana implements Performable {
    @Override
    @Step("{0} chews the banana")
    public <T extends Actor> void performAs(T actor) {}
}
