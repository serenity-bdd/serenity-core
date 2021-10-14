package net.serenitybdd.screenplay.questions.page;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.Cookie;

import java.util.Set;

public class Cookies {
    public static Question<Set<Cookie>> cookies() {
        return Question.about("cookies").answeredBy(actor -> BrowseTheWeb.as(actor).getDriver().manage().getCookies());
    }

    public static Question<Cookie> cookieNamed(String name) {
        return Question.about("cookies").answeredBy(actor -> BrowseTheWeb.as(actor).getDriver().manage().getCookieNamed(name));
    }
}
