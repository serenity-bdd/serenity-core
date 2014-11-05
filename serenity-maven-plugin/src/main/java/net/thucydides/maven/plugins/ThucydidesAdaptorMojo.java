package net.thucydides.maven.plugins;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.TestOutcomeAdaptorReporter;
import net.thucydides.core.reports.adaptors.AdaptorService;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import  org.apache.maven.plugins.annotations.Parameter;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;
import java.io.IOException;

/**
 * This plugin generates converts external (e.g. xUnit) files into Thucydides reports.
 */
@Mojo( name = "import", requiresProject=false)
public class ThucydidesAdaptorMojo extends AbstractMojo {

    /**
     * Aggregate reports are generated here
     */
    @Parameter(property = "import.target", defaultValue = "target/site/thucydides", required=true)
    public File outputDirectory;

    /**
     * External test reports are read from here
     */
    @Parameter(property = "import.format", required=true)
    public String format;

    /**
     * External test reports are read from here if necessary.
     * This could be either a directory or a single file, depending on the adaptor used.
     * For some adaptors (e.g. database connectors), it will not be necessary.
     */
    @Parameter(property = "import.source")
    public File source;

    private final EnvironmentVariables environmentVariables;
    private final AdaptorService adaptorService;
    private final TestOutcomeAdaptorReporter reporter = new TestOutcomeAdaptorReporter();

    public ThucydidesAdaptorMojo(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.adaptorService = new AdaptorService(environmentVariables);
    }

    public ThucydidesAdaptorMojo() {
        this(Injectors.getInjector().getProvider(EnvironmentVariables.class).get() );
    }

    protected File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setSource(File source) {
        this.source = source;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Importing external test reports");
        getLog().info("Source directory: " + source);
        getLog().info("Output directory: " + getOutputDirectory());

        try {
            getLog().info("Adaptor: " + adaptorService.getAdaptor(format));
            reporter.registerAdaptor(adaptorService.getAdaptor(format));
            reporter.setOutputDirectory(outputDirectory);
            reporter.generateReportsFrom(source);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
