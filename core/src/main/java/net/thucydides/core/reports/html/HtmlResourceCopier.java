package net.thucydides.core.reports.html;

import net.thucydides.core.resources.FileResources;
import net.thucydides.core.resources.ResourceList;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Utility class that copies HTML resource files (images, stylesheets...) from a JAR to a target directory.
 */
public class HtmlResourceCopier {

    private String resourceDirectory;

    public HtmlResourceCopier(final String resourceDirectory) {
        super();
        this.resourceDirectory = resourceDirectory;
    }

    /**
     * Resources (stylesheets, images) etc are all stored in the
     * src/main/resources/reports directory. When the jar is deployed, they will
     * end up on the classpath.
     */
    public void copyHTMLResourcesTo(final File targetDirectory) throws IOException {

        Pattern resourcePattern = allFilesInDirectory(resourceDirectory);
        FileResources fileResource = FileResources.from(resourceDirectory);

        Collection<String> reportResources = ResourceList.forResources(resourcePattern).list();

        for (String resourcePath : reportResources) {
            if (fileResourceFromAJar(resourcePath)) {
                fileResource.copyResourceTo(resourcePath, targetDirectory);
            } else if (fileResourceFromPath(resourcePath)) {
                fileResource.copyResourceTo(resourcePath,
                                            targetDirectory);
            }
        }
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
}
