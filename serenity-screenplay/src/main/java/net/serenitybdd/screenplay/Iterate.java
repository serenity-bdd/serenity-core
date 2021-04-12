package net.serenitybdd.screenplay;

import java.util.List;
import java.util.function.BiConsumer;

public class Iterate<T> {
    private final List<T> collection;

    public Iterate(List<T> collection) {
        this.collection = collection;
    }

    public static <T> Iterate<T> over(List<T> collection) {
        return new Iterate<>(collection);
    }

    public Performable forEach(BiConsumer<Actor, T> action) {
        return Task.where("{0} checks each entry in the collection:",
                actor -> {
                    collection.forEach(
                            item -> actor.attemptsTo(processItem(item, action)));
                }
        );
    }

    private Performable processItem(T item, BiConsumer<Actor, T> action) {
        return Task.where("For item " + item.toString(),
                actor -> action.accept(actor, item)
        );
    }
}
