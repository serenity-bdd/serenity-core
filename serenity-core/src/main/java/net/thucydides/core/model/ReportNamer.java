package net.thucydides.core.model;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.digest.Digest;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_COMPRESS_FILENAMES;
import static net.thucydides.core.util.NameConverter.*;

/**
 * Determies the correct default name for test reports.
 * @author johnsmart
 *
 */
public class ReportNamer {

    public static ReportNamer forReportType(ReportType type) {
        return new ReportNamer(type);
    }

    private ReportType type;
    private boolean compressedFilename = true;

    private ReportNamer(final ReportType type) {
        this(type,
             ConfiguredEnvironment.getEnvironmentVariables()
                     .getPropertyAsBoolean(SERENITY_COMPRESS_FILENAMES.getPropertyName(), true));
    }

    public ReportNamer(ReportType type, boolean compressedFilename) {
        this.type = type;
        this.compressedFilename = compressedFilename;
    }

    /**
     * Return a filesystem-friendly version of the test case name. The file system
     * version should have no spaces and have the XML file suffix.
     */
    public String getNormalizedTestNameFor(final TestOutcome testOutcome) {
        String testName = getBaseTestNameFor(testOutcome);
        String testNameWithoutIndex = stripIndexesFrom(testName);
        return normalizedVersionOf(filesystemSafe(testNameWithoutIndex));
    }

    private String optionallyCompressed(String text) {
        return compressedFilename ? Digest.ofTextValue(text) : text;
    }

    private String getBaseTestNameFor(TestOutcome testOutcome) {
        return withNoIssueNumbers(testOutcome.getQualifiedId());
    }


    /**
     * Return a filesystem-friendly version of the test case name. The filesytem
     * version should have no spaces and have the XML file suffix.
     */
    public String getSimpleTestNameFor(final TestOutcome testOutcome) {
        return optionallyCompressed(appendSuffixTo(withNoIssueNumbers(testOutcome.getQualifiedId())));
    }

    public String getNormalizedTestNameFor(final Story userStory) {
        return getNormalizedTestNameFor(userStory.getName());
    }

    public String getNormalizedTestNameFor(String name) {
        return normalizedVersionOf(underscore(name.toLowerCase()));
    }

    private String normalizedVersionOf(String text) {
        return (compressedFilename) ? appendSuffixTo(Digest.ofTextValue(text)) : appendSuffixTo(text);
    }

    private String appendSuffixTo(final String testNameWithUnderscores) {
        if (type == ReportType.ROOT) {
            return testNameWithUnderscores;
        } else {
            return testNameWithUnderscores + "." + type.toString();
        }
    }

    public ReportNamer withNoCompression() {
        return new ReportNamer(type, false);
    }
}
