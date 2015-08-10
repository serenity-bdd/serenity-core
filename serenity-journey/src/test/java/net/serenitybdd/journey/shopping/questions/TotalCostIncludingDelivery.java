package net.serenitybdd.journey.shopping.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.Actor;

public class TotalCostIncludingDelivery implements Question<Integer> {
    public static TotalCostIncludingDelivery theTotalCostIncludingDelivery() {
        return new TotalCostIncludingDelivery();
    }

    @Override
    public Integer answeredBy(Actor actor) {
        return 25;
    }
}
