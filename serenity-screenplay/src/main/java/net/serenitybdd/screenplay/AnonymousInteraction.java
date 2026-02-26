package net.serenitybdd.screenplay;

import net.serenitybdd.markers.CanBeSilent;

import java.util.List;

public class AnonymousInteraction extends AnonymousPerformable implements Interaction, CanBeSilent {

    private boolean isSilent = false;

    public AnonymousInteraction(){}

    public AnonymousInteraction(String title, List<Performable> steps) {
        super(title, steps);
    }

    public AnonymousPerformableFieldSetter<AnonymousInteraction> with(String fieldName) {
        return new AnonymousPerformableFieldSetter<>(this, fieldName);
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }

    public AnonymousInteraction withNoReporting() {
        this.isSilent = true;
        return this;
    }
}
