package net.thucydides.core.reports;

import java.io.File;

/**
 * A base directory for Thucydides report generators.
 */
public class ThucydidesReporter {
    private File outputDirectory;
    private File sourceDirectory;


    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * Reports will be generated here.
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }


}
