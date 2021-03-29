package serenityscreenplay.net.serenitybdd.screenplay;

import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;

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