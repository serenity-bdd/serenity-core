package net.serenitybdd.screenplay.ensure.visual;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.visual.AbstractScreenshotQuestion;
import net.serenitybdd.screenplay.visual.ImageWithScale;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;

import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
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
        Dimension size;
        if (target == null) {
            bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            size = driver.manage().window().getSize();
        } else {
            WebElement webElementFacade = target.resolveFor(actor);
            bytes = webElementFacade.getScreenshotAs(OutputType.BYTES);
            size = webElementFacade.getSize();
        }
        // Scale factor for high-DPI scenarios
        BufferedImage image = ImageWithScale.readImage(bytes);
        double scale = (double) image.getWidth() / size.width;
        return new ImageWithScale(image, scale);
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
