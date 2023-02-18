package net.thucydides.core.reports.html;

import net.thucydides.core.reports.TestOutcomes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResultCountCache {
    private static final Map<Integer, ResultCounts> RESULT_COUNTS = new ConcurrentHashMap<>(1024);

    public static ResultCounts resultCountsFor(TestOutcomes testOutcomes) {
        return RESULT_COUNTS.computeIfAbsent(testOutcomes.hashCode(), (key -> ResultCounts.forOutcomesIn(testOutcomes)));
    }
}
