package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

public class PeelABanana implements Performable {
    @Override
    @Step("{0} peels a banana")
    public <T extends Actor> void performAs(T actor) {}
}
