package net.serenitybdd.screenplay.shopping.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class TotalCostIncludingDelivery implements Question<Integer> {
    public static TotalCostIncludingDelivery theTotalCostIncludingDelivery() {
        return new TotalCostIncludingDelivery();
    }

    @Override
    public Integer answeredBy(Actor actor) {
        return 25;
    }
}
