package net.serenitybdd.model.history;


import net.serenitybdd.model.collect.NewSet;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.flags.Flag;
import net.thucydides.model.domain.flags.FlagProvider;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.thucydides.model.ThucydidesSystemProperty.SHOW_HISTORY_FLAGS;

public class HistoricalFlagProvider implements FlagProvider {

    private final EnvironmentVariables environmentVariables;
    private final Map<String, PreviousTestOutcome> previousTestOutcomesById;

    private final Set<? extends Flag> NO_FLAGS = NewSet.of();

    
    public HistoricalFlagProvider(EnvironmentVariables environmentVariables,
                                  TestOutcomeSummaryRecorder summaryRecorder) {

        this.environmentVariables = environmentVariables;

        List<PreviousTestOutcome> previousOutcomes = summaryRecorder.loadSummaries();

        previousTestOutcomesById = indexById(previousOutcomes);
    }

    private Map<String, PreviousTestOutcome> indexById(List<PreviousTestOutcome> previousOutcomes) {
        Map<String, PreviousTestOutcome> previousTestOutcomesById = new HashMap();
        for(PreviousTestOutcome previousTestOutcome : previousOutcomes) {
            previousTestOutcomesById.put(previousTestOutcome.getId(), previousTestOutcome);
        }
        return previousTestOutcomesById;
    }

    @Override
    public Set<? extends Flag> getFlagsFor(TestOutcome testOutcome) {
        if (!historicalFlagsAreActivated()) { return NewSet.of(); }

        return newFailureIn(testOutcome) ? NewSet.of(NewFailure.FLAG) : NO_FLAGS;
    }

    private boolean newFailureIn(TestOutcome testOutcome) {
        if (isUnknownTest(testOutcome)) { return false; }
        if (isNotBroken(testOutcome)) { return false; }

        PreviousTestOutcome previousTestOutcome = previousTestOutcomesById.get(testOutcome.getId());
        if (resultIsDifferent(testOutcome, previousTestOutcome)) { return true; }
        return causeIsDifferent(testOutcome, previousTestOutcome);
    }

    private boolean isUnknownTest(TestOutcome testOutcome) {
        return (!previousTestOutcomesById.containsKey(testOutcome.getId()));
    }

    private boolean isNotBroken(TestOutcome testOutcome) {
        return ((testOutcome.getResult() == TestResult.SUCCESS)
                || (testOutcome.getResult() == TestResult.IGNORED)
                || (testOutcome.getResult() == TestResult.PENDING)
                || (testOutcome.getResult() == TestResult.SKIPPED));
    }

    private boolean causeIsDifferent(TestOutcome testOutcome, PreviousTestOutcome previousTestOutcome) {
        return (!StringUtils.equals(testOutcome.getTestFailureSummary(), previousTestOutcome.getTestFailureSummary()));
    }

    private boolean resultIsDifferent(TestOutcome testOutcome, PreviousTestOutcome previousTestOutcome) {
        return (testOutcome.getResult() != previousTestOutcome.getResult());
    }

    private boolean historicalFlagsAreActivated() {
        return SHOW_HISTORY_FLAGS.booleanFrom(environmentVariables, false);
    }
}
