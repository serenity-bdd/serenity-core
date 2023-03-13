package net.serenitybdd.plugins.lambdatest;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BuildName {
    public static String from(TestOutcome testOutcome, EnvironmentVariables environmentVariables) {
        if (environmentVariables.getValue("BUILD_NUMBER") != null) {
            return testOutcome.getStoryTitle() + " - build " + environmentVariables.getValue("BUILD_NUMBER");
        } else {
            return testOutcome.getStoryTitle() + " - build " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        }
    }
}
