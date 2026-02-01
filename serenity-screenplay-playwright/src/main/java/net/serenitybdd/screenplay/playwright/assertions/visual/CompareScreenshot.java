package net.serenitybdd.screenplay.playwright.assertions.visual;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Perform visual regression testing by comparing screenshots to baseline images.
 *
 * <p>On first run, baseline images are created. On subsequent runs, screenshots
 * are compared against the baselines and differences are reported.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Compare full page screenshot
 *     actor.attemptsTo(CompareScreenshot.ofPage().toBaseline("homepage"));
 *
 *     // Compare element screenshot
 *     actor.attemptsTo(CompareScreenshot.of("#header").toBaseline("header-component"));
 *     actor.attemptsTo(CompareScreenshot.of(NAVIGATION).toBaseline("nav-bar"));
 *
 *     // With custom threshold (0.0 = exact match, 1.0 = any difference allowed)
 *     actor.attemptsTo(
 *         CompareScreenshot.ofPage()
 *             .toBaseline("dashboard")
 *             .withThreshold(0.1)  // Allow 10% difference
 *     );
 * </pre>
 */
public class CompareScreenshot {

    private final Target target;
    private final boolean fullPage;

    private CompareScreenshot(Target target, boolean fullPage) {
        this.target = target;
        this.fullPage = fullPage;
    }

    /**
     * Compare a full page screenshot.
     */
    public static CompareScreenshot ofPage() {
        return new CompareScreenshot(null, true);
    }

    /**
     * Compare a screenshot of a specific element.
     *
     * @param selector The CSS or XPath selector
     */
    public static CompareScreenshot of(String selector) {
        return new CompareScreenshot(Target.the(selector).locatedBy(selector), false);
    }

    /**
     * Compare a screenshot of a specific Target element.
     *
     * @param target The Target element
     */
    public static CompareScreenshot of(Target target) {
        return new CompareScreenshot(target, false);
    }

    /**
     * Specify the baseline name for comparison.
     *
     * @param baselineName The name for the baseline image (without extension)
     */
    public ScreenshotComparison toBaseline(String baselineName) {
        return new ScreenshotComparison(target, fullPage, baselineName);
    }

    /**
     * The actual screenshot comparison implementation.
     */
    public static class ScreenshotComparison implements Performable {
        private static final String BASELINES_DIR = "src/test/resources/visual-baselines";
        private static final String ACTUAL_DIR = "target/visual-comparisons/actual";
        private static final String DIFF_DIR = "target/visual-comparisons/diff";

        private final Target target;
        private final boolean fullPage;
        private final String baselineName;
        private double threshold = 0.0;
        private boolean updateBaseline = false;

        ScreenshotComparison(Target target, boolean fullPage, String baselineName) {
            this.target = target;
            this.fullPage = fullPage;
            this.baselineName = baselineName;
        }

        /**
         * Set the allowed difference threshold.
         *
         * @param threshold Value between 0.0 (exact match) and 1.0 (any difference allowed)
         */
        public ScreenshotComparison withThreshold(double threshold) {
            this.threshold = threshold;
            return this;
        }

        /**
         * Force update of the baseline image instead of comparing.
         */
        public ScreenshotComparison updatingBaseline() {
            this.updateBaseline = true;
            return this;
        }

        @Override
        @Step("{0} compares screenshot to baseline '#baselineName'")
        public <T extends Actor> void performAs(T actor) {
            Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

            // Take screenshot
            byte[] screenshotBytes;
            if (fullPage) {
                screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            } else {
                Locator locator = target.resolveFor(page);
                screenshotBytes = locator.screenshot();
            }

            Path baselinePath = Paths.get(BASELINES_DIR, baselineName + ".png");
            Path actualPath = Paths.get(ACTUAL_DIR, baselineName + ".png");

            try {
                // Ensure directories exist
                Files.createDirectories(baselinePath.getParent());
                Files.createDirectories(actualPath.getParent());
                Files.createDirectories(Paths.get(DIFF_DIR));

                // Save actual screenshot
                Files.write(actualPath, screenshotBytes);

                if (updateBaseline || !Files.exists(baselinePath)) {
                    // Create/update baseline
                    Files.write(baselinePath, screenshotBytes);
                    if (!updateBaseline) {
                        System.out.println("Created baseline: " + baselinePath);
                    }
                } else {
                    // Compare to baseline
                    BufferedImage baseline = ImageIO.read(baselinePath.toFile());
                    BufferedImage actual = ImageIO.read(actualPath.toFile());

                    double diffPercentage = compareImages(baseline, actual, baselineName);

                    if (diffPercentage > threshold) {
                        throw new VisualComparisonFailure(
                            String.format(
                                "Visual comparison failed for '%s'. Difference: %.2f%% (threshold: %.2f%%). " +
                                "See: %s",
                                baselineName,
                                diffPercentage * 100,
                                threshold * 100,
                                Paths.get(DIFF_DIR, baselineName + "-diff.png").toAbsolutePath()
                            )
                        );
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to process screenshot for visual comparison", e);
            }
        }

        private double compareImages(BufferedImage baseline, BufferedImage actual, String name) throws IOException {
            int width = Math.max(baseline.getWidth(), actual.getWidth());
            int height = Math.max(baseline.getHeight(), actual.getHeight());

            BufferedImage diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int diffPixels = 0;
            int totalPixels = width * height;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int baselineRgb = (x < baseline.getWidth() && y < baseline.getHeight())
                        ? baseline.getRGB(x, y) : 0;
                    int actualRgb = (x < actual.getWidth() && y < actual.getHeight())
                        ? actual.getRGB(x, y) : 0;

                    if (baselineRgb != actualRgb) {
                        diffPixels++;
                        diff.setRGB(x, y, 0xFFFF0000); // Red for differences
                    } else {
                        // Dimmed original for context
                        int gray = toGrayscale(baselineRgb);
                        diff.setRGB(x, y, gray);
                    }
                }
            }

            // Save diff image
            Path diffPath = Paths.get(DIFF_DIR, name + "-diff.png");
            ImageIO.write(diff, "PNG", diffPath.toFile());

            return (double) diffPixels / totalPixels;
        }

        private int toGrayscale(int rgb) {
            int a = (rgb >> 24) & 0xFF;
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            int gray = (r + g + b) / 3;
            // Make it semi-transparent for diff visualization
            return ((a / 2) << 24) | (gray << 16) | (gray << 8) | gray;
        }
    }
}
