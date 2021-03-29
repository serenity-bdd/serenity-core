package serenityscreenplayrest.net.serenitybdd.screenplay.rest.interactions;

import io.restassured.response.ValidatableResponse;
import net.serenitybdd.rest.SerenityRest;
import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Interaction;
import serenitymodel.net.thucydides.core.annotations.Step;

import java.util.function.Consumer;

import static serenityscreenplay.net.serenitybdd.screenplay.Tasks.instrumented;

public class Ensure implements Interaction {

    private final String description;
    private final Consumer<ValidatableResponse> check;

    public Ensure(String description, Consumer<ValidatableResponse> check) {
        this.description = description;
        this.check = check;
    }

    @Step("Ensure that #description")
    @Override
    public <T extends Actor> void performAs(T actor) {
        check.accept(SerenityRest.then());
    }

    public static Ensure that(String description, Consumer<ValidatableResponse> check) {
        return instrumented(Ensure.class, description, check);
    }
}
