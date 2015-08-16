package net.serenitybdd.screenplay.webtests.pages;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.DefaultUrl;

@DefaultUrl("classpath:sample-web-site/index.html")
public class ProfilePage extends PageObject {

    public enum ProfileField {
        Name("#name");

        public final String xpathOrCssSelector;

        ProfileField(String xpathOrCssSelector) {
            this.xpathOrCssSelector = xpathOrCssSelector;
        }
    }

    public static String the(ProfilePage.ProfileField field) {
        return field.xpathOrCssSelector;
    }

    public String fieldValueFor(ProfileField field) {
        return $(field.xpathOrCssSelector).getValue();
    }
}
