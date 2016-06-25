package net.thucydides.core.requirements.integration

import com.google.common.collect.Lists
import net.thucydides.core.issues.IssueTracking
import net.thucydides.core.model.*
import net.thucydides.core.reports.TestOutcomes
import net.thucydides.core.reports.history.DateProvider
import net.thucydides.core.reports.html.HtmlAggregateStoryReporter
import net.thucydides.core.reports.html.HtmlRequirementsReporter
import net.thucydides.core.requirements.FileSystemRequirementsTagProvider
import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.requirements.reportpages.RequirementsReport
import net.thucydides.core.requirements.reports.RequirementsOutcomes
import net.thucydides.core.requirements.reports.RequirmentsOutcomeFactory
import org.joda.time.DateTime
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class WhenGeneratingRequirementsReports extends Specification {

    class MockedDateProvider implements DateProvider {
        DateTime time

        DateTime getCurrentTime() {
            return time
        }
    }

    def requirementsProvider = new FileSystemRequirementsTagProvider()
    def htmlRequirementsReporter = new HtmlRequirementsReporter()
    def issueTracking = Mock(IssueTracking)
    def requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([], issueTracking);
    def aggregateReporter = new HtmlAggregateStoryReporter("project", issueTracking)

    RequirementsReport report

    def cleanup() {
        report?.close()
    }

    File outputDirectory

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        outputDirectory = temporaryFolder.newFolder()
    }

    def "Should know the type of child requirements"() {
        given: "we read the requirements from the directory structure"
            def noTestOutcomes = TestOutcomes.of(Collections.EMPTY_LIST)
            RequirmentsOutcomeFactory requirmentsOutcomeFactory = new RequirmentsOutcomeFactory([requirementsProvider], issueTracking)
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(noTestOutcomes)
        when: "we get the child requirement type of a requirement"
            Requirement firstRequirement = outcomes.getRequirementOutcomes().get(0).getRequirement();
        then:
            firstRequirement.childType() == 'feature'
    }

    def someTestResults() {

        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feeding poultry", Story.called("raising chickens"))
        testOutcome3.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        TestStep failingStep = new TestStep("look for grain")
        failingStep.failedWith(new AssertionError("No grain left"))
        testOutcome3.recordStep(failingStep)

        TestOutcome testOutcome4 = TestOutcome.forTestInStory("Planting wheat", Story.called("planting some wheet"))
        testOutcome4.addTags(Lists.asList(TestTag.withName("Grow wheat").andType("capability")));
        TestStep passingStep = new TestStep("Grow wheat")
        passingStep.setResult(TestResult.SUCCESS)
        testOutcome4.recordStep(passingStep)

        return [testOutcome1, testOutcome2, testOutcome3, testOutcome4]

    }


    def someMoreTestResults() {

        TestOutcome testOutcome1 = TestOutcome.forTestInStory("planting potatoes in the sun", Story.called("planting potatoes"))
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome1.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));

        TestOutcome testOutcome2 = TestOutcome.forTestInStory("planting potatoes in the rain", Story.called("planting potatoes"))
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow potatoes").andType("capability")));
        testOutcome2.addTags(Lists.asList(TestTag.withName("Grow new potatoes").andType("feature")));

        TestOutcome testOutcome3 = TestOutcome.forTestInStory("Feeding poultry", Story.called("raising chickens"))
        testOutcome3.addTags(Lists.asList(TestTag.withName("Raise chickens").andType("capability")));
        TestStep passingChickenStep = new TestStep("look for grain")
        passingChickenStep.setResult(TestResult.SUCCESS)
        testOutcome3.recordStep(passingChickenStep)

        TestOutcome testOutcome4 = TestOutcome.forTestInStory("Planting wheat", Story.called("planting some wheet"))
        testOutcome4.addTags(Lists.asList(TestTag.withName("Grow wheat").andType("capability")));
        TestStep passingStep = new TestStep("Grow wheat")
        passingStep.setResult(TestResult.SUCCESS)
        testOutcome4.recordStep(passingStep)

        return [testOutcome1, testOutcome2, testOutcome3, testOutcome4]

    }

}
