package net.serenitybdd.cucumber.outcomes

import net.serenitybdd.cucumber.integration.*
import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestResult
import net.thucydides.model.reports.OutcomeFormat
import net.thucydides.model.reports.TestOutcomeLoader
import org.assertj.core.util.Files
import spock.lang.Specification

import static io.cucumber.junit.CucumberRunner.serenityRunnerForCucumberTestRunner
import static net.thucydides.model.domain.TestResult.*

class WhenCreatingSerenityTestOutcomesForTableDrivenScenarios extends Specification {

    File outputDirectory

    def setup() {
        outputDirectory = Files.newTemporaryFolder()
    }

    /*
          Scenario Outline: Buying lots of widgets
            Given I want to purchase <amount> widgets
            And a widget costs $<cost>
            When I buy the widgets
            Then I should be billed $<total>
          Examples:
          | amount | cost | total |
          | 0      | 10   | 0     |
          | 1      | 10   | 10    |
          | 2      | 10   | 20    |
          | 2      | 0    | 0     |
     */


    def "should run table-driven screenplay scenarios with rows containing failures and errors"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScreenplayTableScenarioWithFailuresAndErrors.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.title == "Buying lots of widgets"

        and: "there should be one step for each row in the table"
        testOutcome.stepCount == 5

        and:
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, FAILURE, SUCCESS, ERROR, SUCCESS]

        and:
        testOutcome.result == ERROR

    }

    def "should run table-driven screenplay scenarios with failure"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScreenplayTableScenarioWithFailures.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.title == "Buying lots of widgets"

        and: "there should be one step for each row in the table"
        testOutcome.stepCount == 4

        and:
        testOutcome.dataTable.rows.collect { it.result } == [FAILURE, SUCCESS, SUCCESS, SUCCESS]

        and:
        testOutcome.result == FAILURE

    }

    def "should run table-driven scenarios successfully"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleTableScenario.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)

        then: "there should a test outcome for each scenario"
        recordedTestOutcomes.size() == 2

        and:
        def testOutcome = recordedTestOutcomes[0]
        testOutcome.title == "Buying lots of widgets"

        and: "there should be one step for each row in the table"
        testOutcome.stepCount == 7

        and: "each of these steps should contain the scenario steps as children"
        def childSteps1 = testOutcome.testSteps[0].children.collect { step -> step.description }
        def childSteps2 = testOutcome.testSteps[1].children.collect { step -> step.description }
        def childSteps3 = testOutcome.testSteps[2].children.collect { step -> step.description }

        childSteps1 == ['Given I have $100','Given I want to purchase 0 widgets', 'And a widget costs $10', 'When I buy the widgets', 'Then I should be billed $0']
        childSteps2 == ['Given I have $100','Given I want to purchase 1 widgets', 'And a widget costs $10', 'When I buy the widgets', 'Then I should be billed $10']
        childSteps3 == ['Given I have $100','Given I want to purchase 2 widgets', 'And a widget costs $10', 'When I buy the widgets', 'Then I should be billed $20']

        and:
        recordedTestOutcomes[0].dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS]
        recordedTestOutcomes[1].dataTable.rows.collect { it.result } == [FAILURE, SUCCESS, SUCCESS]

        and:
        testOutcome.exampleFields == ["amount", "cost","total"]
        testOutcome.dataTable.rows[0].stringValues == ["0","10","0"]
        testOutcome.dataTable.rows[1].stringValues == ["1","10","10"]
        testOutcome.dataTable.rows[2].stringValues == ["2","10","20"]
        testOutcome.dataTable.rows[3].stringValues == ["3","10","30"]
        testOutcome.dataTable.rows[4].stringValues == ["4","0","0"]
        testOutcome.dataTable.rows[5].stringValues == ["50","10","500"]
        testOutcome.dataTable.rows[6].stringValues == ["60","10","600"]
    }

    def "should run table-driven scenarios with failing rows"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleTableScenarioWithFailures.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.title == "Buying lots of widgets"

        and: "there should be one step for each row in the table"
        testOutcome.stepCount == 4

        and:
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, ERROR, FAILURE, SUCCESS]

        and:
        testOutcome.errorMessage.contains "Oh Crap!"

        and:
        testOutcome.result == ERROR

    }

    def "should run table-driven screenplay scenarios with rows containing errors"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(SimpleScreenplayTableScenarioWithErrors.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.title == "Buying lots of widgets"

        and: "there should be one step for each row in the table"
        testOutcome.stepCount == 4

        and:
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, ERROR, SUCCESS]

        and:
        testOutcome.result == ERROR

    }




    def "should handle multiple example tables"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(BasicArithmeticWithTablesScenario.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.title == "Many additions"

        and:
        testOutcome.dataTable.dataSets.size() == 2

        and:
        testOutcome.dataTable.dataSets[0].name == "Single digits"
        testOutcome.dataTable.dataSets[0].description == "With just one digit"
        testOutcome.dataTable.dataSets[0].rows.size() == 2

        and:
        testOutcome.dataTable.dataSets[1].name == "Double digits"
        testOutcome.dataTable.dataSets[1].description == "With more digits than one"
        testOutcome.dataTable.dataSets[1].rows.size() == 3

    }

    def "should handle multiple example tables with backgrounds"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(BasicArithmeticWithTablesAndBackgroundScenario.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.title == "Many additions"

        and:
        testOutcome.dataTable.dataSets.size() == 2

        and:
        recordedTestOutcomes.size() == 1
        testOutcome.stepCount == 5

        and:
        testOutcome.backgroundDescription == "The calculator should be set up and all that"
        and:
        testOutcome.dataTable.dataSets[0].name == "Single digits"
        testOutcome.dataTable.dataSets[0].description == "With just one digit"
        testOutcome.dataTable.dataSets[0].rows.size() == 2

        and:
        testOutcome.dataTable.dataSets[1].name == "Double digits"
        testOutcome.dataTable.dataSets[1].description == "With more digits than one"
        testOutcome.dataTable.dataSets[1].rows.size() == 3

    }

    def "table scenarios throwing PendingException should be reported as Pending"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(TableScenarioThrowingPendingException.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)

        then:

        recordedTestOutcomes.size() == 1
        def testOutcome = recordedTestOutcomes[0]

        and:
        testOutcome.result == TestResult.PENDING
        testOutcome.stepCount == 2

        and:
        testOutcome.dataTable.dataSets.size() == 1

        and:
        testOutcome.dataTable.dataSets[0].rows.size() == 2

        and:
        testOutcome.dataTable.dataSets[0].rows[0].getResult() == TestResult.PENDING
        testOutcome.dataTable.dataSets[0].rows[1].getResult() == TestResult.PENDING

    }

    def "table scenarios marked as @Pending should be reported as Pending"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(TableScenarioMarkedAsPending.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)

        then:

        recordedTestOutcomes.size() == 1
        def testOutcome = recordedTestOutcomes[0]

        and:
        testOutcome.result == TestResult.PENDING
        testOutcome.stepCount == 2

        and:
        testOutcome.dataTable.dataSets.size() == 1

        and:
        testOutcome.dataTable.dataSets[0].rows.size() == 2

        and:
        testOutcome.dataTable.dataSets[0].rows[0].getResult() == TestResult.PENDING
        testOutcome.dataTable.dataSets[0].rows[1].getResult() == TestResult.PENDING

    }

    def "table scenarios marked as @Manual should be reported as manual"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(TableScenarioMarkedAsManual.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)

        then:

        recordedTestOutcomes.size() == 1
        TestOutcome testOutcome = recordedTestOutcomes[0]

        and:
        testOutcome.manual
        testOutcome.result == TestResult.PENDING
        testOutcome.stepCount == 2

        and:
        testOutcome.dataTable.dataSets.size() == 1

        and:
        testOutcome.dataTable.dataSets[0].rows.size() == 2

        and:
        testOutcome.dataTable.dataSets[0].rows[0].getResult() == TestResult.SUCCESS
        testOutcome.dataTable.dataSets[0].rows[1].getResult() == TestResult.SUCCESS

    }

    def "should handle example tables with errors"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(BasicArithmeticWithTablesScenarioWithErrors.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, ERROR, FAILURE, SUCCESS]
    }

    def "should handle example tables with tag on first example"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(RunOnlyFirstExampleTableRows.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.getTags().collect{testTag-> testTag.normalisedName()}.contains("example one")
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS]
    }

    def "should handle example tables with tag on second example"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(RunOnlySecondExampleTableRows.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        testOutcome.getTags().collect{testTag-> testTag.normalisedName()}.contains("example two")
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS]
    }

    def "should handle example tables with tags on scenario outline"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(RunAllExamplesThatInheritsScenarioOutlineTagOnly.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
        testTagNames.contains("example one")
        testTagNames.contains("example two")
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS]
    }

    def "should handle example tables with tags on scenario outline and on example"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(RunExampleMatchingBothScenarioOutlineAndSecondExampleTagOnly.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
        testTagNames.contains("scenario outline")
        testTagNames.contains("example two")
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS]

    }

	def "should run all the examples that inherits a feature tag"() {
		given:
		def runtime = serenityRunnerForCucumberTestRunner(RunAllExamplesThatInheritsFeatureTag.class, outputDirectory)

		when:
		runtime.run()
		def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
		def testOutcome = recordedTestOutcomes[0]

		then:
		def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
		testTagNames.contains("example one")
		testTagNames.contains("example two")
		testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS]
	}

	def "should only run the examples that inherits a feature tag and a scenario outline tag"() {
		given:
		def runtime = serenityRunnerForCucumberTestRunner(RunExamplesMatchingFeatureLevelAndOutlineLevelTags.class, outputDirectory)

		when:
		runtime.run()
		def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
		def testOutcome = recordedTestOutcomes[0]

		then:
		def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
		testTagNames.contains("example one")
		testTagNames.contains("example two")
		testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS]
	}

	def "should only run the examples that inherits a feature tag and has a example table tag"() {
		given:
		def runtime = serenityRunnerForCucumberTestRunner(RunExamplesMatchingFeatureAndExampleTags.class, outputDirectory)

		when:
		runtime.run()
		def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
		def testOutcome = recordedTestOutcomes[0]

		then:
		def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
		testTagNames.contains("example one")
		testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS]
	}

	def "should only run the examples that inherits a feature tag or a scenario outline tag"() {
        given:
		def runtime = serenityRunnerForCucumberTestRunner(RunExamplesMatchingFeatureLevelOrOutlineLevelTags.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
        testTagNames.contains("example one")
        testTagNames.contains("example two")
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS]
   }

   def "should only run the examples that inherits a feature tag or has a example tag"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(RunExamplesMatchingFeatureOrExampleTags.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
        testTagNames.contains("example one")
        testTagNames.contains("example two")
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS, SUCCESS, SUCCESS]
   }

   def "should only run the examples that inherits a feature or a scenario outline tag and a particular example table tag"() {
		given:
		def runtime = serenityRunnerForCucumberTestRunner(RunExamplesMatchingExampleOrScenarioOutlineTagAndAnExampleTag.class, outputDirectory)

		when:
		runtime.run()
		def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
		def testOutcome = recordedTestOutcomes[0]

		then:
		def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
		testTagNames.contains("example two")
		testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS]
	}

   def "should only run the examples that inherits a feature or a scenario outline tag but not a particular example table tag"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(RunExamplesMatchingExampleOrScenarioOutlineTagButNotAnExampleTag.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def testOutcome = recordedTestOutcomes[0]

        then:
        def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
        testTagNames.contains("example two")
        testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS, SUCCESS]
   }

   def "should only run the examples that inherits a feature but not a particular example table tag"() {
		given:
		def runtime = serenityRunnerForCucumberTestRunner(RunExamplesMatchingFeatureTagButNotAnExampleTag.class, outputDirectory)

		when:
		runtime.run()
		def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
		def testOutcome = recordedTestOutcomes[0]

		then:
        recordedTestOutcomes.size() == 1
		def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
		testTagNames.contains("example one")
		testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS]
	}

    def "should also run the examples without tags"() {
        given:
        def runtime = serenityRunnerForCucumberTestRunner(RunExamplesWithoutTags.class, outputDirectory)

        when:
        runtime.run()
        def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
        def firstTestOutcome = recordedTestOutcomes[0]
        def secondTestOutcome = recordedTestOutcomes[1]
        then:
        recordedTestOutcomes.size() == 2
        firstTestOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS]
        secondTestOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS,SUCCESS, SUCCESS, SUCCESS]
    }

	def "should generate correct test outcome when only root directory of feature file is provided"() {
		given:
		def runtime = serenityRunnerForCucumberTestRunner(ScenarioWithOnlyFeatureFileRootDirectoryPath.class, outputDirectory)

		when:
		runtime.run()
		def recordedTestOutcomes = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON).loadFrom(outputDirectory)
		def testOutcome = recordedTestOutcomes[0]

		then:
		def testTagNames = testOutcome.getTags().collect{testTag-> testTag.normalisedName()}
		testTagNames.contains("example one")
		testOutcome.dataTable.rows.collect { it.result } == [SUCCESS, SUCCESS]
	}

}
