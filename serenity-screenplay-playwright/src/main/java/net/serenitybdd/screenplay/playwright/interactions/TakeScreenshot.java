package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Capture screenshots of the page or specific elements.
 */
public class TakeScreenshot {

    /**
     * Take a screenshot of the full page and save to the specified path.
     */
    public static PageScreenshotBuilder ofPage() {
        return new PageScreenshotBuilder();
    }

    /**
     * Take a screenshot of a specific element.
     */
    public static ElementScreenshotBuilder ofElement(String selector) {
        return new ElementScreenshotBuilder(selector);
    }
}

/**
 * Builder for page screenshots.
 */
class PageScreenshotBuilder {
    private boolean fullPage = false;

    /**
     * Capture the full scrollable page.
     */
    public PageScreenshotBuilder fullPage() {
        this.fullPage = true;
        return this;
    }

    /**
     * Save the screenshot to the specified path.
     */
    public Performable saveTo(String path) {
        return new TakePageScreenshot(Paths.get(path), fullPage);
    }

    /**
     * Save the screenshot to the specified path.
     */
    public Performable saveTo(Path path) {
        return new TakePageScreenshot(path, fullPage);
    }
}

/**
 * Builder for element screenshots.
 */
class ElementScreenshotBuilder {
    private final String selector;

    ElementScreenshotBuilder(String selector) {
        this.selector = selector;
    }

    /**
     * Save the screenshot to the specified path.
     */
    public Performable saveTo(String path) {
        return new TakeElementScreenshot(selector, Paths.get(path));
    }

    /**
     * Save the screenshot to the specified path.
     */
    public Performable saveTo(Path path) {
        return new TakeElementScreenshot(selector, path);
    }
}

class TakePageScreenshot implements Performable {
    private final Path path;
    private final boolean fullPage;

    TakePageScreenshot(Path path, boolean fullPage) {
        this.path = path;
        this.fullPage = fullPage;
    }

    @Override
    @Step("{0} takes a screenshot of the page")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(path)
            .setFullPage(fullPage));
    }
}

class TakeElementScreenshot implements Performable {
    private final String selector;
    private final Path path;

    TakeElementScreenshot(String selector, Path path) {
        this.selector = selector;
        this.path = path;
    }

    @Override
    @Step("{0} takes a screenshot of #selector")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Locator element = page.locator(selector);
        element.screenshot(new Locator.ScreenshotOptions().setPath(path));
    }
}
