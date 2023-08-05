package net.serenitybdd.screenplay;

import net.serenitybdd.annotations.Step;

class EatsARockmelon implements Performable {

    private final String fruit;

    EatsARockmelon(String fruit) {
        this.fruit = fruit;
    }

    public static EatsARockmelon quietly() {
        return new EatsARockmelon("rockmelon");
    }

    @Override
    @Step("{0} eats a #fruit")
    public <T extends Actor> void performAs(T actor) {}
}
