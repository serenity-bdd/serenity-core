package net.serenitybdd.screenplay.playwright.ui;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Factory for locating image elements using Playwright selectors.
 *
 * <p>Uses Playwright's role selectors and attribute matching for image location.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     Target logo = Image.withAltText("Company Logo");
 *     Target avatar = Image.withSrc("/images/avatar.png");
 *     Target productImage = Image.withSrcContaining("product-");
 * </pre>
 */
public class Image {

    private Image() {
        // Factory class - prevent instantiation
    }

    /**
     * Locate an image by its alt text (case-insensitive).
     * Uses Playwright's role selector for semantic image matching.
     *
     * @param alt The image's alt text
     * @return A Target for the image element
     */
    public static Target withAltText(String alt) {
        return Target.the("image with alt '" + alt + "'")
                .locatedBy("role=img[name=\"" + alt + "\" i]");
    }

    /**
     * Locate an image by its exact src attribute.
     *
     * @param src The exact src URL
     * @return A Target for the image element
     */
    public static Target withSrc(String src) {
        return Target.the("image with src '" + src + "'")
                .locatedBy("img[src=\"" + src + "\"]");
    }

    /**
     * Locate an image whose src contains the specified substring.
     *
     * @param part The substring to search for in the src attribute
     * @return A Target for the image element
     */
    public static Target withSrcContaining(String part) {
        return Target.the("image with src containing '" + part + "'")
                .locatedBy("img[src*=\"" + part + "\"]");
    }

    /**
     * Locate an image using a custom Playwright selector.
     *
     * @param selector A valid Playwright selector
     * @return A Target for the image element
     */
    public static Target locatedBy(String selector) {
        return Target.the("image").locatedBy(selector);
    }
}
