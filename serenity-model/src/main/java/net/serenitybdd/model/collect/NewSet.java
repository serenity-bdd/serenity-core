package net.serenitybdd.model.collect;

import java.util.*;

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
        return new HashSet<>(Collections.singletonList(element));
    }

    public static <E> Set<E> of(E... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

}
