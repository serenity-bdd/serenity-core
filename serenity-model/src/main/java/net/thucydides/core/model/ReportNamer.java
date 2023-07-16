package net.thucydides.core.model;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.digest.Digest;
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
    public String getNormalizedReportNameFor(final TestOutcome testOutcome) {
        String testName = getBaseTestNameFor(testOutcome);
        String testNameWithoutIndex = NameConverter.stripIndexesFrom(testName);
        return normalizedVersionOf(NameConverter.filesystemSafe(testNameWithoutIndex));
    }

    public String getNormalizedTestReportNameFor(String testName) {
        String testNameWithoutIndex = NameConverter.stripIndexesFrom(testName);
        return normalizedVersionOf(NameConverter.filesystemSafe(testNameWithoutIndex));
    }

    public String optionallyCompressed(String text) {
        return compressedFilename ? Digest.ofTextValue(text) : Digest.ofTextValue(text, shortened(text));
    }

    private String shortened(String text) {
        return text.substring(Math.max(0, text.length() - 30));
    }

    private String getBaseTestNameFor(TestOutcome testOutcome) {
        if (isJUnit5(testOutcome.getQualifiedId())) {
            return testOutcome.getUserStory().getId() + "." + testOutcome.getMethodName();
        } else {
            String pathWithoutSlashes = testOutcome.getPath() != null ? testOutcome.getPath().replace(".", "_").replace("/", "_SL_") : "";
            return testOutcome.getQualifiedId() + ":" + NameConverter.withNoIssueNumbers(pathWithoutSlashes);
        }
    }

    private boolean isJUnit5(String qualifiedId) {
        return qualifiedId.startsWith("[engine:");
    }


    /**
     * Return a filesystem-friendly version of the test case name. The filesytem
     * version should have no spaces and have the XML file suffix.
     */
    public String getSimpleTestNameFor(final TestOutcome testOutcome) {
        return optionallyCompressed(appendSuffixTo(NameConverter.withNoIssueNumbers(testOutcome.getQualifiedId())));
    }

    public String getNormalizedReportNameFor(final Story userStory) {
        return getNormalizedReportNameFor(userStory.getName());
    }

    public String getNormalizedReportNameFor(String name) {
        return normalizedVersionOf(NameConverter.underscore(name.toLowerCase()));
    }

    private String normalizedVersionOf(String text) {
        return prefix + appendSuffixTo(optionallyCompressed(text));
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
