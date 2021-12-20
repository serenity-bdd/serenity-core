package io.cucumber.core.plugin;



import io.cucumber.messages.types.Tag;
import net.thucydides.core.util.EnvironmentVariables;
import org.jsoup.internal.StringUtil;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static net.thucydides.core.ThucydidesSystemProperty.CURRENT_TARGET_VERSION;

public class ManualScenarioChecker {
    private final EnvironmentVariables environmentVariables;

    private static final String ANSI_RED = "\u001B[91m";
    private static final String ANSI_RESET = "\u001B[0m";

    private static final String UNDEFINED_MANUAL_VERSION_TARGET_MESSAGE = System.lineSeparator() + ANSI_RED +
            "WARNING" +
            System.lineSeparator() +
            "The @manual and @manual-last-tested-version annotations were used in the Cucumber scenarios, " +
            System.lineSeparator() +
            "but no current.target.version was defined. For the @last-tested-version tag to work," +
            System.lineSeparator() +
            "You need to set the current.target.version to a corresponding value in your Serenity configuration file."
            + System.lineSeparator() + ANSI_RESET;


    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ManualScenarioChecker.class);

    public ManualScenarioChecker(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean scenarioResultIsUpToDate(List<Tag> scenarioTags) {
        Optional<String> lastTestedVersion = lastTestedVersionFromTags(scenarioTags);

        Optional<String> currentTargetVersion = CURRENT_TARGET_VERSION.optionalFrom(environmentVariables);

        // If no @manual-last-tested-version is defined, a manual scenario result is always up to date
        if (!lastTestedVersion.isPresent()) {
            return true;
        }

        // If @manual-last-tested-version is defined but no current.target.version is defined, we might have a problem.
        if (!currentTargetVersion.isPresent() || StringUtil.isBlank(currentTargetVersion.get())) {
            LOGGER.warn(UNDEFINED_MANUAL_VERSION_TARGET_MESSAGE);
            return true;
        }

        return lastTestedVersion.get().equalsIgnoreCase(currentTargetVersion.get());
    }

    public Optional<String> lastTestedVersionFromTags(List<Tag> scenarioTags) {
        return scenarioTags.stream()
                .filter(tag -> tag.getName().startsWith("@manual-last-tested:"))
                .map(tag -> versionFrom(tag.getName()))
                .findFirst();
    }

    private String versionFrom(String lastTestedVersionTag) {
        return lastTestedVersionTag.trim().substring("@manual-last-tested:".length()).trim();

    }

    public Optional<String> testEvidenceFromTags(List<Tag> scenarioTags) {
        return scenarioTags.stream()
                .filter(tag -> tag.getName().startsWith("@manual-test-evidence:"))
                .map(tag -> evidenceFrom(tag.getName()))
                .findFirst();
    }

    private String evidenceFrom(String evidenceTag) {
        return evidenceTag.trim().substring("@manual-test-evidence:".length()).trim();

    }

}
