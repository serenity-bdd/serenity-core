package net.thucydides.model.reports;

import net.thucydides.model.domain.TestOutcome;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Generates a report based on a set of acceptance test results.
 *
 * @author johnsmart
 *
 */
public interface AcceptanceTestReporter {

    /**
     * A name used to identify a given reporter.
     */
    String getName();

    /**
     * Return the format that this reporter generates, if it is a format that can be activated or deactivated
     * via the output.formats configuration property.
     */
    Optional<OutcomeFormat> getFormat();
    /**
     * Generate reports for a given acceptance test run.
     */
    File generateReportFor(final TestOutcome testOutcome) throws IOException;
    
    /**
     * Define the output directory in which the reports will be written.
     */
    void setOutputDirectory(final File outputDirectory);
    
    /**
     * Define or override the directory where report resources are stored.
     * This can be on the file system or on the classpath
     */
    void setResourceDirectory(final String resourceDirectoryPath);

    /**
     * Optional. Used to distinguish the report generated from other similar reports.
     */
    void setQualifier(final String qualifier);
    
}
