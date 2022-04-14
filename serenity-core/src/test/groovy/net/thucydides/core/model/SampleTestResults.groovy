package net.thucydides.core.model

import com.google.common.collect.Lists
/**
 * Created by john on 23/07/2016.
 */
class SampleTestResults {
    /*
    + grow_potatoes
        - grow_new_potatoes: 2 tests, 5 steps
        - grow_organic_potatoes: 0 tests
        - grow_sweet_potatoes: 0 tests
    - grow_wheat: 0 tests
    - raise_chickens: 6 tests, 6 steps,
 */

    static def withVariousOutcomes() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("plant potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));
        testOutcome1.recordStep(TestStep.forStepCalled("step 1.1").withResult(TestResult.SUCCESS))
        testOutcome1.recordStep(TestStep.forStepCalled("step 1.2").withResult(TestResult.SUCCESS))
        testOutcome1.recordStep(TestStep.forStepCalled("step 1.3").withResult(TestResult.SUCCESS))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Plant potatoes").andType("story")));
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome1.addVersion("Release 1");
        testOutcome1.addVersion("Iteration 1.1");

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("plant potatoes"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 2.1").withResult(TestResult.SUCCESS))
        testOutcome2.recordStep(TestStep.forStepCalled("step 2.2").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Plant potatoes").andType("story")));
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome2.addVersion("Release 1");
        testOutcome2.addVersion("Iteration 1.1");

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome3.recordStep(TestStep.forStepCalled("step 3").withResult(TestResult.SUCCESS))
        testOutcome3.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome3.addTags(Lists.asList(TestTag.withName("Daily care of chickens").andType("feature")));
        testOutcome3.addTags(Lists.asList(TestTag.withName("Feed chickens").andType("story")));
        testOutcome3.addVersion("Release 1");
        testOutcome3.addVersion("Iteration 1.3");

        TestOutcome testOutcome4 = TestOutcome.forTestInStory("Feed chickens cake", Story.called("Feed chickens"))
        testOutcome4.recordStep(TestStep.forStepCalled("step 4").withResult(TestResult.SUCCESS))
        testOutcome4.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome4.addTags(Lists.asList(TestTag.withName("Daily care of chickens").andType("feature")));
        testOutcome4.addTags(Lists.asList(TestTag.withName("Feed chickens").andType("story")));
        testOutcome4.addVersion("Release 1");
        testOutcome4.addVersion("Iteration 1.3");

        TestOutcome testOutcome5 = TestOutcome.forTestInStory("Feed chickens bread", Story.called("Feed chickens"))
        testOutcome5.recordStep(TestStep.forStepCalled("step 5").withResult(TestResult.FAILURE))
        testOutcome5.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome5.addVersion("Release 2");
        testOutcome5.addVersion("Iteration 2.1");

        TestOutcome testOutcome6 = TestOutcome.forTestInStory("Feed chickens oranges", Story.called("Feed chickens"))
        testOutcome6.recordStep(TestStep.forStepCalled("step 6").withResult(TestResult.PENDING))
        testOutcome6.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome6.addVersion("Release 2");
        testOutcome6.addVersion("Iteration 2.1");

        TestOutcome testOutcome7 = TestOutcome.forTestInStory("Feed chickens apples", Story.called("Feed chickens"))
        testOutcome7.recordStep(TestStep.forStepCalled("step 7").withResult(TestResult.SKIPPED))
        testOutcome7.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome7.addVersion("Release 2");
        testOutcome7.addVersion("Iteration 2.1");

        TestOutcome testOutcome8 = TestOutcome.forTestInStory("Feed chickens peas", Story.called("Feed chickens grain type A"))
        testOutcome8.recordStep(TestStep.forStepCalled("step 8").withResult(TestResult.SUCCESS))
        testOutcome8.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome8.addVersion("Release 3");
        testOutcome8.addVersion("Iteration 3.1");

        TestOutcome testOutcome9 = TestOutcome.forTestInStory("Feed chickens pears", Story.called("Feed chickens grain type B"))
        testOutcome9.recordStep(TestStep.forStepCalled("step 9").withResult(TestResult.SUCCESS))
        testOutcome9.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome9.addVersion("Release 3");
        testOutcome9.addVersion("Iteration 3.1");

        TestOutcome testOutcome10 = TestOutcome.forTestInStory("Feed chickens figs", Story.called("Feed chickens"))
        testOutcome10.recordStep(TestStep.forStepCalled("step 10").withResult(TestResult.SUCCESS))
        testOutcome10.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome6.addVersion("Release 3");
        testOutcome6.addVersion("Iteration 3.2");

        TestOutcome testOutcome11 = TestOutcome.forTestInStory("Feed chickens grain again", Story.called("Feed chickens"))
        testOutcome10.recordStep(TestStep.forStepCalled("step 11").withResult(TestResult.SUCCESS))
        testOutcome10.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome6.addVersion("Release 3");
        testOutcome6.addVersion("Iteration 3.2");

        TestOutcome testOutcome12 = TestOutcome.forTestInStory("Feed chickens grain yet again", Story.called("Feed chickens"))
        testOutcome10.recordStep(TestStep.forStepCalled("step 12").withResult(TestResult.SUCCESS))
        testOutcome10.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome6.addVersion("Release 3");
        testOutcome6.addVersion("Iteration 3.2");

        return [testOutcome1, testOutcome2, testOutcome3, testOutcome4, testOutcome5, testOutcome6, testOutcome7,
                testOutcome8, testOutcome9, testOutcome10, testOutcome11, testOutcome12]

    }

    static def withPassingTestForApples() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("Buying apples in the sun", Story.called("Buying apples"))
        testOutcome1.recordStep(TestStep.forStepCalled("step 1.1").withResult(TestResult.SUCCESS))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Apples").andType("feature")));
        testOutcome1.addTags(Lists.asList(TestTag.withName("Buying apples").andType("story")));

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("Picking apples in the sun", Story.called("Picking apples"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 1.1").withResult(TestResult.FAILURE))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Apples").andType("feature")));
        testOutcome2.addTags(Lists.asList(TestTag.withName("Picking apples").andType("story")));


        return [testOutcome1, testOutcome2]
    }

    static def withPassingTestForApplesAndZucchinis() {

        TestOutcome testOutcome = TestOutcome.forTestInStory("Picking zucchinis in the sun", Story.called("Picking zucchinis"))
        testOutcome.recordStep(TestStep.forStepCalled("step 1.1").withResult(TestResult.SUCCESS))
        testOutcome.addTags(Lists.asList(TestTag.withName("Nice zucchinis").andType("feature")));
        testOutcome.addTags(Lists.asList(TestTag.withName("Picking zucchinis").andType("story")));

        return withPassingTestForApples() + [testOutcome]
    }

}
