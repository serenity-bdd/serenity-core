package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;

public class ProfilePage extends PageObject {
    // Journey-pattern selectors
    public static Target NAME = Target.the("Name").locatedBy("#name");
    public static Target COUNTRY = Target.the("Country of residence").located(By.cssSelector("#country"));

    // Illulstrations of classic page object methods

    public void updateNameTo(String name) {
        $("#name").type(name);
    }

    public void updateCountryTo(String country) {
        $("#country").selectByVisibleText(country);
    }

}
