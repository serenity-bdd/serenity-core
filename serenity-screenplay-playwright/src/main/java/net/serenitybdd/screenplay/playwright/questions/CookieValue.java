package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.options.Cookie;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.List;
import java.util.Optional;

/**
 * Questions about browser cookies.
 */
public class CookieValue {

    /**
     * Get the value of a cookie by name.
     */
    public static Question<String> of(String cookieName) {
        return new CookieValueQuestion(cookieName);
    }

    /**
     * Get all cookies.
     */
    public static Question<List<Cookie>> allCookies() {
        return new AllCookiesQuestion();
    }

    /**
     * Check if a cookie exists.
     */
    public static Question<Boolean> exists(String cookieName) {
        return new CookieExistsQuestion(cookieName);
    }
}

@Subject("the value of cookie '#cookieName'")
class CookieValueQuestion implements Question<String> {
    private final String cookieName;

    CookieValueQuestion(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    public String answeredBy(Actor actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        List<Cookie> cookies = ability.getCookies();

        return cookies.stream()
            .filter(c -> c.name.equals(cookieName))
            .findFirst()
            .map(c -> c.value)
            .orElse(null);
    }
}

@Subject("all cookies")
class AllCookiesQuestion implements Question<List<Cookie>> {
    @Override
    public List<Cookie> answeredBy(Actor actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        return ability.getCookies();
    }
}

@Subject("whether cookie '#cookieName' exists")
class CookieExistsQuestion implements Question<Boolean> {
    private final String cookieName;

    CookieExistsQuestion(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        return ability.getCookies().stream()
            .anyMatch(c -> c.name.equals(cookieName));
    }
}
