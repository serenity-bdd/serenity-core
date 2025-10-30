package net.thucydides.core.requirements.integration

import com.google.common.collect.Lists
import net.thucydides.core.requirements.reportpages.RequirementsReport
import net.thucydides.model.domain.*
import net.thucydides.model.issues.IssueTracking
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.model.reports.history.DateProvider
import net.thucydides.model.reports.html.ReportNameProvider
import net.thucydides.model.requirements.FileSystemRequirementsTagProvider
import net.thucydides.model.requirements.model.Requirement
import net.thucydides.model.requirements.reports.MultipleSourceRequirmentsOutcomeFactory
import net.thucydides.model.requirements.reports.RequirementsOutcomeFactory
import net.thucydides.model.requirements.reports.RequirementsOutcomes
import org.joda.time.DateTime
import spock.lang.Specification

import java.nio.file.Files

class WhenGeneratingRequirementsReports extends Specification {

    class MockedDateProvider implements DateProvider {
        DateTime time

        DateTime getCurrentTime() {
            return time
        }
    }

    def requirementsProvider = new FileSystemRequirementsTagProvider()
    def issueTracking = Mock(IssueTracking)

    RequirementsReport report

    def cleanup() {
        report?.close()
    }

    File outputDirectory

    def setup() {
        outputDirectory = Files.createTempDirectory("testdata").toFile()
        outputDirectory.deleteOnExit()
    }

    def "Should know the type of child requirements"() {
        given: "we read the requirements from the directory structure"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
            RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory([requirementsProvider], issueTracking, new ReportNameProvider())
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        when: "we get the child requirement type of a requirement"
            Requirement firstRequirement = outcomes.getRequirementOutcomes().get(0).getRequirement()
        then:
            firstRequirement.childType() == 'feature'
    }


    def "Should read the requirements overview"() {
        when: "we read the requirements from the directory structure"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
            RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory([requirementsProvider], issueTracking, new ReportNameProvider())
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        then:
            outcomes.overview == "Farming stories"
    }

    def someTestResults() {

        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")))

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")))

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feeding poultry", Story.called("raising chickens"))
        testOutcome3.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")))
        TestStep failingStep = new TestStep("look for grain")
        failingStep.failedWith(new AssertionError("No grain left"))
        testOutcome3.recordStep(failingStep)

        TestOutcome testOutcome4 = TestOutcome.forTestInStory("Planting wheat", Story.called("planting some wheet"))
        testOutcome4.addTags(Lists.asList(TestTag.withName("Grow wheat").andType("capability")))
        TestStep passingStep = new TestStep("Grow wheat")
        passingStep.setResult(TestResult.SUCCESS)
        testOutcome4.recordStep(passingStep)

        return [testOutcome1, testOutcome2, testOutcome3, testOutcome4]

    }


    def someMoreTestResults() {

        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")))

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")))

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feeding poultry", Story.called("raising chickens"))
        testOutcome3.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")))
        TestStep passingChickenStep = new TestStep("look for grain")
        passingChickenStep.setResult(TestResult.SUCCESS)
        testOutcome3.recordStep(passingChickenStep)

        TestOutcome testOutcome4 = TestOutcome.forTestInStory("Planting wheat", Story.called("planting some wheet"))
        testOutcome4.addTags(Lists.asList(TestTag.withName("Grow wheat").andType("capability")))
        TestStep passingStep = new TestStep("Grow wheat")
        passingStep.setResult(TestResult.SUCCESS)
        testOutcome4.recordStep(passingStep)

        return [testOutcome1, testOutcome2, testOutcome3, testOutcome4]

    }

}
