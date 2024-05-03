package net.serenitybdd.screenplay.questions.page;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class TheWebPage {
    public static Question<String> title() {
        return Question.about("the title of the page").answeredBy(actor -> BrowseTheWeb.as(actor).getTitle());
    }

    public static Question<String> currentUrl() {
        return Question.about("the current URL").answeredBy(actor -> BrowseTheWeb.as(actor).getDriver().getCurrentUrl());
    }

    public static Question<String> alertText() {
        return Question.about("alert text").answeredBy(actor -> BrowseTheWeb.as(actor).getAlert().getText());
    }

    public static Question<String> source() {
        return Question.about("page source").answeredBy(actor -> BrowseTheWeb.as(actor).getDriver().getPageSource());
    }
}
