package net.serenitybdd.screenplay.playwright.questions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.util.HashMap;
import java.util.Map;

/**
 * Questions about browser storage (localStorage and sessionStorage).
 */
public class StorageValue {

    /**
     * Get a value from localStorage.
     */
    public static Question<String> fromLocalStorage(String key) {
        return new LocalStorageValueQuestion(key);
    }

    /**
     * Get a value from sessionStorage.
     */
    public static Question<String> fromSessionStorage(String key) {
        return new SessionStorageValueQuestion(key);
    }

    /**
     * Get all items from localStorage as a Map.
     */
    public static Question<Map<String, String>> allLocalStorageItems() {
        return new AllLocalStorageItemsQuestion();
    }

    /**
     * Get all items from sessionStorage as a Map.
     */
    public static Question<Map<String, String>> allSessionStorageItems() {
        return new AllSessionStorageItemsQuestion();
    }
}

@Subject("localStorage value for '#key'")
class LocalStorageValueQuestion implements Question<String> {
    private final String key;

    LocalStorageValueQuestion(String key) {
        this.key = key;
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Object result = page.evaluate("k => localStorage.getItem(k)", key);
        return result == null ? null : result.toString();
    }
}

@Subject("sessionStorage value for '#key'")
class SessionStorageValueQuestion implements Question<String> {
    private final String key;

    SessionStorageValueQuestion(String key) {
        this.key = key;
    }

    @Override
    public String answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Object result = page.evaluate("k => sessionStorage.getItem(k)", key);
        return result == null ? null : result.toString();
    }
}

@Subject("all localStorage items")
class AllLocalStorageItemsQuestion implements Question<Map<String, String>> {
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Object result = page.evaluate("() => { " +
            "const items = {}; " +
            "for (let i = 0; i < localStorage.length; i++) { " +
            "  const key = localStorage.key(i); " +
            "  items[key] = localStorage.getItem(key); " +
            "} " +
            "return items; " +
            "}");
        return result == null ? new HashMap<>() : (Map<String, String>) result;
    }
}

@Subject("all sessionStorage items")
class AllSessionStorageItemsQuestion implements Question<Map<String, String>> {
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Object result = page.evaluate("() => { " +
            "const items = {}; " +
            "for (let i = 0; i < sessionStorage.length; i++) { " +
            "  const key = sessionStorage.key(i); " +
            "  items[key] = sessionStorage.getItem(key); " +
            "} " +
            "return items; " +
            "}");
        return result == null ? new HashMap<>() : (Map<String, String>) result;
    }
}
