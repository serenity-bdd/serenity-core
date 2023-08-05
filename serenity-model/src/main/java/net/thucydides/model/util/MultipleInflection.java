package net.thucydides.model.util;

public class MultipleInflection {

    private final long cardinality;
    private final Inflector inflector;

    protected MultipleInflection(long cardinality, Inflector inflector) {
        this.cardinality = cardinality;
        this.inflector = inflector;
    }

    public Inflection times(String word) {
        return new Inflection(word, cardinality, inflector);
    }
}
