package net.serenitybdd.cucumber.outcomes

import net.serenitybdd.cucumber.integration.*
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestResult
import net.thucydides.core.model.TestStep
import net.thucydides.core.model.TestTag
import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import net.thucydides.core.steps.TestSourceType
import org.assertj.core.util.Files
import spock.lang.Specification

import static io.cucumber.junit.CucumberRunner.serenityRunnerForCucumberTestRunner

class WhenCreatingSerenityTestOutcomes extends Specification {

    File outputDirectory

    def setup() {
        outputDirectory = Files.newTemporaryFolder()
    }


    def "should record failures for a failing scenario"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(FailingScenario.class, outputDirectory);

        when:
        runtime.run();
        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        TestOutcome testOutcome = recordedTestOutcomes[0]
        List<TestStep> stepResults = testOutcome.testSteps.collect { step -> step.result }

        then:
        testOutcome.result == TestResult.FAILURE
        and:
        stepResults == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS, TestResult.FAILURE, TestResult.SKIPPED]
    }
    /*
    Feature: A simple feature

      Scenario: A simple scenario
        Given I want to purchase 2 widgets
        And a widget costs $5
        When I buy the widgets
        Then I should be billed $10
     */

    def "should generate a well-structured Serenity test outcome for each executed Cucumber scenario"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenario.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]
        def steps = testOutcome.testSteps.collect { step -> step.description }

        then:
        testOutcome.title == "A simple scenario"

        and:
        testOutcome.stepCount == 4
        steps == ['Given I want to purchase 2 widgets', 'And a widget costs $5', 'When I buy the widgets', 'Then I should be billed $10']
    }

    def "should record results for each step"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenario.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.result == TestResult.SUCCESS

        and:
        testOutcome.testSteps.collect { step -> step.result } == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS]
    }

    def "should record failures for failing scenario outlines"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(FailingScenarioOutline.class, outputDirectory);

        when:
        runtime.run();
        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        TestOutcome testOutcome = recordedTestOutcomes[0]
        List<TestStep> stepResults = testOutcome.testSteps.collect { step -> step.result }

        then:
        testOutcome.result == TestResult.FAILURE
        and:
        stepResults == [TestResult.FAILURE, TestResult.SUCCESS]
        and:
        testOutcome.testSteps[0].description.contains("1: A simple failing scenario outline")
        testOutcome.testSteps[1].description.contains("2: A simple failing scenario outline")
    }

    def "should record a feature tag based on the name of the feature when the feature name is different from the feature file name"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenarioWithALongName.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        TestOutcome testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.featureTag.get() == TestTag.withName("Samples/A simple feature showing how features can have long names").andType("feature")
    }

    def "should record the capability tag based on the directory of the feature if known"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenarioWithALongName.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        TestOutcome testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.featureTag.get() == TestTag.withName("Samples/A simple feature showing how features can have long names").andType("feature")
    }

    def "should record background steps"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(ScenariosWithTableInBackgroundSteps.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.stepCount == 4
    }

    def "should record table data in steps"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(ScenariosWithTableInBackgroundSteps.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.testSteps[0].description.contains("Given the following customers exist:")
        testOutcome.testSteps[0].description.contains("| Name | DOB | Mobile Phone | Home Phone | Work Phone | Address Line 1 | Address Line 2 |")
        testOutcome.testSteps[0].description.contains("| SEAN PAUL | 30/05/1978 | 860123334 | 1234567899 | 16422132 | ONE BBI ACC | BEACON SOUTH |")
        testOutcome.testSteps[0].description.contains("| TONY SMITH | 10/10/1975 | 86123335 | 11255555 | 16422132 | 1 MAIN STREET | BANKCENTRE |")
        testOutcome.testSteps[0].description.contains("| PETE FORD | 12/03/1970 | 865555551 | 15555551 | 15555551 | Q6B HILL ST | BLACKROCK |")
        testOutcome.testSteps[0].description.contains("| JOHN B JOVI | 22/08/1957 | 871274762 |  | 16422132 | BLAKBURN | TALLAGHT |")
        testOutcome.testSteps[0].description.contains("| JOHN ANFIELD | 20/05/1970 | 876565656 | 015555551 | 214555555 | DUBLIN | DUBLIN |")
    }

