package net.serenitybdd.screenplay.ensure.visual;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.visual.BaselineComparison;

/**
 * Perform visual regression testing by comparing screenshots to baseline images
 * using WebDriver.
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
     * Compare a screenshot of a specific element identified by a CSS or XPath selector.
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
            byte[] screenshotBytes = new WebDriverScreenshotQuestion(target).answeredBy(actor);
            BaselineComparison comparison = new BaselineComparison(baselineName).withThreshold(threshold);
            if (updateBaseline) {
                comparison.updatingBaseline();
            }
            comparison.test(screenshotBytes);
        }
    }
}
