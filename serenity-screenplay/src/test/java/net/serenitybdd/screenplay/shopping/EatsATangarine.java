package net.serenitybdd.screenplay.shopping;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

import static net.serenitybdd.screenplay.Tasks.instrumented;

class EatsATangarine implements Performable {

    private String size;

    public EatsATangarine(String size) {
        this.size = size;
    }

    @Override
    @Step("{0} eats a #size pear")
    public <T extends Actor> void performAs(T actor) {}

    static EatsATangarine ofSize(String size) {
        return instrumented(EatsATangarine.class, size);
    }

}
