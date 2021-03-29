package serenityscreenplay.net.serenitybdd.screenplay.conditions;

import serenitycore.net.serenitybdd.markers.IsSilent;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Performable;

public class SilentPerformable implements Performable, IsSilent {
    @Override
    public <T extends Actor> void performAs(T actor) {}
}
