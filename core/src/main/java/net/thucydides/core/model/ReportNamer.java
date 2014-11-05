package net.thucydides.core.model;

import net.thucydides.core.digest.Digest;
import net.thucydides.core.util.NameConverter;
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

    private ReportNamer(final ReportType type) {
        this.type = type;
    }

    /**
     * Return a filesystem-friendly version of the test case name. The file system
     * version should have no spaces and have the XML file suffix.
     */
    public String getNormalizedTestNameFor(final TestOutcome testOutcome) {
        String testName = getBaseTestNameFor(testOutcome);
        String testNameWithoutIndex = stripIndexesFrom(testName);
        return appendSuffixTo(Digest.ofTextValue(testNameWithoutIndex));
    }

    private String getBaseTestNameFor(TestOutcome testOutcome) {
        String testName = "";

        if (testOutcome.getUserStory() != null) {
            testName = NameConverter.underscore(testOutcome.getUserStory().getName());
        } else if (testOutcome.getPath() != null) {
            testName = NameConverter.underscore(testOutcome.getPath());
        }
        String scenarioName = NameConverter.underscore(testOutcome.getQualifiedMethodName());
        return pathFrom(testOutcome) + withNoIssueNumbers(appendToIfNotNull(testName, scenarioName));
    }


    /**
     * Return a filesystem-friendly version of the test case name. The filesytem
     * version should have no spaces and have the XML file suffix.
     */
    public String getSimpleTestNameFor(final TestOutcome testOutcome) {
        String testName = "";
        if (testOutcome.getUserStory() != null) {
            testName = NameConverter.underscore(testOutcome.getUserStory().getName());
        }
        String scenarioName = NameConverter.underscore(testOutcome.getMethodName());
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
        String testNameWithUnderscores = NameConverter.underscore(name.toLowerCase());
        return appendSuffixTo(Digest.ofTextValue(testNameWithUnderscores));
    }

    private String appendSuffixTo(final String testNameWithUnderscores) {
        if (type == ReportType.ROOT) {
            return testNameWithUnderscores;
        } else {
            return testNameWithUnderscores + "." + type.toString();
        }
    }
}
