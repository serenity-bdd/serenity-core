package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Manage browser storage (localStorage and sessionStorage).
 */
public class ManageStorage {

    /**
     * Manage localStorage.
     */
    public static LocalStorageBuilder localStorage() {
        return new LocalStorageBuilder();
    }

    /**
     * Manage sessionStorage.
     */
    public static SessionStorageBuilder sessionStorage() {
        return new SessionStorageBuilder();
    }
}

/**
 * Builder for localStorage operations.
 */
class LocalStorageBuilder {

    /**
     * Set a value in localStorage.
     */
    public Performable setItem(String key, String value) {
        return new SetLocalStorageItem(key, value);
    }

    /**
     * Remove an item from localStorage.
     */
    public Performable removeItem(String key) {
        return new RemoveLocalStorageItem(key);
    }

    /**
     * Clear all items from localStorage.
     */
    public Performable clear() {
        return new ClearLocalStorage();
    }
}

/**
 * Builder for sessionStorage operations.
 */
class SessionStorageBuilder {

    /**
     * Set a value in sessionStorage.
     */
    public Performable setItem(String key, String value) {
        return new SetSessionStorageItem(key, value);
    }

    /**
     * Remove an item from sessionStorage.
     */
    public Performable removeItem(String key) {
        return new RemoveSessionStorageItem(key);
    }

    /**
     * Clear all items from sessionStorage.
     */
    public Performable clear() {
        return new ClearSessionStorage();
    }
}

class SetLocalStorageItem implements Performable {
    private final String key;
    private final String value;

    SetLocalStorageItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    @Step("{0} sets localStorage item '#key' to '#value'")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.evaluate("([k, v]) => localStorage.setItem(k, v)", new Object[]{key, value});
    }
}

class RemoveLocalStorageItem implements Performable {
    private final String key;

    RemoveLocalStorageItem(String key) {
        this.key = key;
    }

    @Override
    @Step("{0} removes localStorage item '#key'")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.evaluate("k => localStorage.removeItem(k)", key);
    }
}

class ClearLocalStorage implements Performable {
    @Override
    @Step("{0} clears localStorage")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.evaluate("localStorage.clear()");
    }
}

class SetSessionStorageItem implements Performable {
    private final String key;
    private final String value;

    SetSessionStorageItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    @Step("{0} sets sessionStorage item '#key' to '#value'")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.evaluate("([k, v]) => sessionStorage.setItem(k, v)", new Object[]{key, value});
    }
}

class RemoveSessionStorageItem implements Performable {
    private final String key;

    RemoveSessionStorageItem(String key) {
        this.key = key;
    }

    @Override
    @Step("{0} removes sessionStorage item '#key'")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.evaluate("k => sessionStorage.removeItem(k)", key);
    }
}

class ClearSessionStorage implements Performable {
    @Override
    @Step("{0} clears sessionStorage")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.evaluate("sessionStorage.clear()");
    }
}
