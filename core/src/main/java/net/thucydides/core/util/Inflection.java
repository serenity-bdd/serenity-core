package net.thucydides.core.util;

public class Inflection {
    private final static int DEFAULT_PLURAL_COUNT = 2;

    private final String word;
    private final int pluralCount;

    protected Inflection(String word) {
        this(word, DEFAULT_PLURAL_COUNT);
    }

    protected Inflection(String word, int pluralCount) {
        this.word = word;
        this.pluralCount = pluralCount;
    }

    public String toString() {
        return word;
    }

    public Inflection inPluralForm() {
        return new Inflection(Inflector.getInstance().pluralize(word, pluralCount));
    }

    public Inflection inSingularForm() {
        return new Inflection(Inflector.getInstance().singularize(word));
    }
    
    public Inflection startingWithACapital() {
        return new Inflection(Inflector.getInstance().capitalize(word));
    }
    
    public Inflection inHumanReadableForm() {
        Inflector inflector = Inflector.getInstance();
        return new Inflection(inflector.humanize(inflector.underscore(word)));
    }

    public Inflection withUnderscores() {
        return new Inflection(Inflector.getInstance().underscore(word));
    }

    public Inflection asATitle() {
        return new Inflection(Inflector.getInstance().titleCase(word));
    }
}
