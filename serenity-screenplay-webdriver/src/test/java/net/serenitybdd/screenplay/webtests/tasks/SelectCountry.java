package net.serenitybdd.screenplay.webtests.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.SelectFromOptions;

import static net.serenitybdd.screenplay.webtests.pages.ProfilePage.COUNTRY;

public class SelectCountry implements Task {
  
    String code;

    public SelectCountry() {
    }

    private SelectCountry(String code) {
        this.code = code.toUpperCase();
    }
  
    public static SelectCountry withCountryCode(String code) {
        return new SelectCountry(code);
    }

    @Step("{0} selects #code from #COUNTRY")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(SelectFromOptions.byValue(code).from(COUNTRY));
    }

}
