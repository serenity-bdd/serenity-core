package net.serenitybdd.screenplay.conditions;

import net.serenitybdd.markers.IsSilent;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

public class SilentPerformable implements Performable, IsSilent {
    @Override
    public <T extends Actor> void performAs(T actor) {}
}
