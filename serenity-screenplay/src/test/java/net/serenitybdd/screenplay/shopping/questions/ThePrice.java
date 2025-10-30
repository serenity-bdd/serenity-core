package net.serenitybdd.screenplay.shopping.questions;

import net.serenitybdd.screenplay.Question;

public class ThePrice {

    public static Question<Integer> total() {
        return actor -> 100;
    }

    public static Question<Integer> vat() {
        return actor -> 20;
    }

    public static Question<Integer> vat(final boolean throwError) {
        return actor -> {
            if (throwError) {
                throw new RuntimeException("Oh crap!");
            }
            return 20;
        };
    }

    public static Question<Integer> totalWithVAT() {
        return actor -> 120;
    }

}
