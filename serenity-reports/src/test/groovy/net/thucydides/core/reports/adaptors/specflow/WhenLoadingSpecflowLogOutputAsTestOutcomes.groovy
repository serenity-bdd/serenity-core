package net.thucydides.core.reports.adaptors.specflow

import net.thucydides.model.domain.TestResult
import net.thucydides.model.reports.adaptors.TestOutcomeAdaptor
import net.thucydides.model.reports.adaptors.specflow.SpecflowAdaptor
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files

import static net.thucydides.model.util.TestResources.fileInClasspathCalled

/**
 * We want to convert Specflow log outputs (possibly with some extra optional data) to TestOutcomes
 * so that they can be used to generate viable Thucydides test reports.
 */
class WhenLoadingSpecflowLogOutputAsTestOutcomes extends Specification {

    def simpleSpecflowOutput = """***** root.packages.MyCapability.SpecFlow.Features.MyFeature.MyScenario()
   Given a precondition
   -> done: bla bla bla (2.3s)
   When something happens
   -> done: bla bla bla (1.0s)
   Then some outcome should occur
   -> done: bla bla bla (0.9s)
"""

    def "should find the scenario and story titles"() {
        given:
            def specflowOutput = fileFrom(simpleSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
            testOutcomes.size() == 1
        and:
            testOutcomes.get(0).title == "My scenario"
        and:
            testOutcomes.get(0).storyTitle == "My feature"
        and:
            testOutcomes.get(0).getTags()
            testOutcomes.get(0).path == "root.packages.MyCapability.SpecFlow.Features.MyFeature"
    }

    def "should find the scenario steps"() {
        given:
            def specflowOutput = fileFrom(simpleSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
            def testOutcome = testOutcomes.get(0)
        then:
            testOutcome.getTestSteps().collect{ it.description } == ["Given a precondition",
                                                                     "When something happens",
                                                                     "Then some outcome should occur"]
    }

    def "should record the scenario step results"() {
        given:
            def specflowOutput = fileFrom(simpleSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
            def testOutcome = testOutcomes.get(0)
        then:
            testOutcome.getTestSteps().collect{ it.result } == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS]
    }

    def "should record the step times"() {
        given:
            def specflowOutput = fileFrom(simpleSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
            def testOutcome = testOutcomes.get(0)
        then:
            testOutcome.getTestSteps().collect{ it.duration } == [2300, 1000, 900]
    }

    def  failingSpecflowOutput = """***** my.SpecFlow.Features.MyFeature.MyScenario()
   Given a precondition
   -> done: bla bla bla (2.3s)
   When something happens
   -> done: bla bla bla (1.0s)
   Then some outcome should occur
   -> error: bla bla bla
   more bla bla bla
"""

    def "should record step failures"() {
        given:
            def specflowOutput = fileFrom(failingSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
            def testOutcome = testOutcomes.get(0)
        then:
            testOutcome.getTestSteps().collect{ it.result } == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.FAILURE]
    }

    def  multiScenarioSpecflowOutput = """***** my.SpecFlow.Features.MyFeature.MyScenario()
   Given a precondition
   -> done: bla bla bla (2.3s)
   When something happens
   -> done: bla bla bla (1.0s)
   Then some outcome should occur
   -> done: bla bla bla (0.9s)
***** my.SpecFlow.Features.MyFeature.MyOtherScenario()
   Given a precondition
   -> done: bla bla bla (2.3s)
   When something happens
   -> done: bla bla bla (1.0s)
   Then some outcome should occur
   -> error: bla bla bla
   more bla bla bla
"""

    def "should record multiple steps"() {
        given:
            def specflowOutput = fileFrom(multiScenarioSpecflowOutput)
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
            testOutcomes.size() == 2
        and:
            testOutcomes.collect{ it.title } == ["My scenario", "My other scenario"]
    }

    def "should record multiple scenarios"() {
        given:
            def specflowOutput = fileInClasspathCalled("/specflow-output/multiple-scenarios.txt")
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
            testOutcomes.size() == 1
            testOutcomes.get(0).getTestSteps().size() == 8
            testOutcomes.get(0).getDataTable().getSize() == 8
            testOutcomes.get(0).getDataTable().getRows().get(0).result == TestResult.SUCCESS
    }

    def "should load outcomes from output directory"() {
        given:
            def outputReportsDir = fileInClasspathCalled("/specflow-output/samples")
            def specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(outputReportsDir)
        then:
            testOutcomes.size() == 3

    }

    def "should record multiple different scenarios in a single file"() {
        given:
            def specflowOutput = fileInClasspathCalled("/specflow-output/multiple-separate-scenarios.txt")
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
            testOutcomes.size() == 3
        and:
            testOutcomes.collect{ it.title } == ["Populate business payment process drop down list",
                "Test to fail",
                "Debit account owner selection"]
        and:
            testOutcomes.collect{ it.result } == [TestResult.SUCCESS, TestResult.FAILURE, TestResult.PENDING]
        and:
            testOutcomes.collect {it.dataTable } == [null,null,null]
    }

    def "should record multiple different scenarios including a table in a single file"() {
        given:
            def specflowOutput = fileInClasspathCalled("/specflow-output/multiple-separate-scenarios-containing-a-table.txt")
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
            testOutcomes.size() == 4
        and:
            testOutcomes.collect{ it.title } ==
                    ["Populate business payment process drop down list",
                    "Test to fail",
                    "Populate business transaction and payment type drop down lists",
                    "Debit account owner selection"]
        and:
            testOutcomes.collect{ it.result } == [TestResult.SUCCESS, TestResult.FAILURE, TestResult.SUCCESS, TestResult.PENDING]
        and:
            testOutcomes.get(2).dataTable.size == 3
        and:
            testOutcomes.get(0).dataTable == null && testOutcomes.get(1).dataTable == null && testOutcomes.get(3).dataTable == null
    }

    def "should record scenarios with rows in a table"() {
        given:
            def specflowOutput = fileInClasspathCalled("/specflow-output/passing-multiple-scenarios-in-a-table.txt")
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
            testOutcomes.size() == 1
        and:
            testOutcomes.get(0).dataTable != null
        and:
            testOutcomes.get(0).dataTable.rows.collect{ it.result } == [TestResult.SUCCESS, TestResult.SUCCESS, TestResult.SUCCESS]
        and: "we have no way of knowing the header values from this file"
            testOutcomes.get(0).dataTable.headers == ["A","B","C","D","E"]
        and:
            testOutcomes.get(0).dataTable.rows.collect{ it.values } ==
                [["Inputter-DirectBanking", "Funds Transfer between Own Accounts", "N/A", "Funds Transfer", ""],
                        ["Inputter-DirectBanking", "Credit Card Repayment", "N/A", "Funds Transfer", ""],
                        ["Inputter", "Credit Card Repayment", "N/A", "Funds Transfer", ""]]
    }

    def "should record scenarios with rows in a table containing failures"() {
        given:
            def specflowOutput = fileInClasspathCalled("/specflow-output/multiple-scenarios-in-a-table-with-failures.txt")
            TestOutcomeAdaptor specflowLoader = new SpecflowAdaptor()
        when:
            def testOutcomes = specflowLoader.loadOutcomesFrom(specflowOutput)
        then:
            testOutcomes.size() == 1
        and:
            testOutcomes.get(0).dataTable != null
        and:
            testOutcomes.get(0).dataTable.rows.collect{ it.result } ==
                [TestResult.SUCCESS, TestResult.FAILURE, TestResult.FAILURE, TestResult.FAILURE,
                        TestResult.FAILURE, TestResult.FAILURE, TestResult.FAILURE, TestResult.SUCCESS]
    }

    File tmp

    def setup() {
        tmp = Files.createTempDirectory("serenity-tmp").toFile()
        tmp.deleteOnExit()
    }


    def fileFrom(def contents) {
        def outputFile = new File(tmp, "specflow-output-${System.currentTimeMillis()}.out")
        outputFile.write(contents)
        return outputFile
    }


}
