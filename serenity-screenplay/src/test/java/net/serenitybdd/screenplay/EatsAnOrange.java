package net.serenitybdd.screenplay;

import net.serenitybdd.annotations.Step;

class EatsAnOrange implements Performable {

    @Override
    @Step("{0} eats a large orange")
    public <T extends Actor> void performAs(T actor) {}


}
