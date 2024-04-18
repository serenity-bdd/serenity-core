package net.serenitybdd.screenplay.rest.interactions;

import io.restassured.response.ValidatableResponse;
import net.serenitybdd.markers.CanBeSilent;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.annotations.Step;

import java.util.function.Consumer;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Ensure implements Interaction, CanBeSilent {

    private final String description;
    private final Consumer<ValidatableResponse> check;
    private final boolean isSilent;

    public Ensure(String description, Consumer<ValidatableResponse> check, boolean isSilent) {
        this.description = description;
        this.check = check;
        this.isSilent = isSilent;
    }

    @Step("Ensure that #description")
    @Override
    public <T extends Actor> void performAs(T actor) {
        check.accept(SerenityRest.then());
    }

    public static Ensure that(String description, Consumer<ValidatableResponse> check) {
        return instrumented(Ensure.class, description, check, false);
    }

    public static Ensure silentlyThat(Consumer<ValidatableResponse> check) {
        return instrumented(Ensure.class, "", check, true);
    }

    @Override
    public boolean isSilent() {
        return isSilent;
    }
}
