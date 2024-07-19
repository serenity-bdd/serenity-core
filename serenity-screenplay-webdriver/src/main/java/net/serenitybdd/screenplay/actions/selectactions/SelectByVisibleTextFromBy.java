package net.serenitybdd.screenplay.actions.selectactions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.ByAction;
import net.serenitybdd.annotations.Step;
import org.openqa.selenium.By;

import java.util.Collections;
import java.util.List;

public class SelectByVisibleTextFromBy extends ByAction {
    private List<String> options;
    private String selectedOptions;

    public SelectByVisibleTextFromBy() {}

    public SelectByVisibleTextFromBy(String option, By... locators) {
        super(locators);
        this.options = Collections.singletonList(option);
        this.selectedOptions = option;
    }

    public SelectByVisibleTextFromBy(List<String> options, By... locators) {
        super(locators);
        this.options = options;
        this.selectedOptions = String.join(",", options);
    }

    @Step("{0} selects #selectedOptions")
    public <T extends Actor> void performAs(T theUser) {
        options.forEach(
            option -> BrowseTheWeb.as(theUser).find(locators).selectByVisibleText(option)
        );
    }
}
