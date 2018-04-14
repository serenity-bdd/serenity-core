package net.thucydides.core.model

import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.ExecutedStepDescription
import net.thucydides.core.steps.StepEventBus
import net.thucydides.core.steps.StepFailure
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

import static net.thucydides.core.model.TestResult.*

class WhenRecordingDataDrivenTestOutcomes extends Specification {

    def setup() {
        StepEventBus.eventBus.reset();
    }

    def "Test outcomes should not have a data-driven table by default"()  {
        when:
            def testOutcome = new TestOutcome("someTest")
        then:
            !testOutcome.dataDriven
    }

    def "Test outcome with a data-driven table recorded should make this known"() {
        given:
            def testOutcome = new TestOutcome("someTest")
        when:
            def dataTable = DataTable.withHeaders(["firstName","lastName","age"]).build()
            testOutcome.useExamplesFrom(dataTable)
        then:
            testOutcome.dataDriven
    }

    def "Should be able to build a data table with headings"() {
        when:
            def table = DataTable.withHeaders(["firstName","lastName","age"]).build()
        then:
            table.headers == ["firstName","lastName","age"]
            table.rows == []
    }

    def "Should be able to build a data table with headings and rows"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                              andRows([["Joe", "Smith",20],
                                       ["Jack", "Jones",21]]).build();
        then:
        table.headers == ["firstName","lastName","age"]
        table.rows.collect {it.values} ==[["Joe","Smith",20], ["Jack","Jones",21]]

    }

    def "Should be able restore variables in the step description"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andRows([["Joe", "Smith",20],
                         ["Jack", "Jones",21]]).build();
        then:
        table.restoreVariablesIn("A person named Joe Smith") == "A person named <firstName> <lastName>"
    }

    def "Should be able restore variables in the step description with funny characters"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andRows([["[a, b, c]", ".*",20],
                         ["Jack", "Jones",21]]).build();
        then:
        table.restoreVariablesIn("A person named Joe Smith") == "A person named Joe Smith"
    }

    def "Should be able restore variables in the step description and only include separate words"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andRows([["Joe", "Smith",20],
                         ["Jack", "Jones",21]]).build();
        then:
        table.restoreVariablesIn("Joesphine and Joe Smith") == "Joesphine and <firstName> <lastName>"
    }


    def "A data table should have a single data set by default"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andRows([["Joe", "Smith",20],
                         ["Jack", "Jones",21]]).build();
        then:
        table.dataSets.size() == 1
        and:
        table.dataSets[0].rows == table.rows
    }

    def "A data set can have a name and description"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andRows([["Joe", "Smith",20],
                         ["Jack", "Jones",21]])
                .andTitle("a title")
                .andDescription("a description")
                .build();
        then:
        table.dataSets.size() == 1
        and:
        table.dataSets[0].name == "a title"
        table.dataSets[0].description == "a description"
    }

    def "A data table can have several data sets"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andRows([["Joe", "Smith",20],
                         ["Jack", "Jones",21]])
                .andTitle("a title")
                .andDescription("a description")
                .build();
        and:
        table.startNewDataSet("another title","another description")
        table.addRows([new DataTableRow(["Jane", "Smith",20]),
                       new DataTableRow(["Jill", "Jones",21]),
                       new DataTableRow(["Jodie", "Jacobs",21])])
        then:
        table.dataSets.size() == 2
        table.rows.size() == 5
        and:
        table.dataSets[0].name == "a title"
        table.dataSets[0].description == "a description"
        table.dataSets[0].startRow == 0
        table.dataSets[0].rows.size() == 2
        and:
        table.dataSets[1].name == "another title"
        table.dataSets[1].description == "another description"
        table.dataSets[1].startRow == 2
        table.dataSets[1].rows.size() == 3
    }

    def "Should be able to build a data table with headings and mapped rows"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andMappedRows([["firstName":"Joe",  "lastName":"Smith","age":20],
                               ["firstName":"Jack", "lastName":"Jones","age":21]]).build();
        then:
        table.headers == ["firstName","lastName","age"]
        table.rows.collect {it.values} ==[["Joe","Smith",20], ["Jack","Jones",21]]
    }

    def "row results should be undefined by default"() {
        when:
        def table = DataTable.withHeaders(["firstName","lastName","age"]).
                andMappedRows([["firstName":"Joe",  "lastName":"Smith","age":20],
                        ["firstName":"Jack", "lastName":"Jones","age":21]]).build();
        then:
        table.rows.collect {it.result} ==[UNDEFINED, UNDEFINED]
    }

    def "should be able to define the outcome by default"() {
        given:
            def table = DataTable.withHeaders(["firstName","lastName","age"]).
                    andMappedRows([["firstName":"Joe",  "lastName":"Smith","age":20],
                            ["firstName":"Jack", "lastName":"Jones","age":21]]).build();
        when:
            table.row(0).hasResult(FAILURE)
            table.row(1).hasResult(PENDING)
        then:
            table.rows.collect {it.result} ==[FAILURE, PENDING]
    }

    def "should be able to define the outcome for successive rows"() {
        given:
            def table = DataTable.withHeaders(["firstName","lastName","age"]).
                    andMappedRows([["firstName":"Joe",  "lastName":"Smith","age":20],
                                   ["firstName":"Jack", "lastName":"Jones","age":21]]).build();
        when:
            table.currentRow().hasResult(FAILURE)
            table.nextRow()
            table.currentRow().hasResult(PENDING)
        then:
            table.rows.collect {it.result} ==[FAILURE, PENDING]
    }

    def "should be able to add rows incrementally"() {
        given:
            def table = DataTable.withHeaders(["firstName","lastName","age"]).build();
        when:
            table.addRow(["firstName":"Joe",  "lastName":"Smith","age":20])
            table.currentRow().hasResult(FAILURE)
            table.nextRow()
            table.addRow(["firstName":"Jack", "lastName":"Jones","age":21])
            table.currentRow().hasResult(PENDING)
        then:
            table.rows.collect {it.result} ==[FAILURE, PENDING]
    }

    def outputDirectory = Mock(File);
    def environmentVariables = new MockEnvironmentVariables()

    def "Should be able to describe an example table via the event bus"() {
        given:
            def eventBus = new StepEventBus(environmentVariables)
            def BaseStepListener listener = new BaseStepListener(outputDirectory)
            eventBus.registerListener(listener)
        when:
            eventBus.testSuiteStarted(Story.called("A data driven test suite"))
            eventBus.testStarted("aDataDrivenTest")
            eventBus.useExamplesFrom(DataTable.withHeaders(["firstName","lastName","age"]).
                                               andRows([["Joe", "Smith",20],
                                                       ["Jack", "Jones",21]]).build())
        then:
            listener.testOutcomes[0].isDataDriven()
        and:
            listener.testOutcomes[0].dataTable
    }

    def "Should be able to describe a set of example tables via the event bus"() {
        given:
            def eventBus = new StepEventBus(environmentVariables)
            def BaseStepListener listener = new BaseStepListener(outputDirectory)
            eventBus.registerListener(listener)
        when:
            eventBus.testSuiteStarted(Story.called("A data driven test suite"))
            eventBus.testStarted("aDataDrivenTest")
            eventBus.useExamplesFrom(DataTable.withHeaders(["firstName","lastName","age"]).
                    andRows([["Joe", "Smith",20],
                             ["Jack", "Jones",21]]).build())
            int currentRow = listener.testOutcomes[0].dataTable.currentRowNumber()
        and:
            eventBus.addNewExamplesFrom(DataTable.withHeaders(["firstName","lastName","age"]).
                    andRows([["Joe", "Smith",20],
                             ["Jack", "Jones",21],
                             ["Jill", "Jones",22],
                             ["Jane", "Jones",22],
                             ["Mary", "Jones",22]]).build())
        then:
            listener.testOutcomes[0].isDataDriven()
        and:
            listener.testOutcomes[0].dataTable.rows.size() == 5
        and:
            listener.testOutcomes[0].dataTable.currentRowNumber() == currentRow
    }

    def "Should not add rows that already exist"() {
        given:
        def eventBus = new StepEventBus(environmentVariables)
        def BaseStepListener listener = new BaseStepListener(outputDirectory)
        eventBus.registerListener(listener)
        when:
        eventBus.testSuiteStarted(Story.called("A data driven test suite"))
        eventBus.testStarted("aDataDrivenTest")
        eventBus.useExamplesFrom(DataTable.withHeaders(["firstName","lastName","age"]).
                andRows([["Joe", "Smith",20],
                         ["Jack", "Jones",21]]).build())
        and:
        eventBus.addNewExamplesFrom(DataTable.withHeaders(["firstName","lastName","age"]).
                andRows([["Joe", "Smith",20],
                         ["Jack", "Jones",21]]).build())

        then:
        listener.testOutcomes[0].isDataDriven()
        and:
        listener.testOutcomes[0].dataTable.rows.size() == 2
    }

    private static class SomeTest {
        public void step1() {}
        public void step2() {}
        public void step3() {}
        public void step4() {}
        public void step5() {}
    }

    def failure = Mock(StepFailure)

    def "Should be able to update the table results incrementally via the event bus"() {
        given:
            def eventBus = new StepEventBus(environmentVariables)
            def BaseStepListener listener = new BaseStepListener(outputDirectory)
            eventBus.registerListener(listener)
        when:
            eventBus.testSuiteStarted(Story.called("A data driven test suite"))
            eventBus.testStarted("aDataDrivenTest")
            eventBus.useExamplesFrom(DataTable.withHeaders(["firstName","lastName","age"]).build())

            eventBus.exampleStarted(["firstName":"Joe","lastName":"Smith","age":20])
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step1"));
            eventBus.stepFinished()
            eventBus.exampleFinished()

            eventBus.exampleStarted(["firstName":"Jack","lastName":"Smith","age":21])
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step2"));
            eventBus.stepPending()
            eventBus.exampleFinished()

            eventBus.exampleStarted(["firstName":"John","lastName":"Smith","age":22])
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step3"));
            eventBus.stepFailed(failure);

            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step4"));
            eventBus.stepIgnored()
            eventBus.stepStarted(ExecutedStepDescription.of(SomeTest.class,"step5"));
            eventBus.stepIgnored()
            eventBus.exampleFinished()

            eventBus.testFinished()
        then:
            listener.testOutcomes[0].dataTable.rows.collect { it.result } == [SUCCESS, PENDING, FAILURE]
    }
}
