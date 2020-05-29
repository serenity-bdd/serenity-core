package net.serenitybdd.screenplay;

import net.thucydides.core.annotations.Step;

class EatsFruit implements Performable {

    private String fruit;

    public EatsFruit() {}

    EatsFruit(String fruit) {
        this.fruit = fruit;
    }

    public static EatsFruit loudly() {
        return new EatsFruit("peach");
    }

    @Override
    @Step("{0} eats a #fruit")
    public <T extends Actor> void performAs(T actor) {}
}