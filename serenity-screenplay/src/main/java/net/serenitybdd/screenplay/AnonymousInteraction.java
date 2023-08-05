package net.serenitybdd.screenplay;

import java.util.List;

public class AnonymousInteraction extends AnonymousPerformable implements Interaction {
    public AnonymousInteraction(String title, List<Performable> steps) {
        super(title, steps);
    }

    public AnonymousPerformableFieldSetter<AnonymousInteraction> with(String fieldName) {
        return new AnonymousPerformableFieldSetter<>(this, fieldName);
    }
}
