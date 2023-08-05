package net.serenitybdd.screenplay;

import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.annotations.Step;

class EatsAWatermelon implements Performable, CanBeSilent {

    private final boolean isSilent;
    private final String fruit;

    EatsAWatermelon(boolean isSilent, String fruit) {
        this.isSilent = isSilent;
        this.fruit = fruit;
    }

    public static EatsAWatermelon quietly() {
        return Tasks.instrumented(EatsAWatermelon.class,true, "watermelon quietly");
    }

    public static EatsAWatermelon noisily() {
        return Tasks.instrumented(EatsAWatermelon.class,false, "watermelon loudly");
    }

    @Override
    @Step("{0} eats a #fruit")
    public <T extends Actor> void performAs(T actor) {}

    @Override
    public boolean isSilent() {
        return isSilent;
    }
}
