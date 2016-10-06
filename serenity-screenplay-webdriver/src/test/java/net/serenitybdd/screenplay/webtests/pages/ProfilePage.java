package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;

public class ProfilePage extends PageObject {
    // Journey-pattern selectors
    public static Target NAME = Target.the("Name").locatedBy("#name");
    public static Target COUNTRY = Target.the("Country of residence").located(By.cssSelector("#country"));
    public static Target COLOR = Target.the("Favorite Color").locatedBy("#color");
    public static Target DATE_OF_BIRTH = Target.the("Date of Birth").locatedBy("#dob");

    @FindBy(id="name")
    public WebElementFacade nameField;

    // Illulstrations of classic page object methods

    public void updateNameTo(String name) {
        $("#name").type(name);
    }

    public void updateCountryTo(String country) {
        $("#country").selectByVisibleText(country);
    }

}
