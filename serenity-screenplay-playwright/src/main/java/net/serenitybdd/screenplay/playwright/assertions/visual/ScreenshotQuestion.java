package net.serenitybdd.screenplay.playwright.assertions.visual;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
import net.serenitybdd.screenplay.visual.AbstractScreenshotQuestion;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * A Question that takes a screenshot using Playwright and returns it as a byte array.
 * Supports masking specific elements to redact them from the screenshot.
 */
public class ScreenshotQuestion extends AbstractScreenshotQuestion<Target> {

    public ScreenshotQuestion(Target target) {
        super(target);
    }

    /**
     * @deprecated Use {@link #ScreenshotQuestion(Target)} instead. The fullPage parameter is ignored.
     */
    @Deprecated
    public ScreenshotQuestion(Target target, boolean fullPage) {
        super(target);
    }

    public static ScreenshotQuestion ofPage() {
        return new ScreenshotQuestion(null);
    }

    public static ScreenshotQuestion of(Target target) {
        return new ScreenshotQuestion(target);
    }

    @Override
    public ScreenshotQuestion mask(String... selectors) {
        super.mask(selectors);
        return this;
    }

    @Override
    public ScreenshotQuestion mask(Target... targets) {
        super.mask(targets);
        return this;
    }

    @Override
    public ScreenshotQuestion withColor(Color color) {
        super.withColor(color);
        return this;
    }

    @Override
    protected Target createTarget(String selector) {
        return Target.the(selector).locatedBy(selector);
    }

    @Override
    protected byte[] takeScreenshot(Actor actor, Target target) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        if (target == null) {
            return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        } else {
            Locator locator = target.resolveFor(page);
            return locator.screenshot();
        }
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
