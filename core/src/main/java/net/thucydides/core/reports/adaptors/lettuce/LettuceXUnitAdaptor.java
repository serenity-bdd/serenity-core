package net.thucydides.core.reports.adaptors.lettuce;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.thucydides.core.PendingStepException;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.reports.adaptors.common.FilebasedOutcomeAdaptor;
import net.thucydides.core.reports.adaptors.xunit.BasicXUnitLoader;
import net.thucydides.core.reports.adaptors.xunit.io.XUnitFiles;
import net.thucydides.core.reports.adaptors.xunit.model.TestCase;
import net.thucydides.core.reports.adaptors.xunit.model.TestSuite;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LettuceXUnitAdaptor extends FilebasedOutcomeAdaptor {

    BasicXUnitLoader loader = new BasicXUnitLoader();

    public List<TestOutcome> loadOutcomesFrom(final File source) throws IOException {
        List<TestOutcome> loadedOutcomes = Lists.newArrayList();
        for(File xunitFile : XUnitFiles.in(source)) {
            loadedOutcomes.addAll(testOutcomesIn(xunitFile));
        }
        return ImmutableList.copyOf(loadedOutcomes);
    }

    private Collection<? extends TestOutcome> testOutcomesIn(File xunitFile) throws IOException {
        List<TestSuite> rawTestSuites = loader.loadFrom(xunitFile);
        return ImmutableList.copyOf(groupTestCasesByClassname(rawTestSuites));
    }

    private List<TestOutcome> groupTestCasesByClassname(List<TestSuite> testSuites) {
        List<TestOutcome> groupedOutcomes = Lists.newArrayList();
        for(TestSuite suite : testSuites) {
            groupedOutcomes.addAll(testOutcomesWithGroupedTestCases(suite));
        }
        return groupedOutcomes;
    }


    private List<TestOutcome> testOutcomesWithGroupedTestCases(TestSuite testSuite) {
        List<TestCase> rawTestCases = testSuite.getTestCases();
        List<TestOutcome> groupedTestOutcomes = Lists.newArrayList();
        Map<String, TestOutcome> testOutcomesIndex = Maps.newHashMap();

        for(TestCase testCase : rawTestCases) {
            TestOutcome testOutcome = testOutcomeForTestClass(testOutcomesIndex, testCase.getClassname());
            addIfNotPresent(groupedTestOutcomes, testOutcome);
            TestStep nextStep = TestStep.forStepCalled(testCase.getName()).withResult(resultOf(testCase));
            Optional<Throwable> testFailure = testFailureFrom(testCase);
            if (testFailure.isPresent()) {
                nextStep.failedWith(testFailure.get());
            }
            testOutcome.recordStep(nextStep);
        }
        return ImmutableList.copyOf(groupedTestOutcomes);
    }

    private void addIfNotPresent(List<TestOutcome> groupedTestOutcomes, final TestOutcome testOutcome) {
        if (!groupedTestOutcomes.contains(testOutcome)) {
            groupedTestOutcomes.add(testOutcome);
        }
    }

    private Optional<Throwable> testFailureFrom(TestCase testCase) {
        Throwable testFailure = null;
        if (testCase.getFailure().isPresent()) {
            testFailure = testCase.getFailure().get().asAssertionFailure();
        } else if (testCase.getSkipped().isPresent()) {
            testFailure = new PendingStepException(testCase.getSkipped().get().getType());
        } else if (testCase.getError().isPresent()) {
            testFailure = testCase.getError().get().asException();
        }
        return Optional.fromNullable(testFailure);
    }

    private TestResult resultOf(TestCase testCase) {
        if (testCase.getError().isPresent()) {
            return TestResult.ERROR;
        } else if (testCase.getFailure().isPresent()) {
            return TestResult.FAILURE;
        } else if (testCase.getSkipped().isPresent()) {
            return TestResult.SKIPPED;
        } else {
            return TestResult.SUCCESS;
        }
    }

    private TestOutcome testOutcomeForTestClass(Map<String, TestOutcome> groupedTestOutcomes, String testClassName) {
        if (groupedTestOutcomes.containsKey(testClassName)) {
            return groupedTestOutcomes.get(testClassName);
        }
        TestOutcome newTestOutcome = TestOutcome.forTestInStory(testClassName, Story.called(testClassName));
        groupedTestOutcomes.put(testClassName, newTestOutcome);
        return newTestOutcome;
    }


}
