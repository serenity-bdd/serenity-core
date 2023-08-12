package net.thucydides.model.reports.adaptors.lettuce;

import net.serenitybdd.model.PendingStepException;
import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestStep;
import net.thucydides.model.reports.adaptors.common.FilebasedOutcomeAdaptor;
import net.thucydides.model.reports.adaptors.xunit.BasicXUnitLoader;
import net.thucydides.model.reports.adaptors.xunit.io.XUnitFiles;
import net.thucydides.model.reports.adaptors.xunit.model.TestCase;
import net.thucydides.model.reports.adaptors.xunit.model.TestSuite;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LettuceXUnitAdaptor extends FilebasedOutcomeAdaptor {

    BasicXUnitLoader loader = new BasicXUnitLoader();

    public List<TestOutcome> loadOutcomesFrom(final File source) throws IOException {
        List<TestOutcome> loadedOutcomes = new ArrayList<>();
        for(File xunitFile : XUnitFiles.in(source)) {
            loadedOutcomes.addAll(testOutcomesIn(xunitFile));
        }
        return NewList.copyOf(loadedOutcomes);
    }

    private Collection<? extends TestOutcome> testOutcomesIn(File xunitFile) throws IOException {
        List<TestSuite> rawTestSuites = loader.loadFrom(xunitFile);
        return NewList.copyOf(groupTestCasesByClassname(rawTestSuites));
    }

    private List<TestOutcome> groupTestCasesByClassname(List<TestSuite> testSuites) {
        List<TestOutcome> groupedOutcomes = new ArrayList<>();
        for(TestSuite suite : testSuites) {
            groupedOutcomes.addAll(testOutcomesWithGroupedTestCases(suite));
        }
        return groupedOutcomes;
    }


    private List<TestOutcome> testOutcomesWithGroupedTestCases(TestSuite testSuite) {
        List<TestCase> rawTestCases = testSuite.getTestCases();
        List<TestOutcome> groupedTestOutcomes = new ArrayList<>();
        Map<String, TestOutcome> testOutcomesIndex = new HashMap();

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
        return NewList.copyOf(groupedTestOutcomes);
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
        return Optional.ofNullable(testFailure);
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
