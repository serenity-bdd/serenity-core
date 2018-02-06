package net.serenitybdd.core.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NewSet {
    public static <E> Set<E> copyOf(E[] elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    public static <E> Set<E> copyOf(Collection<E> elements) {
        return new HashSet(elements);
    }

    public static <E> Set<E> of() {
        return new HashSet<>();
    }

    public static <E> Set<E> of(E element) {
        return new HashSet<>(Arrays.asList(element));
    }

    public static <E> Set<E> of(E... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

}