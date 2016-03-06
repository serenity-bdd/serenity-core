package net.serenitybdd.screenplay;

class TheCost implements Question<Integer> {

        int cost;

        @Override
        Integer answeredBy(Actor actor) {
            return cost;
        }

        static of(int cost) {
            return new TheCost(cost: cost)
        }
    }
