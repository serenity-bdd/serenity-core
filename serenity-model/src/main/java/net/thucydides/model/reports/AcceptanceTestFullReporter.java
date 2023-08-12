package net.thucydides.model.reports;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface AcceptanceTestFullReporter {

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
     * Define the output directory in which the reports will be written.
     */
    void setOutputDirectory(final File outputDirectory);

    /**
     * Optional. Used to distinguish the report generated from other similar reports.
     */
    void setQualifier(final String qualifier);

    /**
     * Generate reports for a given acceptance test run.
     */
    void generateReportsFor(final TestOutcomes testOutcomes) throws IOException;
}
