package net.thucydides.core.util;

public class MultipleInflection {

    private final int cardinality;

    protected MultipleInflection(int cardinality) {
        this.cardinality = cardinality;
    }

    public Inflection times(String word) {
        return new Inflection(word, cardinality);
    }
}
