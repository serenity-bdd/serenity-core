package net.thucydides.core.requirements

import net.thucydides.core.model.SampleTestResults
import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.domain.ReportType
import net.thucydides.model.domain.TestTagCache
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.issues.IssueTracking
import net.thucydides.model.reports.TestOutcomes
import net.thucydides.model.reports.html.ReportNameProvider
import net.thucydides.model.requirements.MultiSourceRequirementsService
import net.thucydides.model.requirements.PackageAnnotationBasedTagProvider
import net.thucydides.model.requirements.RequirementsTagProvider
import net.thucydides.model.requirements.reports.MultipleSourceRequirmentsOutcomeFactory
import net.thucydides.model.requirements.reports.RequirementsOutcomeFactory
import net.thucydides.model.requirements.reports.RequirementsOutcomes
import net.thucydides.model.statistics.service.TagProvider
import net.thucydides.model.statistics.service.TagProviderService
import spock.lang.Specification

import static net.thucydides.model.reports.html.ReportNameProvider.NO_CONTEXT

class WhenCalculatingRequirementTestCoverage extends Specification {

    public static final String ROOT_DIRECTORY = "packagerequirements"

    List<RequirementsTagProvider> requirementsProviders
    ReportNameProvider reportNameProvider
    TagProviderService tagProviderService
    
    def setup() {
        TestTagCache.clear()
        def vars = new MockEnvironmentVariables()
        vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, ROOT_DIRECTORY)
        requirementsProviders = [new PackageAnnotationBasedTagProvider(vars)]

        tagProviderService = new TagProviderService() {
            @Override
            List<TagProvider> getTagProviders() {
                return requirementsProviders
            }

            @Override
            List<TagProvider> getTagProviders(String testSource) {
                return requirementsProviders
            }
        }

        reportNameProvider =  new ReportNameProvider(NO_CONTEXT, ReportType.HTML, new MultiSourceRequirementsService())
    }

    def issueTracking = Mock(IssueTracking)



    def "should count the total number of nested requirements"() {
        given: "there are two stories with passing tests in the 'Apples' feature"
            def testOutcomes = TestOutcomes.of(SampleTestResults.withPassingTestForApples())
            def environmentVariables = new MockEnvironmentVariables()
        and: "we read the requirements from the directory structure"
          RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables, new ReportNameProvider())
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
            def applesFeatureOutcomes = outcomes.requirementOutcomes.find { it.requirement.name == 'Apples'}
        then:
            applesFeatureOutcomes.subrequirements.total == 2
    }

// TODO: Refactor to use recorded test outcomes, not the deprecated PackageRequirementsTagProvider
//
//    def "should show only outcomes containing tests if so configured"() {
//        given: "there are two stories with passing tests in the 'Apples' feature"
//        def testOutcomes = TestOutcomes.of(SampleTestResults.withPassingTestForApples())
//        def environmentVariables = new MockEnvironmentVariables()
//        environmentVariables.setProperty("serenity.report.hide.empty.requirements","true")
//        and: "we read the requirements from the directory structure"
//        RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables, new ReportNameProvider())
//        when: "we generate the capability outcomes"
//        RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
//        then:
//        outcomes.visibleOutcomes.size() == 1
//    }

    def "should count zero if there are no nested requirements"() {
        given: "there are two stories with passing tests in the 'Apples' feature"
            def testOutcomes = TestOutcomes.of(SampleTestResults.withPassingTestForApples())
            def environmentVariables = new MockEnvironmentVariables()
        and: "we read the requirements from the directory structure"
            RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables, new ReportNameProvider())
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
            def potatoesFeatureOutcomes = outcomes.requirementOutcomes.find { it.requirement.name == 'Potatoes'}
        then:
            potatoesFeatureOutcomes.subrequirements.total == 0
    }

// TODO: Refactor to use recorded test outcomes, not the deprecated PackageRequirementsTagProvider
//
//    def "should count the number of nested requirements with tests in a given state"() {
//        given: "there are two stories with passing tests in the 'Apples' feature"
//            def testOutcomes = TestOutcomes.of(SampleTestResults.withPassingTestForApples())
//            def environmentVariables = new MockEnvironmentVariables()
//        and: "we read the requirements from the directory structure"
//            RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables, new ReportNameProvider())
//        when: "we generate the capability outcomes"
//            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
//            def applesFeatureOutcomes = outcomes.requirementOutcomes.find { it.requirement.name == 'Apples'}
//        then:
//            applesFeatureOutcomes.subrequirements.withResult("success") == 1
//        and:
//            applesFeatureOutcomes.subrequirements.withResult("failure") == 1
//        and:
//            applesFeatureOutcomes.subrequirements.withResult("pending") == 0
//    }

// TODO: Refactor to use recorded test outcomes, not the deprecated PackageRequirementsTagProvider
//
//    def "should count the percentage of nested requirements with tests in a given state"() {
//        given: "there are two stories with passing tests in the 'Apples' feature"
//            def testOutcomes = TestOutcomes.of(SampleTestResults.withPassingTestForApples())
//            def environmentVariables = new MockEnvironmentVariables()
//        and: "we read the requirements from the directory structure"
//            RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables, new ReportNameProvider())
//        when: "we generate the capability outcomes"
//            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
//            def applesFeatureOutcomes = outcomes.requirementOutcomes.find { it.requirement.name == 'Apples'}
//        then:
//            applesFeatureOutcomes.subrequirements.proportion.withResult("success") == 0.5
//        and:
//            applesFeatureOutcomes.subrequirements.proportion.withResult("failure") == 0.5
//        and:
//            applesFeatureOutcomes.subrequirements.proportion.withResult("pending") == 0.0
//    }

    def "should count the number of nested requirements with no tests"() {
        given: "there are two stories with passing tests in the 'Apples' feature"
            def testOutcomes = TestOutcomes.of(SampleTestResults.withPassingTestForApples())
            def environmentVariables = new MockEnvironmentVariables()
        and: "we read the requirements from the directory structure"
            RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables, new ReportNameProvider())
        when: "we generate the capability outcomes"
            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
            def featureOutcomes = outcomes.requirementOutcomes.find { it.requirement.name == 'Nice zucchinis'}
        then:
            featureOutcomes.subrequirements.withNoTests() == 2
    }

//    def "For leaf requirements we count the number of acceptance criteria"() {
//        given: "there are two stories with passing tests in the 'Apples' feature"
//            def testOutcomes = TestOutcomes.of(SampleTestResults.withPassingTestForApples()).withTag(TestTag.withName("Apples").andType("feature"))
//            def environmentVariables = new MockEnvironmentVariables()
//        and: "we read the requirements from the directory structure"
//            RequirementsOutcomeFactory requirmentsOutcomeFactory = new MultipleSourceRequirmentsOutcomeFactory(requirementsProviders,issueTracking, environmentVariables, new ReportNameProvider())
//        when: "we generate the capability outcomes"
//            RequirementsOutcomes outcomes = requirmentsOutcomeFactory.buildRequirementsOutcomesFrom(testOutcomes)
//        outcomes.requirementOutcomes
//            RequirementOutcome applesFeatureOutcomes = outcomes.requirementOutcomes.find { it.requirement.name == 'Apples'}
//            RequirementOutcome buyingApplesStory = outcomes.requirementOutcomes.find { it.requirement.name == 'Buying Apples'}
//
//        then:
//            buyingApplesStory.subrequirements.total == 1
//
//    }

    /*
    TODO:
        - Count child requirements with non-leaf requirements
        - Count child acceptance criteria (tests) for leaf requirements
        - Count passing child requirements
     */

}
