package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.targets.Target;

public class ProfilePage extends PageObject {

    // Screenplay selectors
    public static Target NAME = Target.the("Name").locatedBy("#name");
    public static Target COUNTRY = Target.the("Country of residence").located(By.cssSelector("#country"));
    public static Target COLOR = Target.the("Favorite Color").locatedBy("#color");
    public static Target DATE_OF_BIRTH = Target.the("Date of Birth").locatedBy("#dob");
    public static Target CONTACT_PREFERENCES = Target.the("Contact Preferences").locatedBy("#contactPreferances");
    public static Target INVISIBLE = Target.the("Invisible field").locatedBy("#invisible");
    public static Target INEXISTANT = Target.the("Inexistant field").locatedBy("#inexistant");
    public static Target SOME_BUTTON = Target.the("Button").locatedBy("#button");

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
