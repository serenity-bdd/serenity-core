package net.serenitybdd.cucumber.service

import net.thucydides.model.domain.TestOutcome
import net.thucydides.model.domain.TestTag
import net.thucydides.model.requirements.FileSystemRequirementsTagProvider
import net.thucydides.model.statistics.service.TagProvider
import net.thucydides.model.util.EnvironmentVariables
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification
/**
 * Created by john on 10/07/2016.
 */
class WhenConfiguringCucumberTagProviders extends Specification {

    def "should handle Cucumber test types"() {
        expect:
            new CucumberTagProviderStrategy().canHandleTestSource(testType) == handled
        where:
            testType   | handled
            "cucumber" | true
            "Cucumber" | true
            "junit"    | false
    }

    def "should use the features directory as the root directory by default"() {
        given:
            CucumberTagProviderStrategy tagProviderStrategy = new CucumberTagProviderStrategy()
        when:
            Set<TagProvider> tagProviders = tagProviderStrategy.tagProviders
        then:
            tagProviders.size() == 3
            tagProviders[0].class == FileSystemRequirementsTagProvider
            System.err.println "ROOT DIRECTORY A:" + ((FileSystemRequirementsTagProvider) tagProviders[0]).rootDirectory
            ((FileSystemRequirementsTagProvider) tagProviders[0]).rootDirectory.replace("\\","/") == "src/test/resources/features"
    }

    def "should be able to override the feature directory using the environment variables"() {
        given:
            EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setProperty("serenity.requirements.dir","feature-files")
            CucumberTagProviderStrategy tagProviderStrategy = new CucumberTagProviderStrategy(environmentVariables)
        when:
            Set<TagProvider> tagProviders = tagProviderStrategy.tagProviders
        then:
            tagProviders.size() == 3
            tagProviders[0].class == FileSystemRequirementsTagProvider
            System.err.println "ROOT DIRECTORY B:" + ((FileSystemRequirementsTagProvider) tagProviders[0]).rootDirectory
            ((FileSystemRequirementsTagProvider) tagProviders[0]).rootDirectory == "feature-files"
    }

    def "should provide feature and capability tags based on the feature file directory"() {
        given:
            CucumberTagProviderStrategy tagProviderStrategy = new CucumberTagProviderStrategy()
            def testOutcome = Mock(TestOutcome)
            testOutcome.getPath() >> "basic_arithmetic.feature"
        when:
            FileSystemRequirementsTagProvider tagProvider = tagProviderStrategy.tagProviders[0]
        and:
            def tags = tagProvider.getTagsFor(testOutcome)
        then:
            tags.contains(TestTag.withValue("feature:Calculator/Basic Arithmetic")) && tags.contains(TestTag.withValue("capability:Calculator"))
    }

    def "should provide feature and capability tags based on the correct name of the feature"() {
        given:
            CucumberTagProviderStrategy tagProviderStrategy = new CucumberTagProviderStrategy()
            def testOutcome = Mock(TestOutcome)
            testOutcome.getPath() >> "complex_arithmetic.feature"
        when:
            FileSystemRequirementsTagProvider tagProvider = tagProviderStrategy.tagProviders[0]
        and:
            def tags = tagProvider.getTagsFor(testOutcome)
        then:
            System.err.println tags
            tags.contains(TestTag.withValue("feature:calculator/complex_arithmetic")) && tags.contains(TestTag.withValue("capability:Calculator"))
    }

}
