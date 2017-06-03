package net.thucydides.core.util;

public class MultipleInflection {

    private final int cardinality;
    private final Inflector inflector;

    protected MultipleInflection(int cardinality, Inflector inflector) {
        this.cardinality = cardinality;
        this.inflector = inflector;
    }

    public Inflection times(String word) {
        return new Inflection(word, cardinality, inflector);
    }
}
