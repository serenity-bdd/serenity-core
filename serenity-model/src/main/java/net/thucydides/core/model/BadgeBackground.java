package net.thucydides.core.model;

import java.util.HashMap;
import java.util.Map;

public class BadgeBackground {
    private final static Map<TestResult, String> DARKER_RESULT_BACKGROUND_COLORS = new HashMap<>();

    private final static String DEFAULT_COLOUR = "gray";

    static {
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.SUCCESS, "#1D9918");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.PENDING, "#6884A6");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.IGNORED, "#777777");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.SKIPPED, "#B2A671c");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.FAILURE, "#f8001f");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.ERROR, "darkorange");
        DARKER_RESULT_BACKGROUND_COLORS.put(TestResult.COMPROMISED, "fuchsia");
    }

    public static String forOutcome(TestOutcome outcome) {
        return DARKER_RESULT_BACKGROUND_COLORS.getOrDefault(outcome.getResult(), DEFAULT_COLOUR);
    }
}