/*
@flavor:strawberry
Feature: A simple feature with tags
  This is about selling widgets
  @shouldPass
  @color:red
  @in-progress
  ...
 */

    def "should record any provided tags"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenarioWithTags.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.tags.size() == 4
        and:
        testOutcome.tags.contains(TestTag.withName("strawberry").andType("flavor"))
        testOutcome.tags.contains(TestTag.withName("red").andType("color"))
        testOutcome.tags.contains(TestTag.withName("shouldPass").andType("tag"))
        testOutcome.tags.contains(TestTag.withName("in-progress").andType("tag"))
    }

    def "should record the narrative text"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenarioWithNarrativeTexts.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.userStory.narrative == "  This is about selling widgets"
    }

    def "should record the scenario description text for a scenario"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenarioWithNarrativeTexts.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.description == """    A description of this scenario
    It goes for two lines"""
    }

    def "should record pending and skipped steps for a pending scenario"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(PendingScenario.class, outputDirectory);

        when:
        runtime.run();
        List<TestOutcome> recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        TestOutcome testOutcome = recordedTestOutcomes[0]
        List<TestStep> stepResults = testOutcome.testSteps.collect { step -> step.result }

        then:
        testOutcome.result == TestResult.PENDING
        and:
        stepResults == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.PENDING, TestResult.IGNORED]
    }

    def "should generate a well-structured Thucydides test outcome for feature files with several Cucumber scenario"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(MultipleScenarios.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:

        recordedTestOutcomes.size() == 2

        def testOutcome1 = recordedTestOutcomes[0]
        def steps1 = testOutcome1.testSteps.collect { step -> step.description }

        def testOutcome2 = recordedTestOutcomes[1]
        def steps2 = testOutcome2.testSteps.collect { step -> step.description }

        and:
        testOutcome1.title == "Simple scenario 1"
        testOutcome1.result == TestResult.FAILURE

        and:
        testOutcome2.title == "Simple scenario 2"
        testOutcome2.result == TestResult.SUCCESS

        and:
        steps1 == ['Given I want to purchase 2 widgets', 'And a widget costs $5', 'When I buy the widgets', 'Then I should be billed $50']
        steps2 == ['Given I want to purchase 4 widgets', 'And a widget costs $3', 'When I buy the widgets', 'Then I should be billed $12']
    }

    def "should generate outcomes for scenarios with a background section"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(BasicArithmeticScenario.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:
        recordedTestOutcomes.size() == 4

        and:
        recordedTestOutcomes.collect { it.name } == ["Addition", "Another Addition", "Many additions", "Many additions"]
        and:
        recordedTestOutcomes[0].stepCount == 3
        recordedTestOutcomes[1].stepCount == 3
        recordedTestOutcomes[2].stepCount == 5
        recordedTestOutcomes[3].stepCount == 5
    }

    def "should read @issue tags"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(BasicArithmeticScenario.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:
        recordedTestOutcomes.each { outcome ->
            outcome.tags.contains(TestTag.withName("ISSUE-123").andType("issue"))
        }
        and:
        recordedTestOutcomes[0].tags.contains(TestTag.withName("ISSUE-456").andType("issue"))
    }

    def "should fill @issue keys"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(BasicArithmeticScenario.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:
        recordedTestOutcomes.each { outcome ->
            outcome.tags.contains(TestTag.withName("ISSUE-123").andType("issue"))
            outcome.getIssueKeys().contains("ISSUE-123");
        }
        and:
        recordedTestOutcomes[0].tags.contains(TestTag.withName("ISSUE-456").andType("issue"))
        recordedTestOutcomes[0].getIssueKeys().contains("ISSUE-123");
        recordedTestOutcomes[0].getIssueKeys().contains("ISSUE-456");
    }

    def "should fill @issues keys"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(FeatureWithMoreIssuesTag.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:
        recordedTestOutcomes.each { outcome ->
            outcome.tags.contains(TestTag.withName("ISSUE-123,ISSUE-789").andType("issues"))
            outcome.getIssueKeys().contains("ISSUE-123");
            outcome.getIssueKeys().contains("ISSUE-789");
        }
        and:
        recordedTestOutcomes[0].tags.contains(TestTag.withName("ISSUE-456,ISSUE-001").andType("issues"))
        recordedTestOutcomes[0].getIssueKeys().contains("ISSUE-456");
        recordedTestOutcomes[0].getIssueKeys().contains("ISSUE-001");
        recordedTestOutcomes[0].getIssueKeys().contains("ISSUE-123");
        recordedTestOutcomes[0].getIssueKeys().contains("ISSUE-789");
    }

    def "scenarios with the @pending tag should be reported as Pending"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(MultipleScenariosWithPendingTag.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:

        recordedTestOutcomes.size() == 2

        def testOutcome1 = recordedTestOutcomes[0]
        def steps1 = testOutcome1.testSteps.collect { step -> step.description }

        def testOutcome2 = recordedTestOutcomes[1]
        def steps2 = testOutcome2.testSteps.collect { step -> step.description }

        and:
        testOutcome1.title == "Simple scenario 1"
        testOutcome1.result == TestResult.PENDING

        and:
        testOutcome2.title == "Simple scenario 2"
        testOutcome2.result == TestResult.PENDING

        and:
        steps1 == ['Given I want to purchase 2 widgets', 'And a widget costs $5', 'When I buy the widgets', 'Then I should be billed $50']
        steps2 == ['Given I want to purchase 4 widgets', 'And a widget costs $3', 'When I buy the widgets', 'Then I should be billed $12']
    }


    def "individual scenarios with the @pending tag should be reported as Pending"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(ScenariosWithPendingTag.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:

        recordedTestOutcomes.size() == 3
        def testOutcome1 = recordedTestOutcomes[0]
        def testOutcome2 = recordedTestOutcomes[1]
        def testOutcome3 = recordedTestOutcomes[2]

        and:
        testOutcome1.result == TestResult.SUCCESS
        testOutcome2.result == TestResult.PENDING
        testOutcome3.result == TestResult.SUCCESS
    }

    def "Scenarios in a feature file with the @pending tag should all be reported as Pending"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(FeatureWithPendingTag.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:

        recordedTestOutcomes.size() == 3
        def testOutcome1 = recordedTestOutcomes[0]
        def testOutcome2 = recordedTestOutcomes[1]
        def testOutcome3 = recordedTestOutcomes[2]

        and:
        testOutcome1.result == TestResult.PENDING
        testOutcome2.result == TestResult.PENDING
        testOutcome3.result == TestResult.PENDING
    }


    def "individual scenarios with the @wip tag should be reported as Skipped"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(ScenariosWithSkippedTag.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:

        recordedTestOutcomes.size() == 3
        def testOutcome1 = recordedTestOutcomes[0]
        def testOutcome2 = recordedTestOutcomes[1]
        def testOutcome3 = recordedTestOutcomes[2]

        and:
        testOutcome1.result == TestResult.SUCCESS
        testOutcome2.result == TestResult.SKIPPED
        testOutcome3.result == TestResult.SUCCESS
    }

    def "scenarios with the @wip tag should be reported as skipped"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(MultipleScenariosWithSkippedTag.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:

        recordedTestOutcomes.size() == 2

        def testOutcome1 = recordedTestOutcomes[0]
        def steps1 = testOutcome1.testSteps.collect { step -> step.description }

        def testOutcome2 = recordedTestOutcomes[1]
        def steps2 = testOutcome2.testSteps.collect { step -> step.description }

        and:
        testOutcome1.title == "Simple scenario 1"
        testOutcome1.result == TestResult.SKIPPED

        and:
        testOutcome2.title == "Simple scenario 2"
        testOutcome2.result == TestResult.SKIPPED

        and:
        steps1 == ['Given I want to purchase 2 widgets', 'And a widget costs $5', 'When I buy the widgets', 'Then I should be billed $50']
        steps2 == ['Given I want to purchase 4 widgets', 'And a widget costs $3', 'When I buy the widgets', 'Then I should be billed $12']
    }

    def "throwing PendingException should be reported as Pending"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(ScenarioThrowingPendingException.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name }

        then:

        recordedTestOutcomes.size() == 1
        def testOutcome1 = recordedTestOutcomes[0]

        and:
        testOutcome1.result == TestResult.PENDING

    }


    def "should store correct test source in outcome"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenario.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.result == TestResult.SUCCESS

        and:
        testOutcome.getTestSource() == TestSourceType.TEST_SOURCE_CUCUMBER.getValue()
    }

    def "should run steps defined as lambdas"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenarioWithLambdaSteps.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.result == TestResult.SUCCESS
    }

    def "should contain rule information in test outcome"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScenarioWithRules.class, outputDirectory);

        when:
        runtime.run();
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory).sort { it.name };
        def firstTestOutcome = recordedTestOutcomes[0]
        def secondTestOutcome = recordedTestOutcomes[1]

        then:
        firstTestOutcome.rule.getName() == "This is a simple rule"
        firstTestOutcome.rule.getDescription().trim() == "Simple first rule description";
        secondTestOutcome.rule.getName() == "This is a simple second rule"
        secondTestOutcome.rule.getDescription().trim() == "Simple second rule description";
    }
}