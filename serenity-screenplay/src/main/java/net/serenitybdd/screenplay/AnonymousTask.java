package net.serenitybdd.screenplay;

import java.util.List;

public class AnonymousTask extends AnonymousPerformable implements Task {
    public AnonymousTask(String title, List<Performable> steps) {
        super(title, steps);
    }

    public AnonymousPerformableFieldSetter<AnonymousTask> with(String fieldName) {
        return new AnonymousPerformableFieldSetter<>(this, fieldName);
    }


}
