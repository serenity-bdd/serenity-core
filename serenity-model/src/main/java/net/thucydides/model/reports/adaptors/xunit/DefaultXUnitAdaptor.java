package net.thucydides.model.reports.adaptors.xunit;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.reports.adaptors.common.FilebasedOutcomeAdaptor;
import net.thucydides.model.reports.adaptors.xunit.io.XUnitFiles;
import net.thucydides.model.reports.adaptors.xunit.model.TestCase;
import net.thucydides.model.reports.adaptors.xunit.model.TestException;
import net.thucydides.model.reports.adaptors.xunit.model.TestSuite;
import net.thucydides.model.util.NameConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultXUnitAdaptor extends FilebasedOutcomeAdaptor {
    private final XUnitLoader loader = new BasicXUnitLoader();

    public List<TestOutcome> loadOutcomesFrom(final File source) throws IOException {
        List<TestOutcome> loadedOutcomes = new ArrayList<>();
        for(File xunitFile : XUnitFiles.in(source)) {
            loadedOutcomes.addAll(testOutcomesIn(xunitFile));
        }
        return NewList.copyOf(loadedOutcomes);
    }

    public List<TestOutcome> testOutcomesIn(File xunitFile) throws IOException {
        List<TestSuite> xunitTestSuites =  loader.loadFrom(xunitFile);
        List<TestOutcome> testOutcomes = new ArrayList<>();
        for(TestSuite testSuite : xunitTestSuites) {
            testOutcomes.addAll(testOutcomesIn(testSuite));
        }
        return NewList.copyOf(testOutcomes);
    }

    private Collection<? extends TestOutcome> testOutcomesIn(TestSuite testSuite) {
        return testSuite.getTestCases().stream()
                .map(testCase -> convertToOutcome(testCase))
                .collect(Collectors.toList());

//        return convert(testSuite.getTestCases(), toTestOutcomes());
    }

    private TestOutcome convertToOutcome(TestCase from) {
        TestOutcome outcome = TestOutcome.forTestInStory(from.getName(), Story.called(from.getClassname()));
        outcome.setTitle(NameConverter.humanize(from.getName()));
        outcome.setDuration(timeAsLong(from.getTime()));

        if (from.getError().isPresent()) {
            TestException failure = from.getError().get();
            outcome.determineTestFailureCause(failure.asException());
        } else if (from.getFailure().isPresent()) {
            TestException failure = from.getFailure().get();
            outcome.determineTestFailureCause(failure.asAssertionFailure());
        } else if (from.getSkipped().isPresent()) {
            //although it is logged by junit as 'skipped', Thucydides
            //makes a distinction between skipped and ignored.
            //outcome.setAnnotatedResult(TestResult.IGNORED);

            //setting the outcome to PENDING for now as the reports don't yet handle the
            //ignored test cases
            outcome.setAnnotatedResult(TestResult.PENDING);
        } else {
            outcome.setAnnotatedResult(TestResult.SUCCESS);
        }
        return outcome;    }

    private long timeAsLong(double time) {
        return (time < 1.0) ? 1 : (long) time;
    }
}
