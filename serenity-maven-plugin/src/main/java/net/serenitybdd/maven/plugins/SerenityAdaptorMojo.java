package net.serenitybdd.maven.plugins;

import net.thucydides.core.reports.TestOutcomeAdaptorReporter;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.reports.adaptors.AdaptorService;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * This plugin generates converts external (e.g. xUnit) files into Serenity reports.
 */
@Mojo( name = "import", requiresProject=false)
public class SerenityAdaptorMojo extends AbstractMojo {

    /**
     * Aggregate reports are generated here
     */
    @Parameter(property = "import.target", defaultValue = "${user.dir}/target/site/serenity", required=true)
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

    public SerenityAdaptorMojo(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
        this.adaptorService = new AdaptorService(environmentVariables);
    }

    public SerenityAdaptorMojo() {
        this(SystemEnvironmentVariables.currentEnvironmentVariables() );
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

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Importing external test reports");
        getLog().info("Source directory: " + source);
        getLog().info("Output directory: " + getOutputDirectory());

        try {
            getLog().info("Adaptor: " + adaptorService.getAdaptor(format));
            reporter.registerAdaptor(adaptorService.getAdaptor(format));
            reporter.setOutputDirectory(outputDirectory);
            reporter.generateReportsFrom(source);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
