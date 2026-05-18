package net.serenitybdd.screenplay.visual;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for screenshot questions that supports masking elements.
 *
 * <p>Subclasses provide the driver-specific screenshot capture and mask resolution logic.
 * This class handles the common image manipulation for applying masks.</p>
 *
 * @param <T> the driver-specific target type used to locate elements
 */
public abstract class AbstractScreenshotQuestion<T> implements Question<ImageWithScale> {

    protected final T target;
    protected final List<T> masks = new ArrayList<>();
    protected Color maskColor = Color.BLACK;

    protected AbstractScreenshotQuestion(T target) {
        this.target = target;
    }

    /**
     * Set the color used to mask/redact elements.
     *
     * @param color The mask color (defaults to black)
     */
    public AbstractScreenshotQuestion<T> withColor(Color color) {
        this.maskColor = color;
        return this;
    }

    /**
     * Mask specific elements by selector strings.
     *
     * @param selectors CSS or XPath selectors of elements to mask
     */
    public AbstractScreenshotQuestion<T> mask(String... selectors) {
        for (String selector : selectors) {
            masks.add(createTarget(selector));
        }
        return this;
    }

    /**
     * Mask specific target elements.
     *
     * @param targets The targets to mask
     */
    @SuppressWarnings("unchecked")
    public AbstractScreenshotQuestion<T> mask(T... targets) {
        masks.addAll(Arrays.asList(targets));
        return this;
    }

    /**
     * Create a target from a CSS or XPath selector.
     */
    protected abstract T createTarget(String selector);

    /**
     * Take the raw screenshot bytes using the driver-specific API.
     *
     * @param actor the actor performing the action
     * @param target the target element to screenshot, or {@code null} for a full page screenshot
     */
    protected abstract ImageWithScale takeScreenshot(Actor actor, T target);

    /**
     * Resolve all mask targets into screen rectangles.
     */
    protected abstract List<Rectangle> resolveMaskRectangles(Actor actor);

    @Override
    public ImageWithScale answeredBy(Actor actor) {
        ImageWithScale screenshot = takeScreenshot(actor, target);

        List<Rectangle> maskRects = resolveMaskRectangles(actor);
        if (maskRects.isEmpty()) {
            return screenshot;
        }

        return applyMasks(screenshot, maskRects);
    }

    private ImageWithScale applyMasks(ImageWithScale screenshot, List<Rectangle> maskRects) {
        BufferedImage image = screenshot.getImage();
        Graphics2D g = image.createGraphics();
        g.setColor(maskColor);

        for (Rectangle rect : maskRects) {
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        g.dispose();
        return new ImageWithScale(image, screenshot.getScale());
    }
}
