package net.serenitybdd.screenplay;

import net.thucydides.core.annotations.Step;

class EatsAPeach implements Performable {

    private String fruit;

    public EatsAPeach() {}

    EatsAPeach(String fruit) {
        this.fruit = fruit;
    }

    public static EatsAPeach loudly() {
        return new EatsAPeach("peach");
    }

    @Override
    @Step("{0} eats a #fruit")
    public <T extends Actor> void performAs(T actor) {}
}