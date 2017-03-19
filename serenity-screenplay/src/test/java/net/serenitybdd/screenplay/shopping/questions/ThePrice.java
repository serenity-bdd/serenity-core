package net.serenitybdd.screenplay.shopping.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ThePrice {

    public static Question<Integer> total() {
        return new Question<Integer>() {
            @Override
            public Integer answeredBy(Actor actor) {
                return 100;
            }
        };
    }

    public static Question<Integer> vat() {
        return new Question<Integer>() {
            @Override
            public Integer answeredBy(Actor actor) {
                return 20;
            }
        };
    }

    public static Question<Integer> vat(final boolean throwError) {
        return new Question<Integer>() {
            @Override
            public Integer answeredBy(Actor actor) {
                if (throwError) {
                    throw new RuntimeException("Oh crap!");
                }
                return 20;
            }
        };
    }

    public static Question<Integer> totalWithVAT() {
        return new Question<Integer>() {
            @Override
            public Integer answeredBy(Actor actor) {
                return 120;
            }
        };
    }

}
