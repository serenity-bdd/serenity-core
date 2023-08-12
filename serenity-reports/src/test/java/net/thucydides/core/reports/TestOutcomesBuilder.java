package net.thucydides.core.reports;

import net.serenitybdd.annotations.Feature;
import net.thucydides.model.domain.*;
import net.thucydides.model.reports.TestOutcomes;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.model.domain.TestResult.FAILURE;
import static net.thucydides.model.domain.TestResult.SUCCESS;

public class TestOutcomesBuilder {

    public final static ZonedDateTime EARLY_DATE = ZonedDateTime.of(2013, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
    public final static ZonedDateTime LATE_DATE =  ZonedDateTime.of(2013, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault());

    @Feature
    class WidgetFeature {
        class PurchaseNewWidget {
        }
    }

    public TestOutcomes getDefaultResults() {
        List<TestOutcome> testOutcomeList = new ArrayList<TestOutcome>();

        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        testOutcomeList.add(thatSucceedsFor(story, 10));
        testOutcomeList.add(thatSucceedsFor(story, 20));
        testOutcomeList.add(thatIsFailingFor(story, 30));
        testOutcomeList.add(thatIsPendingFor(story, 0));
        testOutcomeList.add(thatIsPendingFor(story, 0));
        testOutcomeList.add(thatIsPendingFor(story, 0));

        testOutcomeList.add(thatIsFailingFor(story, 10));
        testOutcomeList.add(thatIsFailingFor(story, 20));
        testOutcomeList.add(thatIsFailingFor(story, 30));
        testOutcomeList.add(thatIsIgnoredFor(story, 10));
        testOutcomeList.add(thatIsPendingFor(story, 0));
        testOutcomeList.add(thatIsPendingFor(story, 0));

        return TestOutcomes.of(testOutcomeList);
    }

    public TestOutcomes getDataDrivenResults() {
        List<TestOutcome> testOutcomeList = new ArrayList<TestOutcome>();

        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        testOutcomeList.add(thatIsADataDrivenStory(story));
        testOutcomeList.add(thatIsADataDrivenStory(story));

        return TestOutcomes.of(testOutcomeList);
    }

    public TestOutcome thatSucceedsFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for (int i = 1; i <= stepCount; i++) {
            testOutcome.recordStep(TestStepFactory.forASuccessfulTestStepCalled("Step " + i));
        }
        testOutcome.setStartTime(EARLY_DATE);
        return testOutcome;
    }

    public TestOutcome thatIsPendingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for (int i = 1; i <= stepCount; i++) {
            testOutcome.recordStep(TestStepFactory.forAPendingTestStepCalled("Step " + i));
        }
        testOutcome.setStartTime(LATE_DATE);
        testOutcome.setResult(TestResult.PENDING);
        return testOutcome;
    }

    public TestOutcome thatIsIgnoredFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for (int i = 1; i <= stepCount; i++) {
            testOutcome.recordStep(TestStepFactory.forAnIgnoredTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    public TestOutcome thatIsFailingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for (int i = 1; i <= stepCount; i++) {
            testOutcome.recordStep(TestStepFactory.forABrokenTestStepCalled("Step " + i, new AssertionError()));
        }
        testOutcome.setStartTime(LATE_DATE);
        return testOutcome;
    }

    public TestOutcome thatIsADataDrivenStory(Story story) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        testOutcome.addDataFrom(DataTable.withHeaders(Arrays.asList("A","B"))
                .andRows(Arrays.asList(
                        Arrays.asList("1","2"),
                        Arrays.asList("3","4"),
                        Arrays.asList("5","6"),
                        Arrays.asList("7","8")
                )).build());

        testOutcome.getDataTable().getRows().get(0).setResult(SUCCESS);
        testOutcome.getDataTable().getRows().get(1).setResult(SUCCESS);
        testOutcome.getDataTable().getRows().get(2).setResult(SUCCESS);
        testOutcome.getDataTable().getRows().get(3).setResult(FAILURE);

        testOutcome.recordStep(TestStep.forStepCalled("Test 1").withResult(SUCCESS));
        testOutcome.recordStep(TestStep.forStepCalled("Test 2").withResult(SUCCESS));
        testOutcome.recordStep(TestStep.forStepCalled("Test 3").withResult(SUCCESS));
        testOutcome.recordStep(TestStep.forStepCalled("Test 4").withResult(FAILURE));

        testOutcome.setStartTime(LATE_DATE);
        testOutcome.setResult(FAILURE);
        return testOutcome;
    }

}
