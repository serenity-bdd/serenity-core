package net.thucydides.core.model;

import net.thucydides.core.digest.Digest;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

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
             Injectors.getInjector().getInstance(EnvironmentVariables.class)
                     .getPropertyAsBoolean("serenity.compress.filenames", true));
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
        return normalizedVersionOf(testNameWithoutIndex);
            // appendSuffixTo(Digest.ofTextValue(testNameWithoutIndex));
    }

    private String getBaseTestNameFor(TestOutcome testOutcome) {
        String testName = "";

        if (testOutcome.getUserStory() != null) {
            testName = underscore(testOutcome.getUserStory().getName());
        } else if (testOutcome.getPath() != null) {
            testName = underscore(testOutcome.getPath());
        }
        String scenarioName = underscore(testOutcome.getQualifiedMethodName());
        return pathFrom(testOutcome) + withNoIssueNumbers(appendToIfNotNull(testName, scenarioName));
    }


    /**
     * Return a filesystem-friendly version of the test case name. The filesytem
     * version should have no spaces and have the XML file suffix.
     */
    public String getSimpleTestNameFor(final TestOutcome testOutcome) {
        String testName = "";
        if (testOutcome.getUserStory() != null) {
            testName = underscore(testOutcome.getUserStory().getName());
        }
        String scenarioName = underscore(testOutcome.getName());
        testName = pathFrom(testOutcome) + withNoIssueNumbers(withNoArguments(appendToIfNotNull(testName, scenarioName)));
        return appendSuffixTo(Digest.ofTextValue(testName));
    }

    private String pathFrom(TestOutcome testOutcome) {
        return (testOutcome.getPath() != null) ? testOutcome.getPath() + "/" : "";
    }

    private String appendToIfNotNull(final String baseString, final String nextElement) {
        if (StringUtils.isNotEmpty(baseString)) {
            return baseString + "_" + nextElement;
        } else {
            return nextElement;
        }
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
