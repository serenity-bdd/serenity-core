package net.serenitybdd.plugins.saucelabs;

import net.thucydides.model.util.EnvironmentVariables;
import org.joda.time.LocalDateTime;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_PROJECT_NAME;

public class BuildName {

    private static final String buildTimeStamp = LocalDateTime.now().toString("yyyy-MM-dd hh:mm");

    public static String from(EnvironmentVariables environmentVariables) {
        String projectName = SERENITY_PROJECT_NAME.from(environmentVariables, "Serenity BDD Test Suite");
        if (environmentVariables.getValue("BUILD_NUMBER") != null) {
            return projectName + " - build " + environmentVariables.getValue("BUILD_NUMBER");
        } else {
            return projectName + " - " + buildTimeStamp;
        }
    }
}
