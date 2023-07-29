package net.thucydides.core.reports;

import net.serenitybdd.core.di.ModelInfrastructure;
import net.thucydides.core.webdriver.Configuration;

import java.io.File;

/**
 * A base directory for Thucydides report generators.
 */
public class ThucydidesReporter {
    private File outputDirectory;
    private File sourceDirectory;
    private final Configuration configuration;

    public ThucydidesReporter() {
        this.configuration = ModelInfrastructure.getConfiguration();
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    /**
     * Override the default source directory (target/site/serenity) for test reports.
     */
    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        configuration.setSourceDirectory(sourceDirectory);
    }

    /**
     * Reports will be generated here.
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Override the default output directory (target/site/serenity) for test reports.
     */
    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
        configuration.setOutputDirectory(outputDirectory);
    }


}
