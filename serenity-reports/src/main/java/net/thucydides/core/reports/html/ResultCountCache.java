package net.thucydides.core.reports.html;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.reports.html.ResultCounts;

public class ResultCountCache {

    private static final LoadingCache<TestOutcomes, ResultCounts> RESULT_COUNTS = Caffeine.newBuilder()
            .maximumSize(1024)
            .build(ResultCounts::forOutcomesIn);

    public static ResultCounts resultCountsFor(TestOutcomes testOutcomes) {
        return RESULT_COUNTS.get(testOutcomes);
    }
}
