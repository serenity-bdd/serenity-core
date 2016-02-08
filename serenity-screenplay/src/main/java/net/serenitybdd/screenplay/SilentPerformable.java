package net.serenitybdd.screenplay;

public class SilentPerformable implements Performable {
    @Override
    public <T extends Actor> void performAs(T actor) {}
}
