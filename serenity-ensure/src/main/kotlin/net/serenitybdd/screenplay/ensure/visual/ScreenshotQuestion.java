package net.serenitybdd.screenplay.ensure.visual;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.visual.AbstractScreenshotQuestion;
import org.openqa.selenium.*;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * A Question that takes a screenshot using WebDriver and returns it as a byte array.
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
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        if (target == null) {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } else {
            return target.resolveFor(actor).getScreenshotAs(OutputType.BYTES);
        }
    }

    @Override
    protected List<Rectangle> resolveMaskRectangles(Actor actor) {
        List<Rectangle> rects = new ArrayList<>();
        for (Target mask : masks) {
            WebElement element = mask.resolveFor(actor);
            org.openqa.selenium.Rectangle r = element.getRect();
            rects.add(new Rectangle(r.x, r.y, r.width, r.height));
        }
        return rects;
    }
}
