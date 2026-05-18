package net.serenitybdd.screenplay.visual;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * A Predicate that compares screenshot bytes against a baseline image.
 *
 * <p>Returns {@code true} if the screenshot matches the baseline within the configured threshold.
 * Throws {@link VisualComparisonFailure} if the difference exceeds the threshold.</p>
 *
 * <p>On first run (or when updating), the baseline is created from the provided screenshot bytes.</p>
 */
public class BaselineComparison implements Predicate<ImageWithScale> {

    private SnapshotFileNamer fileNamer;
    private double threshold = 0.0;
    private boolean updateBaseline = false;

    public BaselineComparison(String baselineName) {
        this(new DefaultSnapshotFileNamer(baselineName));
    }

    public BaselineComparison(SnapshotFileNamer fileNamer) {
        this.fileNamer = fileNamer;
    }

    public BaselineComparison withThreshold(double threshold) {
        this.threshold = threshold;
        return this;
    }

    public BaselineComparison updatingBaseline() {
        this.updateBaseline = true;
        return this;
    }

    @Override
    public boolean test(ImageWithScale screenshot) {
        try {
            String description = screenshot.description();
            Path baselinePath = fileNamer.baselinePath(description, "png");
            Path actualPath = fileNamer.actualPath(description, "png");
            Path diffPath = fileNamer.diffPath(description, "png");

            Files.createDirectories(baselinePath.getParent());
            Files.createDirectories(actualPath.getParent());
            Files.createDirectories(diffPath.getParent());

            ImageIO.write(screenshot.getImage(), "PNG", actualPath.toFile());

            if (updateBaseline || !Files.exists(baselinePath)) {
                ImageIO.write(screenshot.getImage(), "PNG", baselinePath.toFile());
                if (!updateBaseline) {
                    System.out.println("Created baseline: " + baselinePath);
                }
                return true;
            } else {
                BufferedImage baseline = ImageIO.read(baselinePath.toFile());

                double diffPercentage = compareImages(baseline, screenshot.getImage(), diffPath);

                if (diffPercentage > threshold) {
                    throw new VisualComparisonFailure(
                        String.format(
                            "Visual comparison failed for '%s'. Difference: %.2f%% (threshold: %.2f%%). See: %s",
                            fileNamer.baselineName(description),
                            diffPercentage * 100,
                            threshold * 100,
                            diffPath.toAbsolutePath()
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

    private double compareImages(BufferedImage baseline, BufferedImage actual, Path diffPath) throws IOException {
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
        Files.createDirectories(diffPath.getParent());
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
