package net.serenitybdd.journey.shopping.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class TotalCost implements Question<Integer> {

    private int total;

    public TotalCost(int total) {
        this.total = total;
    }

    public static TotalCost theTotalCost() {
        return new TotalCost(14);
    }

    public static TotalCost theCorrectTotalCost() {
        return new TotalCost(15);
    }

    @Override
    public Integer answeredBy(Actor actor) {
        return total;
    }
}
