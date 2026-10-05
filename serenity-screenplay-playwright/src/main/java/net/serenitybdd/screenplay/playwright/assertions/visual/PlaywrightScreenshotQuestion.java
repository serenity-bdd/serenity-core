package net.serenitybdd.screenplay.playwright.assertions.visual;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.visual.AbstractScreenshotQuestion;
import net.serenitybdd.screenplay.visual.ImageWithScale;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * A Question that takes a screenshot using Playwright and returns it as an ImageWithScale.
 * Supports masking specific elements to redact them from the screenshot.
 */
public class PlaywrightScreenshotQuestion extends AbstractScreenshotQuestion<Target> {

    public PlaywrightScreenshotQuestion(Target target) {
        super(target);
    }

    public static PlaywrightScreenshotQuestion ofPage() {
        return new PlaywrightScreenshotQuestion(null);
    }

    public static PlaywrightScreenshotQuestion of(Target target) {
        return new PlaywrightScreenshotQuestion(target);
    }

    @Override
    public PlaywrightScreenshotQuestion mask(String... selectors) {
        super.mask(selectors);
        return this;
    }

    @Override
    public PlaywrightScreenshotQuestion mask(Target... targets) {
        super.mask(targets);
        return this;
    }

    @Override
    public PlaywrightScreenshotQuestion withColor(Color color) {
        super.withColor(color);
        return this;
    }

    @Override
    protected Target createTarget(String selector) {
        return Target.the(selector).locatedBy(selector);
    }

    @Override
    protected ImageWithScale takeScreenshot(Actor actor, Target target) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        byte[] bytes;
        if (target == null) {
            bytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        } else {
            Locator locator = target.resolveFor(page);
            bytes = locator.screenshot();
        }
        double scale = page.evaluate("window.devicePixelRatio").toString().isEmpty()
            ? 1.0 : ((Number) page.evaluate("window.devicePixelRatio")).doubleValue();
        return new ImageWithScale(bytes, scale);
    }

    @Override
    protected List<Rectangle> resolveMaskRectangles(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        List<Rectangle> rects = new ArrayList<>();
        for (Target mask : masks) {
            Locator maskLocator = mask.resolveFor(page);
            var box = maskLocator.boundingBox();
            if (box != null) {
                rects.add(new Rectangle((int) box.x, (int) box.y, (int) box.width, (int) box.height));
            }
        }
        return rects;
    }
}
