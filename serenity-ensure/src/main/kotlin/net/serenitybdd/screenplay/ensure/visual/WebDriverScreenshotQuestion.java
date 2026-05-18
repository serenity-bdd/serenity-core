package net.serenitybdd.screenplay.ensure.visual;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.visual.AbstractScreenshotQuestion;
import net.serenitybdd.screenplay.visual.ImageWithScale;
import org.openqa.selenium.*;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * A Question that takes a screenshot using WebDriver and returns it as an ImageWithScale.
 * Supports masking specific elements to redact them from the screenshot.
 */
public class WebDriverScreenshotQuestion extends AbstractScreenshotQuestion<Target> {

    public WebDriverScreenshotQuestion(Target target) {
        super(target);
    }

    public static WebDriverScreenshotQuestion ofPage() {
        return new WebDriverScreenshotQuestion(null);
    }

    public static WebDriverScreenshotQuestion of(Target target) {
        return new WebDriverScreenshotQuestion(target);
    }

    @Override
    public WebDriverScreenshotQuestion mask(String... selectors) {
        super.mask(selectors);
        return this;
    }

    @Override
    public WebDriverScreenshotQuestion mask(Target... targets) {
        super.mask(targets);
        return this;
    }

    @Override
    public WebDriverScreenshotQuestion withColor(Color color) {
        super.withColor(color);
        return this;
    }

    @Override
    protected Target createTarget(String selector) {
        return Target.the(selector).locatedBy(selector);
    }

    @Override
    protected ImageWithScale takeScreenshot(Actor actor, Target target) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        byte[] bytes;
        if (target == null) {
            bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } else {
            bytes = target.resolveFor(actor).getScreenshotAs(OutputType.BYTES);
        }
        return new ImageWithScale(bytes, 1.0);
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
