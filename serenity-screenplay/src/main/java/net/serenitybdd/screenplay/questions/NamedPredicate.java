package net.serenitybdd.screenplay.questions;

import java.util.function.Predicate;

public class NamedPredicate<T> implements Predicate<T>{
    private final String name;
    private final Predicate<T> predicate;

    public NamedPredicate(String name, Predicate<T> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    public String getName() {
        return name;
    }

    public Predicate<T> getPredicate() {
        return predicate;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean test(T test) {
        return predicate.test(test);
    }

    @Override
    public Predicate<T> and(Predicate<? super T> other) {
        return new NamedPredicate<T>(name, predicate.and(other));
    }

    @Override
    public Predicate<T> negate() {
        return new NamedPredicate<T>(name, predicate.negate());
    }

    @Override
    public Predicate<T> or(Predicate<? super T> other) {
        return new NamedPredicate<T>(name, predicate.or(other));
    }
}
