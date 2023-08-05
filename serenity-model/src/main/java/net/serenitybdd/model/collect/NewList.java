package net.serenitybdd.model.collect;

import java.util.*;

public class NewList {
    public static <T> List<T> of(T... elements) {
        List<T> list = new ArrayList<>();
        Arrays.stream(elements).forEach(list::add);
        return list;
    }

    public static <T> List<T> copyOf(Collection<? extends T> elements) {
        return new ArrayList<>(elements);
    }

    public static <T> List<T> copyOf(T... elements) {
        return Arrays.asList(elements);
    }

    public static <T> List<T> reverse(List<T> titleElements) {
        List<T> elements = new ArrayList<>(titleElements);
        Collections.reverse(elements);
        return elements;
    }
}
