package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TestOutcomeTimestamp {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static String from(TestOutcomes testOutcomes) {
        return testOutcomes.getStartTime().map(TIMESTAMP_FORMAT::format).orElse("");
//        return testOutcomes.getOutcomes().stream()
//                .filter(outcome -> outcome.getStartTime() != null)
//                .map(TestOutcome::getStartTime)
//                .sorted()
//                .findFirst()
//                .map(TIMESTAMP_FORMAT::format)
//                .orElse("");
    }

    public static String from(TestOutcome testOutcome) {
        ZonedDateTime startTime = testOutcome.getStartTime();
        return startTime == null ? "" : TIMESTAMP_FORMAT.format(startTime);
    }

}
