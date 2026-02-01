package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Margin;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generate a PDF document from the current page.
 *
 * <p>Note: PDF generation is only supported in Chromium headless mode.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Generate PDF with default settings
 *     actor.attemptsTo(GeneratePDF.andSaveAs("report.pdf"));
 *
 *     // Generate PDF with custom path
 *     actor.attemptsTo(GeneratePDF.andSaveTo(Paths.get("output/invoice.pdf")));
 *
 *     // Generate PDF with options
 *     actor.attemptsTo(
 *         GeneratePDF.andSaveAs("document.pdf")
 *             .withFormat("A4")
 *             .withMargins("1cm", "1cm", "1cm", "1cm")
 *             .inLandscape()
 *             .withHeaderTemplate("&lt;div style='font-size:10px'&gt;Header&lt;/div&gt;")
 *             .withFooterTemplate("&lt;div style='font-size:10px'&gt;Page &lt;span class='pageNumber'&gt;&lt;/span&gt;&lt;/div&gt;")
 *     );
 *
 *     // Print background graphics
 *     actor.attemptsTo(
 *         GeneratePDF.andSaveAs("styled-report.pdf")
 *             .withBackground()
 *     );
 * </pre>
 *
 * @see <a href="https://playwright.dev/java/docs/api/class-page#page-pdf">Playwright PDF API</a>
 */
public class GeneratePDF implements Performable {

    private static final String DEFAULT_OUTPUT_DIR = "target/pdfs";

    private final Path outputPath;
    private String format;
    private String width;
    private String height;
    private String marginTop;
    private String marginBottom;
    private String marginLeft;
    private String marginRight;
    private Boolean landscape;
    private Boolean printBackground;
    private Boolean displayHeaderFooter;
    private String headerTemplate;
    private String footerTemplate;
    private String pageRanges;
    private Double scale;

    private GeneratePDF(Path outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * Generate PDF and save with the specified filename in the default output directory.
     *
     * @param filename The filename (e.g., "report.pdf")
     */
    public static GeneratePDF andSaveAs(String filename) {
        return new GeneratePDF(Paths.get(DEFAULT_OUTPUT_DIR, filename));
    }

    /**
     * Generate PDF and save to the specified path.
     *
     * @param path The full path for the PDF file
     */
    public static GeneratePDF andSaveTo(Path path) {
        return new GeneratePDF(path);
    }

    /**
     * Generate PDF and save to the specified path.
     *
     * @param path The full path for the PDF file
     */
    public static GeneratePDF andSaveTo(String path) {
        return new GeneratePDF(Paths.get(path));
    }

    /**
     * Set the paper format (e.g., "Letter", "A4", "A3", "Legal", "Tabloid").
     *
     * @param format The paper format
     */
    public GeneratePDF withFormat(String format) {
        this.format = format;
        return this;
    }

    /**
     * Set custom paper dimensions.
     *
     * @param width Paper width (e.g., "8.5in", "21cm")
     * @param height Paper height (e.g., "11in", "29.7cm")
     */
    public GeneratePDF withDimensions(String width, String height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Set all margins to the same value.
     *
     * @param margin Margin value (e.g., "1cm", "0.5in")
     */
    public GeneratePDF withMargin(String margin) {
        this.marginTop = margin;
        this.marginBottom = margin;
        this.marginLeft = margin;
        this.marginRight = margin;
        return this;
    }

    /**
     * Set individual margins.
     *
     * @param top Top margin
     * @param right Right margin
     * @param bottom Bottom margin
     * @param left Left margin
     */
    public GeneratePDF withMargins(String top, String right, String bottom, String left) {
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
        this.marginLeft = left;
        return this;
    }

    /**
     * Generate PDF in landscape orientation.
     */
    public GeneratePDF inLandscape() {
        this.landscape = true;
        return this;
    }

    /**
     * Generate PDF in portrait orientation (default).
     */
    public GeneratePDF inPortrait() {
        this.landscape = false;
        return this;
    }

    /**
     * Include background graphics in the PDF.
     */
    public GeneratePDF withBackground() {
        this.printBackground = true;
        return this;
    }

    /**
     * Set the header template.
     * Can include classes: date, title, url, pageNumber, totalPages.
     *
     * @param template HTML template for the header
     */
    public GeneratePDF withHeaderTemplate(String template) {
        this.displayHeaderFooter = true;
        this.headerTemplate = template;
        return this;
    }

    /**
     * Set the footer template.
     * Can include classes: date, title, url, pageNumber, totalPages.
     *
     * @param template HTML template for the footer
     */
    public GeneratePDF withFooterTemplate(String template) {
        this.displayHeaderFooter = true;
        this.footerTemplate = template;
        return this;
    }

    /**
     * Set page ranges to print (e.g., "1-5, 8, 11-13").
     *
     * @param ranges Page range specification
     */
    public GeneratePDF withPageRanges(String ranges) {
        this.pageRanges = ranges;
        return this;
    }

    /**
     * Set the scale of the webpage rendering.
     *
     * @param scale Scale factor (default is 1, range 0.1-2)
     */
    public GeneratePDF withScale(double scale) {
        this.scale = scale;
        return this;
    }

    @Override
    @Step("{0} generates PDF and saves to #outputPath")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();

        // Ensure output directory exists
        try {
            Files.createDirectories(outputPath.getParent());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create output directory for PDF", e);
        }

        Page.PdfOptions options = new Page.PdfOptions().setPath(outputPath);

        if (format != null) {
            options.setFormat(format);
        }
        if (width != null) {
            options.setWidth(width);
        }
        if (height != null) {
            options.setHeight(height);
        }
        if (marginTop != null) {
            options.setMargin(new Margin()
                .setTop(marginTop)
                .setRight(marginRight)
                .setBottom(marginBottom)
                .setLeft(marginLeft));
        }
        if (landscape != null) {
            options.setLandscape(landscape);
        }
        if (printBackground != null) {
            options.setPrintBackground(printBackground);
        }
        if (displayHeaderFooter != null) {
            options.setDisplayHeaderFooter(displayHeaderFooter);
        }
        if (headerTemplate != null) {
            options.setHeaderTemplate(headerTemplate);
        }
        if (footerTemplate != null) {
            options.setFooterTemplate(footerTemplate);
        }
        if (pageRanges != null) {
            options.setPageRanges(pageRanges);
        }
        if (scale != null) {
            options.setScale(scale);
        }

        page.pdf(options);
    }

    /**
     * Get the output path for the PDF.
     */
    public Path getOutputPath() {
        return outputPath;
    }
}
