package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.ByAction;
import net.serenitybdd.annotations.Step;
import org.openqa.selenium.By;

import java.util.Collections;
import java.util.List;

public class SelectByValueFromBy extends ByAction {
    private List<String> values;

    public SelectByValueFromBy() {}

    public SelectByValueFromBy(String value, By... locators) {
        super(locators);
        this.values = Collections.singletonList(value);
    }

    public SelectByValueFromBy(List<String> values, By... locators) {
        super(locators);
        this.values = values;
    }

    @Step("{0} selects #value in #locators")
    public <T extends Actor> void performAs(T theUser) {
        values.forEach(
                value -> BrowseTheWeb.as(theUser).find(locators).selectByValue(value)
        );
    }
}
