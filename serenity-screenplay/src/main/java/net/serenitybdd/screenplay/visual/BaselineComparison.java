package net.serenitybdd.screenplay.visual;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

/**
 * A Predicate that compares screenshot bytes against a baseline image.
 *
 * <p>Returns {@code true} if the screenshot matches the baseline within the configured threshold.
 * Throws {@link VisualComparisonFailure} if the difference exceeds the threshold.</p>
 *
 * <p>On first run (or when updating), the baseline is created from the provided screenshot bytes.</p>
 */
public class BaselineComparison implements Predicate<byte[]> {

    private static final String BASELINES_DIR = "src/test/resources/visual-baselines";
    private static final String ACTUAL_DIR = "target/visual-comparisons/actual";
    private static final String DIFF_DIR = "target/visual-comparisons/diff";

    private final String baselineName;
    private final double threshold;
    private final boolean updateBaseline;

    public BaselineComparison(String baselineName, double threshold, boolean updateBaseline) {
        this.baselineName = baselineName;
        this.threshold = threshold;
        this.updateBaseline = updateBaseline;
    }

    @Override
    public boolean test(byte[] screenshotBytes) {
        Path baselinePath = Paths.get(BASELINES_DIR, baselineName + ".png");
        Path actualPath = Paths.get(ACTUAL_DIR, baselineName + ".png");

        try {
            Files.createDirectories(baselinePath.getParent());
            Files.createDirectories(actualPath.getParent());
            Files.createDirectories(Paths.get(DIFF_DIR));

            Files.write(actualPath, screenshotBytes);

            if (updateBaseline || !Files.exists(baselinePath)) {
                Files.write(baselinePath, screenshotBytes);
                if (!updateBaseline) {
                    System.out.println("Created baseline: " + baselinePath);
                }
                return true;
            } else {
                BufferedImage baseline = ImageIO.read(baselinePath.toFile());
                BufferedImage actual = ImageIO.read(new ByteArrayInputStream(screenshotBytes));

                double diffPercentage = compareImages(baseline, actual, baselineName);

                if (diffPercentage > threshold) {
                    throw new VisualComparisonFailure(
                        String.format(
                            "Visual comparison failed for '%s'. Difference: %.2f%% (threshold: %.2f%%). See: %s",
                            baselineName,
                            diffPercentage * 100,
                            threshold * 100,
                            Paths.get(DIFF_DIR, baselineName + "-diff.png").toAbsolutePath()
                        )
                    );
                }
                return true;
            }
        } catch (VisualComparisonFailure e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process screenshot for visual comparison", e);
        }
    }

    private static double compareImages(BufferedImage baseline, BufferedImage actual, String name) throws IOException {
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
                    diff.setRGB(x, y, toGrayscale(baselineRgb));
                }
            }
        }

        // Save diff image
        Path diffPath = Paths.get(DIFF_DIR, name + "-diff.png");
        ImageIO.write(diff, "PNG", diffPath.toFile());

        return (double) diffPixels / totalPixels;
    }

    private static int toGrayscale(int rgb) {
        int a = (rgb >> 24) & 0xFF;
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int gray = (r + g + b) / 3;
        // Make it semi-transparent for diff visualization
        return ((a / 2) << 24) | (gray << 16) | (gray << 8) | gray;
    }
}
