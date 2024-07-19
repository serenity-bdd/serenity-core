package net.thucydides.model.domain;

import java.util.HashMap;
import java.util.Map;

public class BadgeBackground {
    private final static Map<TestResult, String> DARKER_RESULT_BACKGROUND_COLORS = new HashMap<>();

    private final static String DEFAULT_COLOUR = "gray";

    static {
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.SUCCESS, "#99CC33");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.PENDING, "#6884A6");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.IGNORED, "#777777");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.SKIPPED, "#B2A671");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.ABORTED, "#FDAA7D");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.FAILURE, "#ff1631");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.ERROR, "#ff6108");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.COMPROMISED, "#ff68ff");
    }

    public static String forOutcome(TestOutcome outcome) {
        return DARKER_RESULT_BACKGROUND_COLORS.getOrDefault(outcome.getResult(), DEFAULT_COLOUR);
    }
}
