package net.thucydides.model.util;

public class Inflection {
    private final static int DEFAULT_PLURAL_COUNT = 2;

    private String word;
    private final long pluralCount;
    private final Inflector inflector;

    Inflection(String word, Inflector inflector) {
        this(word, DEFAULT_PLURAL_COUNT, inflector);
    }

    Inflection(String word, long pluralCount, Inflector inflector) {
        this.word = word;
        this.pluralCount = pluralCount;
        this.inflector = inflector;
    }

    public String toString() {
        return word;
    }

    public Inflection inPluralForm() {
        this.word = inflector.pluralize(word, pluralCount);
        return this;
    }

    public Inflection inSingularForm() {
        this.word = inflector.singularize(word);
        return this;
    }
    
    public Inflection startingWithACapital() {
        this.word = inflector.capitalize(word);
        return this;
    }
    
    public Inflection inHumanReadableForm() {
        this.word = inflector.humanize(inflector.underscore(word));
        return this;
    }

    public Inflection withUnderscores() {
        this.word = inflector.underscore(word);
        return this;
    }

    public Inflection withKebabCase() {
        this.word = inflector.kebabCase(word);
        return this;
    }

    public Inflection asATitle() {
        this.word = inflector.titleCase(word);
        return this;
    }
}
