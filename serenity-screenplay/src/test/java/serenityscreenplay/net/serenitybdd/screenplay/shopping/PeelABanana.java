package serenityscreenplay.net.serenitybdd.screenplay.shopping;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;
import serenitymodel.net.thucydides.core.annotations.Step;

public class PeelABanana implements Performable {
    @Override
    @Step("{0} peels a banana")
    public <T extends Actor> void performAs(T actor) {}
}
