package net.thucydides.core.requirements

import com.google.common.collect.Lists
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.issues.IssueTracking
import net.thucydides.core.model.*
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.requirements.reports.RequirementsOutcomes
import net.thucydides.core.requirements.reports.RequirmentsOutcomeFactory
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenGeneratingRequirementsReportData extends Specification {

    public static final String ROOT_DIRECTORY = "annotatedstories"

    def requirementsProviders
    def setup() {
        def vars = new MockEnvironmentVariables()
        vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
        requirementsProviders = [new FileSystemRequirementsTagProvider(), new PackageAnnotationBasedTagProvider(vars)]
    }

    def issueTracking = Mock(IssueTracking)

    def "Should list all top-level capabilities in the capabilities report"() {

        given: "there are no associated tests"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "all the known capabilities should be listed"
            def requirementsNames = outcomes.requirementOutcomes.collect {it.requirement.name}
            requirementsNames == ["Grow cucumbers", "Grow potatoes", "Grow wheat",  "Raise chickens", "Apples", "Nice zucchinis", "Potatoes"]
        and: "the display name should be obtained from the narrative file where present"
            def requirementsDisplayNames = outcomes.requirementOutcomes.collect {it.requirement.displayName}
        requirementsDisplayNames == ["Grow cucumbers", "Grow lots of potatoes", "Grow wheat", "Raise chickens", "apples", "Nice zucchinis", "Potatoes title"]
    }

    def "should report no test results for requirements without associated tests"() {
        given: "there are no associated tests"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the test results for the requirements should be empty"
            def requirementsTestCount = outcomes.requirementOutcomes.collect {it.testOutcomes.total}
            requirementsTestCount == [0,0,0,0,0,0,0]
    }

    def "should report narrative test for each requirement"() {
        given: "we read the requirements from the directory structure"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
            def requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the requirement outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the requirement outcomes will contain the requirement narratives when specified"
            def requirementsNarratives = outcomes.requirementOutcomes.collect {it.requirement.narrative.renderedText}
            requirementsNarratives[1].contains("In order to let my country eat chips") == true
            requirementsNarratives[2] == "Grow wheat"
            requirementsNarratives[3] == "Raise chickens"
    }

    public void "should report test results associated with specified requirements"() {
        given: "we have a set of test outcomes"
            def someTestOutcomes = TestOutcomes.of(someTestResults())
            def requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the requirement outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(someTestOutcomes)
        then: "the number of tests for each requirement should be recorded in the requirements outcome"
            def requirementsTestCount = outcomes.requirementOutcomes.collect {it.testOutcomes.total}
            requirementsTestCount == [0,2,0,0,0,0,0]
    }

    def "a requirement with no associated tests is pending"() {
        given: "there are no associated tests"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the overall outcome should all be pending"
            outcomes.completedRequirementsCount == 0
    }

    def "a requirement with only pending tests is pending"() {
        given: "there are no associated tests"
            def noTestOutcomes = TestOutcomes.of(someTestResults())
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the overall outcome should all be pending"
            outcomes.completedRequirementsCount == 0
    }


    def "a requirement with only passing tests is completed"() {
        given: "there are some passing tests"
            def noTestOutcomes = TestOutcomes.of(somePassingTestResults())
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "requirements with passing tests should be completed"
            outcomes.completedRequirementsCount == 1
    }

    def "a requirement with a failing tests is a failure"() {
        given: "there are some passing tests"
            def noTestOutcomes = TestOutcomes.of(someFailingTestResults())
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "requirements with passing tests should be completed"
            outcomes.failingRequirementsCount == 1
    }

    def "a requirement with a pending test is pending"() {
        given: "there are some passing tests"
        def noTestOutcomes = TestOutcomes.of(somePendingTestResults())
        and: "we read the requirements from the directory structure"
        RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the capability outcomes"
        RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "requirements with pending tests should be pending"
        outcomes.pendingRequirementsCount == 6
    }

    def "should report on the number of passing, failing and pending tests for a requirement"() {
        given: "there are some test results"
            def noTestOutcomes = TestOutcomes.of(someVariedTestResults())
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the number of failing, passing and total tests should be reported"
            outcomes.total.withResult(TestResult.SUCCESS) == 9
            outcomes.total.withResult(TestResult.FAILURE) == 1
            outcomes.total.withResult(TestResult.PENDING) == 1
            outcomes.total.withResult(TestResult.SKIPPED) == 1
            outcomes.totalTestCount == 12
    }

    /*
        + grow_potatoes (2 requirements without tests -> 6 unimplemented tests)
            - grow_new_potatoes: 2 tests, 5 steps, 100% success
            - grow_organic_potatoes: 0 tests
            - grow_sweet_potatoes: 0 tests
        - grow_wheat: 0 tests
        - raise_chickens: 8 tests, 8 steps, 37.5% success

        - Total requirements with no tests: 3
        - Estimated tests for requirements with no tests: 3 * 5 = 15
        - Total implemented tests: 10
        - Total implemented and estimated tests: 25
        - Total passing tests: 6
        - Percentage passing tests:  = 24%
     */
    def "functional coverage should cater for requirements with no tests"() {
        given: "there are some test results"
            def noTestOutcomes = TestOutcomes.of(someVariedTestResults())
        and: "we have configured 5 tests per unimplemented requirement"
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("thucydides.estimated.tests.per.requirement", "3")
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the proportionOf of failing, passing and total steps should include estimations for requirements with no tests"
            outcomes.proportion.withResult(TestResult.SUCCESS) ==0.1875
            outcomes.proportion.withResult(TestResult.FAILURE) > 0.0
            outcomes.proportion.withIndeterminateResult() > 0.0
        and: "the number of requirements should be available"
            outcomes.flattenedRequirementCount == 14
            outcomes.requirementsWithoutTestsCount == 12
        and: "the number of tests should be available"
            outcomes.total.total == 12
            outcomes.total.withResult(TestResult.SUCCESS) == 9
            outcomes.total.withResult(TestResult.FAILURE) == 1
            outcomes.total.withResult(TestResult.PENDING) == 1
            outcomes.total.withResult(TestResult.SKIPPED) == 1
            outcomes.estimatedUnimplementedTests == 36
        and: "the results should be available as formatted values"
            outcomes.formattedPercentage.withResult(TestResult.SUCCESS) == "18.8%"
        and: "we can also display the test results by type"
            outcomes.getFormattedPercentage(TestType.ANY).withResult(TestResult.SUCCESS) == "18.8%"
            outcomes.getFormattedPercentage("ANY").withResult(TestResult.SUCCESS) == "18.8%"
    }

    def "functional coverage should cater for requirements with no tests at the requirement outcome level"() {
        given: "there are some test results"
            def noTestOutcomes = TestOutcomes.of(someVariedTestResults())
        and: "we have configured 2 tests per unimplemented requirement"
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("thucydides.estimated.tests.per.requirement", "2")
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables)
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then: "the proportionOf of failing, passing and total steps should include estimations for requirements with no tests"
            outcomes.requirementOutcomes[1].percent.withResult(TestResult.SUCCESS) == 0.2
            outcomes.requirementOutcomes[1].percent.withResult(TestResult.FAILURE)  == 0.0
            outcomes.requirementOutcomes[1].percent.withResult(TestResult.ERROR)  == 0.0
            outcomes.requirementOutcomes[1].percent.withIndeterminateResult()  == 0.8
        and: "the number of requirements should be available"
            outcomes.requirementOutcomes[1].flattenedRequirementCount == 5
        and: "the number of implemented tests should be available"
            outcomes.requirementOutcomes[1].testCount == 2
            outcomes.requirementOutcomes[1].total.withResult(TestResult.SUCCESS) == 2
            outcomes.requirementOutcomes[1].total.withResult(TestResult.FAILURE) == 0
            outcomes.requirementOutcomes[1].total.withResult(TestResult.ERROR) == 0
            outcomes.requirementOutcomes[1].total.withIndeterminateResult() == 0
        and: "the number of requirements without tests should be available"
            outcomes.requirementOutcomes[1].requirementsWithoutTestsCount == 4
        and: "the estimated unimplemented test count should be available"
            outcomes.requirementOutcomes[1].estimatedUnimplementedTests == 8
        and: "the results should be available as formatted values"
            outcomes.requirementOutcomes[1].formattedPercentage.withResult(TestResult.SUCCESS) == "20%"
            outcomes.requirementOutcomes[1].formattedPercentage.withResult(TestResult.FAILURE) == "0%"
            outcomes.requirementOutcomes[1].formattedPercentage.withResult(TestResult.ERROR) == "0%"
            outcomes.requirementOutcomes[1].formattedPercentage.withIndeterminateResult() == "80%"
    }

    def "should get test outcomes for a given release"() {
        given: "there are some test results"
            def testOutcomes = TestOutcomes.of(someVariedTestResults())
            def environmentVariables = new MockEnvironmentVariables()
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables)
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
        when:
            RequirementsOutcomes releasedOutcomes = outcomes.getReleasedRequirementsFor(Release.ofVersion("Release 1"))
        then:
            releasedOutcomes.requirementOutcomes.size() == 2
        and:
            releasedOutcomes.flattenedRequirementOutcomes.size() == 5
        and:
            releasedOutcomes.testOutcomes.outcomes.size() == 4
    }


    def "should get test outcomes for a given nested release"() {
        given: "there are some test results"
            def testOutcomes = TestOutcomes.of(someVariedTestResults())
            def environmentVariables = new MockEnvironmentVariables()
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables)
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
        when:
            RequirementsOutcomes releasedOutcomes = outcomes.getReleasedRequirementsFor(Release.ofVersion("Iteration 1.1"))
        then:
            releasedOutcomes.requirementOutcomes.size() == 1
        and:
            releasedOutcomes.flattenedRequirementOutcomes.size() == 3
        and:
            releasedOutcomes.testOutcomes.outcomes.size() == 2
    }

    def "should get all nested test outcomes for a given release"() {
        given: "there are some test results"
            def testOutcomes = TestOutcomes.of(someVariedTestResults())
            def environmentVariables = new MockEnvironmentVariables()
        and: "we read the requirements from the directory structure"
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables)
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
        when:
            RequirementsOutcomes releasedOutcomes = outcomes.getReleasedRequirementsFor(Release.ofVersion("Release 3"))
        then:
            releasedOutcomes.requirementOutcomes.size() == 1
        and:
            releasedOutcomes.flattenedRequirementOutcomes.size() ==2
        and:
            releasedOutcomes.testOutcomes.outcomes.size() == 3
    }

    def someTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        return [testOutcome1, testOutcome2]

    }

    def somePassingTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome1.recordStep(TestStep.forStepCalled("step 1").withResult(TestResult.SUCCESS))

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 2").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 3").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        return [testOutcome1, testOutcome2, testOutcome3]

    }

    def somePendingTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome1.recordStep(TestStep.forStepCalled("step 1").withResult(TestResult.PENDING))

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 2").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 3").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        return [testOutcome1, testOutcome2, testOutcome3]

    }


    def someFailingTestResults() {
        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("capability")));
        testOutcome1.recordStep(TestStep.forStepCalled("step 1").withResult(TestResult.SUCCESS))

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.recordStep(TestStep.forStepCalled("step 2").withResult(TestResult.SUCCESS))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
        testOutcome3.recordStep(TestStep.forStepCalled("step 3").withResult(TestResult.SUCCESS))
        testOutcome3.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        TestOutcome testOutcome4 = TestOutcome.forTestInStory("Feed chickens cake", Story.called("Feed chickens"))
        testOutcome4.recordStep(TestStep.forStepCalled("step 4").withResult(TestResult.FAILURE))
        testOutcome4.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));

        return [testOutcome1, testOutcome2, testOutcome3, testOutcome4]

    }

    /*
        + grow_potatoes
            - grow_new_potatoes: 2 tests, 5 steps
            - grow_organic_potatoes: 0 tests
            - grow_sweet_potatoes: 0 tests
        - grow_wheat: 0 tests
        - raise_chickens: 6 tests, 6 steps,
     */
    def someVariedTestResults() {
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

        TestOutcome testOutcome8 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens grain type A"))
        testOutcome8.recordStep(TestStep.forStepCalled("step 8").withResult(TestResult.SUCCESS))
        testOutcome8.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome8.addVersion("Release 3");
        testOutcome8.addVersion("Iteration 3.1");

        TestOutcome testOutcome9 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens grain type B"))
        testOutcome9.recordStep(TestStep.forStepCalled("step 9").withResult(TestResult.SUCCESS))
        testOutcome9.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        testOutcome9.addVersion("Release 3");
        testOutcome9.addVersion("Iteration 3.1");

        TestOutcome testOutcome10 = TestOutcome.forTestInStory("Feed chickens grain", Story.called("Feed chickens"))
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

}
