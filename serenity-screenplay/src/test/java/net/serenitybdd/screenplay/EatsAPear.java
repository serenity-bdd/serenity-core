package net.serenitybdd.screenplay;

import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

class EatsAPear implements Performable {

    private String size;

    public EatsAPear(String size) {
        this.size = size;
    }

    @Override
    @Step("{0} eats a #size pear")
    public <T extends Actor> void performAs(T actor) {}

    static EatsAPear ofSize(String size) {
        return instrumented(EatsAPear.class, size);
    }

}
