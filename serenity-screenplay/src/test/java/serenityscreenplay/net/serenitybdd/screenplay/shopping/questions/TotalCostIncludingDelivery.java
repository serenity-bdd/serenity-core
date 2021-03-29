package serenityscreenplay.net.serenitybdd.screenplay.shopping.questions;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;

public class TotalCostIncludingDelivery implements Question<Integer> {
    public static TotalCostIncludingDelivery theTotalCostIncludingDelivery() {
        return new TotalCostIncludingDelivery();
    }

    @Override
    public Integer answeredBy(Actor actor) {
        return 25;
    }
}
