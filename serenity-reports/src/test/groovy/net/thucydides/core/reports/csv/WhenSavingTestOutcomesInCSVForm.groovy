package net.thucydides.core.reports.csv

import au.com.bytecode.opencsv.CSVReader
import net.thucydides.core.reports.OutcomeFormat
import net.thucydides.core.reports.TestOutcomeLoader
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.environment.MockEnvironmentVariables
import spock.lang.Specification

import java.nio.file.Files

import static net.thucydides.core.util.TestResources.directoryInClasspathCalled

/**
 * Test outcomes can be saved as CSV files, so they can be imported and manipulated in Excel.
 */
class WhenSavingTestOutcomesInCSVForm extends Specification {

    File temporaryDirectory
    def loader = new TestOutcomeLoader().forFormat(OutcomeFormat.JSON)

    def setup() {
        temporaryDirectory = Files.createTempDirectory("serenity-tmp").toFile()
        temporaryDirectory.deleteOnExit();
    }

    def environmentVariables = new MockEnvironmentVariables()

    def "should store an empty set of test outcomes as an empty CSV file with only column titles"() {
        given: "no test results"
            def testOutcomeList = TestOutcomes.withNoResults()
        when: "we store these outcomes as a CSV file"
            def csvReporter = new CSVReporter(temporaryDirectory)
            File csvResults = csvReporter.generateReportFor(testOutcomeList, "results.csv")
        then: "the CSV file contains a single line"
            csvResults.text.readLines().size() == 1
        and: "the first line should contain the test outcome headings"
            linesIn(csvResults)[0] == ["Story", "Title", "Result", "Date", "Stability", "Duration (s)"]
    }

    def "should store a row of data for each test result"() {
        given: "a set of test results"
            def testOutcomeList = loader.loadFrom(directoryInClasspathCalled("/tagged-test-outcomes-json"));
        when: "we store these outcomes as a CSV file"
            def csvReporter = new CSVReporter(temporaryDirectory)
            File csvResults = csvReporter.generateReportFor(TestOutcomes.of(testOutcomeList), "results.csv")
        then: "there should be a row for each test result"
            def lines = linesIn(csvResults)
            lines.size() == 4
    }

    def "should store user-configurable extra columns"() {
        given: "a set of test results"
            def testOutcomeList = loader.loadFrom(directoryInClasspathCalled("/tagged-test-outcomes-json"));
        and: "we want to store extra columns from tag values"
            environmentVariables.setProperty("thucydides.csv.extra.columns","feature, epic")
        when: "we store these outcomes as a CSV file"
            def csvReporter = new CSVReporter(temporaryDirectory, environmentVariables)
            File csvResults = csvReporter.generateReportFor(TestOutcomes.of(testOutcomeList), "results.csv")
        then: "the results should contain a column for each additional column"
            linesIn(csvResults)[0] == ["Story", "Title", "Result", "Date", "Stability", "Duration (s)", "Feature", "Epic"]
    }

    def "should store windows-1251 encoding, if it set in config"() {
        given: "a set of test results in windows-1251"
            def testOutcomeList = loader.loadFrom(directoryInClasspathCalled("/tagged-test-outcomes-win-1251"));
            System.out.println(testOutcomeList);
        and: "in environment setted windows-1251 encoding"
            environmentVariables.setProperty("thucydides.report.encoding", "windows-1251")
        when: "we store these outcomes as a CSV file"
            def csvReporter = new CSVReporter(temporaryDirectory, environmentVariables)
            File csvResults = csvReporter.generateReportFor(TestOutcomes.of(testOutcomeList), "results.csv")
        then: "the results should contain a column for each additional column"
            linesIn(csvResults)[1][1] == "Другой приемлемый" ||
                    linesIn(csvResults)[1][1] == "Приемлемый тестовый запуск" ||
                    linesIn(csvResults)[1][1] == "Третий приемлемый"
    }

    def linesIn(File csvResults) {
        def CSVReader reader = new CSVReader(new java.io.InputStreamReader(new java.io.FileInputStream(csvResults), "windows-1251"))
        try{
            reader.readAll()
        }finally{
            reader.close()
        }
    }
}
