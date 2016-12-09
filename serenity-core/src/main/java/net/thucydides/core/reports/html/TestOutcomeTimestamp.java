package net.thucydides.core.reports.html;

import ch.lambdaj.Lambda;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.TestOutcomes;
import org.joda.time.DateTime;

import static ch.lambdaj.Lambda.on;

public class TestOutcomeTimestamp {

    protected static final String TIMESTAMP_FORMAT = "dd-MM-YYYY HH:mm";

    public static String from(TestOutcomes testOutcomes) {
        DateTime startTime = Lambda.min(testOutcomes.getOutcomes(), on(TestOutcome.class).getStartTime());
        return startTime == null ? "" : startTime.toString(TIMESTAMP_FORMAT);
    }

    public static String from(TestOutcome testOutcome) {
        DateTime startTime = testOutcome.getStartTime();
        return startTime == null ? "" : startTime.toString(TIMESTAMP_FORMAT);
    }

}
