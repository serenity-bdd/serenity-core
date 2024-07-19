package net.thucydides.core.model;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestResultList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.thucydides.model.domain.TestResult.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class WhenEvaluatingOverallResults {

    private final List<TestResult> results;
    private final TestResult expectedOverallResult;

    public WhenEvaluatingOverallResults(List<TestResult> results, TestResult expectedOverallResult) {
        this.results = results;
        this.expectedOverallResult = expectedOverallResult;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][] {
                { Collections.emptyList(),                  SUCCESS },
                {List.of(SUCCESS),                   SUCCESS },
                { Arrays.asList(SUCCESS, SUCCESS),          SUCCESS },
                { Arrays.asList(SUCCESS, SUCCESS, SUCCESS), SUCCESS },
                { Arrays.asList(SUCCESS, PENDING),          PENDING },
                { Arrays.asList(SUCCESS, IGNORED),          IGNORED }, // IGNORED can be the result of a failed assumption
                { Arrays.asList(SUCCESS, SKIPPED),          SUCCESS },
                {List.of(FAILURE),                   FAILURE },
                { Arrays.asList(FAILURE, FAILURE),          FAILURE },
                { Arrays.asList(FAILURE, SUCCESS),          FAILURE },
                { Arrays.asList(FAILURE, IGNORED),          FAILURE },
                { Arrays.asList(FAILURE, PENDING),          FAILURE },
                {List.of(IGNORED),                   IGNORED },
                {List.of(SKIPPED),                   SKIPPED },
                { Arrays.asList(IGNORED, FAILURE,SKIPPED),  FAILURE },
                { Arrays.asList(IGNORED, IGNORED),          IGNORED },
                { Arrays.asList(IGNORED, PENDING),          PENDING },
                {List.of(PENDING),                   PENDING },
                { Arrays.asList(PENDING, PENDING),          PENDING },
                {List.of(ERROR),                     ERROR },
                { Arrays.asList(PENDING,ERROR),             ERROR },
                { Arrays.asList(IGNORED,ERROR),             ERROR },
                { Arrays.asList(SKIPPED,ERROR),             ERROR },
                { Arrays.asList(SUCCESS,ERROR),             ERROR },
                { Arrays.asList(SUCCESS,COMPROMISED),       COMPROMISED },
                { Arrays.asList(ERROR,COMPROMISED),         COMPROMISED },
                { Arrays.asList(FAILURE,COMPROMISED),       COMPROMISED },
                { Arrays.asList(IGNORED,COMPROMISED),       COMPROMISED },
                { Arrays.asList(PENDING,COMPROMISED),       COMPROMISED },
                { Arrays.asList(SKIPPED,COMPROMISED),       COMPROMISED },

        });
    }

    @Test
    public void should_produce_correct_overall_result_from_a_list_of_step_results() {
        assertThat(TestResultList.overallResultFrom(results), is(expectedOverallResult));
    }
}
