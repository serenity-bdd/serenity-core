package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.screenplay.targets.Target;

public class ProfilePage {
    public static Target NAME = Target.the("Name").locatedBy("#name");
    public static Target COUNTRY = Target.the("Country of residence").locatedBy("#country");
}
