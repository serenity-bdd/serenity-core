package serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Task;
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.actions.SelectFromOptions;
import serenitymodel.net.thucydides.core.annotations.Step;

import static serenityscreenplaywebdriver.net.serenitybdd.screenplay.webtests.pages.ProfilePage.COUNTRY;

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
