package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Switch between browser pages (tabs) or open new ones.
 */
public class SwitchTo {

    /**
     * Open a new browser page (tab).
     */
    public static Performable newPage() {
        return new SwitchToNewPage();
    }

    /**
     * Open a new page and navigate to the given URL.
     */
    public static Performable newPageAt(String url) {
        return new SwitchToNewPageWithUrl(url);
    }

    /**
     * Switch to the page at the given index (0-based).
     */
    public static Performable pageNumber(int index) {
        return new SwitchToPageByIndex(index);
    }

    /**
     * Switch to the page with the given title (partial match).
     */
    public static Performable pageWithTitle(String title) {
        return new SwitchToPageByTitle(title);
    }

    /**
     * Switch to the page with the given URL (partial match).
     */
    public static Performable pageWithUrl(String url) {
        return new SwitchToPageByUrl(url);
    }

    /**
     * Close the current page and switch to the previous one.
     */
    public static Performable previousPageAfterClosingCurrent() {
        return new CloseCurrentAndSwitchBack();
    }
}

/**
 * Opens a new browser page (tab).
 */
class SwitchToNewPage implements Performable {
    @Override
    @Step("{0} opens a new browser page")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        ability.openNewPage();
        ability.notifyScreenChange();
    }
}

/**
 * Opens a new browser page and navigates to a URL.
 */
class SwitchToNewPageWithUrl implements Performable {
    private final String url;

    SwitchToNewPageWithUrl(String url) {
        this.url = url;
    }

    @Override
    @Step("{0} opens a new browser page at #url")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        Page newPage = ability.openNewPage();
        newPage.navigate(url);
        ability.notifyScreenChange();
    }
}

/**
 * Switches to a page by index.
 */
class SwitchToPageByIndex implements Performable {
    private final int index;

    SwitchToPageByIndex(int index) {
        this.index = index;
    }

    @Override
    @Step("{0} switches to page number #index")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        ability.switchToPage(index);
        ability.notifyScreenChange();
    }
}

/**
 * Switches to a page by title.
 */
class SwitchToPageByTitle implements Performable {
    private final String title;

    SwitchToPageByTitle(String title) {
        this.title = title;
    }

    @Override
    @Step("{0} switches to page with title containing '#title'")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        ability.switchToPageWithTitle(title);
        ability.notifyScreenChange();
    }
}

/**
 * Switches to a page by URL.
 */
class SwitchToPageByUrl implements Performable {
    private final String url;

    SwitchToPageByUrl(String url) {
        this.url = url;
    }

    @Override
    @Step("{0} switches to page with URL containing '#url'")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        ability.switchToPageWithUrl(url);
        ability.notifyScreenChange();
    }
}

/**
 * Closes the current page and switches to the previous one.
 */
class CloseCurrentAndSwitchBack implements Performable {
    @Override
    @Step("{0} closes the current page and switches back")
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright ability = BrowseTheWebWithPlaywright.as(actor);
        ability.closeCurrentPage();
        ability.notifyScreenChange();
    }
}
