package net.serenitybdd.core.model

import net.serenitybdd.core.Serenity
import net.thucydides.core.model.*
import net.thucydides.core.steps.BaseStepListener
import net.thucydides.core.steps.ExecutedStepDescription
import net.thucydides.core.steps.StepEventBus
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class WhenIncludingReportDataWithAStep extends Specification {

    def "Steps do not normally have report data associated with them" () {
        given:
            def testOutcome = TestOutcome.forTestInStory("some test", Story.called("some story"))
        when:
            testOutcome.recordStep(TestStep.forStepCalled("Sample step").withResult(TestResult.SUCCESS))
        then:
            !testOutcome.lastStep().hasData()
    }

    def "Arbitrary report data can be added to a step in the form of a string" () {
        given:
            def testOutcome = TestOutcome.forTestInStory("some test", Story.called("some story"))
            testOutcome.recordStep(TestStep.forStepCalled("Sample step").withResult(TestResult.SUCCESS))
        when:
            testOutcome.currentStep().get().withReportData(ReportData.withTitle("Some data").andContents("<some><data/></some>"))
        then:
            testOutcome.lastStep().hasData() &&
                    testOutcome.lastStep().reportData[0] == ReportData.withTitle("Some data").andContents("<some><data/></some>")
    }

    def "Arbitrary report data can be added to a step from a file"() {
        given:
            def testOutcome = TestOutcome.forTestInStory("some test", Story.called("some story"))
            testOutcome.recordStep(TestStep.forStepCalled("Sample step").withResult(TestResult.SUCCESS))
        when:
            def testDataSource = Paths.get(this.class.getResource("/testdata/report-data.xml").toURI())
            testOutcome.currentStep().get().withReportData(ReportData.withTitle("Some data").fromFile( testDataSource, StandardCharsets.UTF_8))
        then:
            testOutcome.lastStep().hasData() &&
                testOutcome.lastStep().reportData[0] == ReportData.withTitle("Some data").andContents("<some><more><data/></more></some>")
    }

    def "Arbitrary report data can be added to a step from a string using the Serenity class"() {
        given:
            File outputDir = Files.createTempDirectory("out").toFile()

            StepEventBus.getEventBus().registerListener(new BaseStepListener(outputDir));
            StepEventBus.getEventBus().testStarted("some test")
            StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("some test"))

        when:
            Serenity.recordReportData().withTitle("Some data").andContents("<some><data/></some>")

        then:
            TestOutcome testOutcome = StepEventBus.getEventBus().baseStepListener.testOutcomes.get(0)
            testOutcome.lastStep().hasData() &&
                testOutcome.lastStep().reportData[0] == ReportData.withTitle("Some data").andContents("<some><data/></some>")
    }



    def "Arbitrary report data can be added to a step from a downloadable file using the Serenity class"() {
        given:
        File outputDir = Files.createTempDirectory("out").toFile()

        StepEventBus.getEventBus().registerListener(new BaseStepListener(outputDir));
        StepEventBus.getEventBus().testStarted("some test")
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("some test"))

        when:
        def testDataSource = Paths.get(this.class.getResource("/testdata/report-data.xml").toURI())
        Serenity.recordReportData().withTitle("Some data").downloadable().fromFile(testDataSource)

        then:
        TestOutcome testOutcome = StepEventBus.getEventBus().baseStepListener.testOutcomes.get(0)
        testOutcome.lastStep().hasData()
        testOutcome.lastStep().reportData[0].path.startsWith("downloadable")
        testOutcome.lastStep().reportData[0].path.endsWith("report-data.xml")
    }

    def "Arbitrary report data can be recorded as evidence to appear at the feature level"() {
        given:
        File outputDir = Files.createTempDirectory("out").toFile()

        StepEventBus.getEventBus().registerListener(new BaseStepListener(outputDir));
        StepEventBus.getEventBus().testStarted("some test")
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("some test"))

        when:
        def testDataSource = Paths.get(this.class.getResource("/testdata/report-data.xml").toURI())
        Serenity.recordReportData().withTitle("Some data").downloadable().asEvidence().fromFile(testDataSource)

        then:
        TestOutcome testOutcome = StepEventBus.getEventBus().baseStepListener.testOutcomes.get(0)
        testOutcome.lastStep().hasData()
        testOutcome.lastStep().reportData[0].isEvidence
    }


    def "Arbitrary report data can be added to a step from a file using the Serenity class using the encoding"() {
        given:
        File outputDir = Files.createTempDirectory("out").toFile()

        StepEventBus.getEventBus().registerListener(new BaseStepListener(outputDir));
        StepEventBus.getEventBus().testStarted("some test")
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle("some test"))

        when:
        def testDataSource = Paths.get(this.class.getResource("/testdata/report-data.xml").toURI())
        Serenity.recordReportData().withTitle("Some data").fromFile(testDataSource, StandardCharsets.UTF_8)

        then:
        TestOutcome testOutcome = StepEventBus.getEventBus().baseStepListener.testOutcomes.get(0)
        testOutcome.lastStep().hasData() &&
                testOutcome.lastStep().reportData[0] == ReportData.withTitle("Some data").andContents("<some><more><data/></more></some>")
    }
}
