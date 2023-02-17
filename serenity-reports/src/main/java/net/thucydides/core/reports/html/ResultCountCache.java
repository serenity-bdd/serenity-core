package net.thucydides.core.reports.html;

import net.thucydides.core.reports.TestOutcomes;

import java.util.HashMap;
import java.util.Map;

public class ResultCountCache {
    private static final Map<Integer, ResultCounts> RESULT_COUNTS = new HashMap<>();

    public static ResultCounts resultCountsFor(TestOutcomes testOutcomes) {
        if (RESULT_COUNTS.containsKey(testOutcomes.hashCode())) {
            return RESULT_COUNTS.get(testOutcomes.hashCode());
        } else {
            RESULT_COUNTS.put(testOutcomes.hashCode(), ResultCounts.forOutcomesIn(testOutcomes));
            return RESULT_COUNTS.get(testOutcomes.hashCode());
        }
    }
}
