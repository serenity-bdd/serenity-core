package net.thucydides.core.model;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.digest.Digest;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_COMPRESS_FILENAMES;

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
    private boolean compressedFilename;
    private String prefix = "";

    private ReportNamer(final ReportType type) {
        this(type, SERENITY_COMPRESS_FILENAMES.booleanFrom(ConfiguredEnvironment.getEnvironmentVariables(), true));
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
        String testNameWithoutIndex = NameConverter.stripIndexesFrom(testName);
        return normalizedVersionOf(NameConverter.filesystemSafe(testNameWithoutIndex));
    }

    public String getNormalizedTestReportNameFor(String testName) {
        String testNameWithoutIndex = NameConverter.stripIndexesFrom(testName);
        return normalizedVersionOf(NameConverter.filesystemSafe(testNameWithoutIndex));
    }

    public String optionallyCompressed(String text) {
        return compressedFilename ? Digest.ofTextValue(text) : text;
    }

    private String getBaseTestNameFor(TestOutcome testOutcome) {
        return NameConverter.withNoIssueNumbers(testOutcome.getQualifiedId());
    }


    /**
     * Return a filesystem-friendly version of the test case name. The filesytem
     * version should have no spaces and have the XML file suffix.
     */
    public String getSimpleTestNameFor(final TestOutcome testOutcome) {
        return optionallyCompressed(appendSuffixTo(NameConverter.withNoIssueNumbers(testOutcome.getQualifiedId())));
    }

    public String getNormalizedTestNameFor(final Story userStory) {
        return getNormalizedTestNameFor(userStory.getName());
    }

    public String getNormalizedTestNameFor(String name) {
        return normalizedVersionOf(NameConverter.underscore(name.toLowerCase()));
    }

    private String normalizedVersionOf(String text) {
        return prefix + ((compressedFilename) ? appendSuffixTo(Digest.ofTextValue(text)) : appendSuffixTo(text));
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

    public ReportNamer withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
}
