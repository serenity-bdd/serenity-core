package net.thucydides.core.reports.html;

import net.thucydides.model.domain.TestResult;

/**
 * Created by john on 1/11/2015.
 */
public class ResultRankingFormatter {

    public Integer forResult(String result) {
        return forResult(TestResult.valueOf(result));
    }

    public Integer forResult(TestResult result) {
        return (result == null)? 0 : result.getPriority();
    }

}
