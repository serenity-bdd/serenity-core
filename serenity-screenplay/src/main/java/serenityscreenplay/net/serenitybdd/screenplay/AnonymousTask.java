package serenityscreenplay.net.serenitybdd.screenplay;

import serenitycore.net.serenitybdd.markers.CanBeSilent;

import java.util.List;

public class AnonymousTask extends AnonymousPerformable implements Task, CanBeSilent {

    private boolean isSilent = false;

    public AnonymousTask(String title, List<Performable> steps) {
        super(title, steps);
    }

    public AnonymousPerformableFieldSetter<AnonymousTask> with(String fieldName) {
        return new AnonymousPerformableFieldSetter<>(this, fieldName);
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    public AnonymousTask withNoReporting() {
        this.isSilent = true;
        return this;
    }
}
