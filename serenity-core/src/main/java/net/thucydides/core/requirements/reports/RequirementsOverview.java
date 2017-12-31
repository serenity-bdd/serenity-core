package net.thucydides.core.requirements.reports;

import com.google.common.base.Joiner;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.reports.html.MarkdownRendering;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.core.reports.html.MarkdownRendering.RenderedElements.overview;

public class RequirementsOverview {

    private final EnvironmentVariables environmentVariables;
    private final String overviewText;
    private final Formatter formatter;

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsOverview.class);

    private List<String> classpathPaths() {
        return Arrays.asList(
                testRoot() + "/overview.md",
                "/features/overview.md",
                "/stories/overview.md"
        );
    }

    private List<String> fileSystemPaths() {
        return Arrays.asList(
                "src/test/resources/overview.md",
                "src/test/resources/features/overview.md",
                "src/test/resources/stories/overview.md"
        );
    }

    private String testRoot() {
        return ThucydidesSystemProperty.SERENITY_TEST_ROOT.from(environmentVariables,"");
    }

    private RequirementsOverview(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.overviewText = readOverviewText();
        this.formatter = new Formatter(Injectors.getInjector().getInstance(IssueTracking.class), environmentVariables);
    }

    private String readOverviewText() {
        return classpathPaths().stream()
                        .map(this::readOverviewFromClasspath)
                        .filter(overviewText -> !overviewText.isEmpty())
                        .findFirst()
                        .orElse(readOverviewTextFromFilesystem());
    }

    private String readOverviewTextFromFilesystem() {
        return fileSystemPaths().stream()
                .map(this::readOverviewFromFileSystemPath)
                .filter(overviewText -> !overviewText.isEmpty())
                .findFirst()
                .orElse("");
    }

    private String readOverviewFromClasspath(String path) {
        if (getClass().getResource(path) != null) {
            try {
                return Joiner.on(System.lineSeparator())
                        .join(Files.readAllLines(Paths.get(getClass().getResource(path).toURI())));
            } catch (IOException | URISyntaxException ignoreAndMoveOn) {
                LOGGER.warn("Could not read overview file: " + ignoreAndMoveOn.getMessage());
            }
        }
        return "";
    }

    private String readOverviewFromFileSystemPath(String path) {
        if (Files.exists(Paths.get(path))) {
            try {
                return Joiner.on(System.lineSeparator())
                        .join(Files.readAllLines((Paths.get(path))));
            } catch (IOException ignoreAndMoveOn) {
                LOGGER.warn("Could not read overview file: " + ignoreAndMoveOn.getMessage());
            }
        }
        return "";
    }

    public String asText() {
        return overviewText;
    }

    public String asRenderedHtml() {
        if (MarkdownRendering.configuredIn(environmentVariables).renderMarkdownFor(overview)) {
            return formatter.renderMarkdown(overviewText);
        } else {
            return Formatter.addLineBreaks(overviewText);
        }
    }

    public static RequirementsOverview withEnvironmentVariables(EnvironmentVariables environmentVariables) {
        return new RequirementsOverview(environmentVariables);
    }
}
