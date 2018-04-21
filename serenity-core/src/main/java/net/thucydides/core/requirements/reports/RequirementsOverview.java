package net.thucydides.core.requirements.reports;

import net.serenitybdd.core.strings.Joiner;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.reports.html.MarkdownRendering;
import net.thucydides.core.requirements.RootDirectory;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.thucydides.core.reports.html.MarkdownRendering.RenderedElements.overview;

public class RequirementsOverview {

    private final EnvironmentVariables environmentVariables;
    private final Formatter formatter;

    private static final String OVERVIEW = "overview.md";
    private static final String NARRATIVE_TXT = "narrative.txt";
    private static final String NARRATIVE_MD = "narrative.md";

    private static final Logger LOGGER = LoggerFactory.getLogger(RequirementsOverview.class);
    private String relativePath = "";


    public String asText() {
        return readOverviewText();
    }

    public String asRenderedHtml() {
        if (MarkdownRendering.configuredIn(environmentVariables).renderMarkdownFor(overview)) {
            return formatter.renderMarkdown(readOverviewText());
        } else {
            return Formatter.addLineBreaks(readOverviewText());
        }
    }

    public static RequirementsOverview withEnvironmentVariables(EnvironmentVariables environmentVariables) {
        return new RequirementsOverview(environmentVariables);
    }

    private List<String> classpathPaths() {
        return Stream.of(OVERVIEW, NARRATIVE_MD, NARRATIVE_TXT)
                                   .map(this::relativeOverviewFilesOnClasspath)
                                   .flatMap(Collection::stream)
                                   .collect(Collectors.toList());
    }

    private List<String> fileSystemPaths() {
        return Stream.of(OVERVIEW, NARRATIVE_MD, NARRATIVE_TXT)
                .map(this::relativeFileSystemPaths)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<String> relativeOverviewFilesOnClasspath(String overviewFile) {
        RootDirectory rootDirectory = RootDirectory.definedIn(environmentVariables);

        return Arrays.asList(
                testRoot() + "/" + relativePath() + overviewFile,
                "/" + rootDirectory.featureDirectoryName() + "/" + relativePath() + overviewFile,
                "/" + rootDirectory.storyDirectoryName() + "/" + relativePath() + overviewFile
                );
    }

    private List<String> relativeFileSystemPaths(String overviewFile) {
        RootDirectory rootDirectory = RootDirectory.definedIn(environmentVariables);
        Optional<Path> featureOrStoriesDirectory = rootDirectory.featuresOrStoriesRootDirectory();

        List<String> overviewDirectories = new ArrayList<>();

        overviewDirectories.add("src/test/resources/" + relativePath() +  overviewFile);
        featureOrStoriesDirectory.ifPresent(
                featureDirectory -> overviewDirectories.add(featureDirectory + "/" + relativePath() + overviewFile)
        );

        return overviewDirectories;
    }

    private String relativePath() {
        if (relativePath.isEmpty()) return relativePath;

        return relativePath + "/";
    }

    private String testRoot() {
        return ThucydidesSystemProperty.SERENITY_TEST_ROOT
                .from(environmentVariables,"")
                .replaceAll("\\.","/");
    }

    private RequirementsOverview(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
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

    public RequirementsOverview withRelativePath(String relativePath) {
        this.relativePath = relativePath;
        return this;
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

}
