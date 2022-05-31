package net.serenitybdd.screenplay.actions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class HtmlAlert {

    /**
     * Retrieve the text of the current alert window
     */
    public static Question<String> text() {
        return actor -> BrowseTheWeb.as(actor).getDriver().switchTo().alert().getText();
    }
}
