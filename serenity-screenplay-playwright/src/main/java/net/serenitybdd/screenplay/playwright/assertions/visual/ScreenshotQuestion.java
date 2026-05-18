package net.serenitybdd.screenplay.playwright.assertions.visual;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * A Question that takes a screenshot using Playwright and returns it as a byte array.
 */
public class ScreenshotQuestion implements Question<byte[]> {

    private final Target target;
    private final boolean fullPage;

    public ScreenshotQuestion(Target target, boolean fullPage) {
        this.target = target;
        this.fullPage = fullPage;
    }

    public static ScreenshotQuestion ofPage() {
        return new ScreenshotQuestion(null, true);
    }

    public static ScreenshotQuestion of(Target target) {
        return new ScreenshotQuestion(target, false);
    }

    @Override
    public byte[] answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        if (fullPage) {
            return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        } else {
            Locator locator = target.resolveFor(page);
            return locator.screenshot();
        }
    }
}
