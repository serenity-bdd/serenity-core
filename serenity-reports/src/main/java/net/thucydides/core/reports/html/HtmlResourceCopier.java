package net.thucydides.core.reports.html;

import net.thucydides.model.resources.FileResources;
import net.thucydides.model.resources.ResourceList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Utility class that copies HTML resource files (images, stylesheets...) from a JAR to a target directory.
 */
public class HtmlResourceCopier {

    private String resourceDirectory;
    private Pattern resourcePattern;

    public HtmlResourceCopier(final String resourceDirectory) {
        super();
        this.resourceDirectory = resourceDirectory;
        resourcePattern = allFilesInDirectory(resourceDirectory);
    }

    /**
     * Resources (stylesheets, images) etc are all stored in the
     * src/main/resources/reports directory. When the jar is deployed, they will
     * end up on the classpath.
     */
    public void to(final File targetDirectory) throws IOException {

        if (resourceFilesAreAlreadyPresentIn(targetDirectory)) {
            return;
        }

        recordResourceMarkerIn(targetDirectory);
        FileResources fileResource = FileResources.from(resourceDirectory);

        Collection<String> reportResources = ResourceList.forResources(resourceDirectory, resourcePattern).list();

        for (String resourcePath : reportResources) {
            if (fileResourceFromAJar(resourcePath)) {
                fileResource.copyResourceTo(resourcePath, targetDirectory);
            } else if (fileResourceFromPath(resourcePath)) {
                fileResource.copyResourceTo(resourcePath, targetDirectory);
            }
        }
    }

    private void recordResourceMarkerIn(File targetDirectory) throws IOException {
        Files.createDirectories(targetDirectory.toPath());
        resourceMarkerIn(targetDirectory).toFile().createNewFile();
    }

    private boolean resourceFilesAreAlreadyPresentIn(File targetDirectory) {
        return Files.exists(resourceMarkerIn(targetDirectory));
    }

    private Path resourceMarkerIn(File outputDirectory) {
        return outputDirectory.toPath().resolve("serenity-resources");
    }


    private boolean fileResourceFromAJar(final String resourcePath) {
        return (resourceIsFromAJar(resourcePath)
                && (thisIsNotTheRoot(resourcePath))
                && (thisIsNotADirectory(resourcePath)));

    }

    private boolean fileResourceFromPath(final String resourcePath) {
        boolean res = (!resourceIsFromAJar(resourcePath)
                && (thisIsNotTheRoot(resourcePath))
                && (thisIsNotADirectory(resourcePath)));
        return res;
    }

    private boolean thisIsNotADirectory(final String resourcePath) {
        return !resourcePath.endsWith("/");
    }

    private boolean thisIsNotTheRoot(final String resourcePath) {
        return !resourceDirectory.equals(resourcePath);
    }

    private Pattern allFilesInDirectory(final String directory) {
        String allFilesPattern = String.format(".*[\\\\/]?%s[\\\\/].*", directory);
        return Pattern.compile(allFilesPattern);
    }

    private boolean resourceIsFromAJar(final String resourcePath) {
        return !resourcePath.startsWith("/");
    }

    public static HtmlResourceCopier copyHtmlResourcesFrom(String resourceDirectory) {
        return new HtmlResourceCopier(resourceDirectory);
    }
}
