package serenityscreenplay.net.serenitybdd.screenplay;

import serenitymodel.net.thucydides.core.annotations.Step;

class EatsAnOrange implements Performable {

    @Override
    @Step("{0} eats a large orange")
    public <T extends Actor> void performAs(T actor) {}


}