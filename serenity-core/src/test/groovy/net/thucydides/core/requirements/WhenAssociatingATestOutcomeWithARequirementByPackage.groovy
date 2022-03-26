package net.thucydides.core.requirements

import annotatedstorieswithcontents.apples.BuyApples
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.model.Story
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.requirements.stories.grow_potatoes.ASampleTestWithACapability
import net.thucydides.core.requirements.stories.grow_potatoes.grow_new_potatoes.ASampleNestedTestWithACapability
import net.thucydides.core.requirements.stories.nocapacities.ASampleTestWithNoCapability
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenAssociatingATestOutcomeWithARequirementByPackage extends Specification {

    def "Should read requirements structure from the package directory structure"() {
        given: "We define the root package in the 'thucydides.test.root' property"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            vars.setProperty("serenity.test.root", "packagerequirements")
            PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars).withCacheDisabled()

        when: "We load the requirements structure"
            List<Requirement> requirements = capabilityProvider.getRequirements()
        then:
            requirements.get(0).type == "feature" && requirements.get(0).getChildren().get(0).type == "story"
    }

    def "Should read requirements structure from a 3 level package directory structure"() {
        given: "We define the root package in the 'thucydides.test.root' property"
            EnvironmentVariables vars = new MockEnvironmentVariables();
            vars.setProperty("serenity.test.root", "deeppackagerequirements")
            PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars).withCacheDisabled()

        when: "We load the requirements structure"
            List<Requirement> requirements = capabilityProvider.getRequirements()

            Requirement fruit = requirements.find {it.name == "Fruit"}
            Requirement apples = fruit.getChildren().find {it.name == "Apples"}
            Requirement pickingApple = apples.getChildren().find {it.name == "Picking apples"}

        then:
            fruit.type == "capability"
            apples.type == "feature"
            pickingApple.type == "story"
    }

    def "Should read requirements structure from a one-level package directory structure"() {
        given: "We define the root package in the 'thucydides.test.root' property"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        vars.setProperty("serenity.test.root", "packagerequirements.pears")
        PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars).withCacheDisabled()

        when: "We load the requirements structure"
        List<Requirement> requirements = capabilityProvider.getRequirements()
        then:
        requirements.get(0).type == "story"
    }

    def "Should associate a test case to capability based on it's package"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars).withCacheDisabled()
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = TestOutcome.inEnvironment(vars).forTest("someTest", ASampleTestWithACapability)
        then:
        capabilityProvider.getTagsFor(testOutcome) == [
                TestTag.withName("Grow potatoes").andType("capability"),
                TestTag.withName("Grow potatoes/A sample test with a capability").andType("feature"),
        ] as Set
    }

    def "Should associate a nested test case to capability based on it's package"() {
        given: "We are using the package requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars).withCacheDisabled()
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = TestOutcome.inEnvironment(vars).forTest("someTest", ASampleNestedTestWithACapability)
        then:
        capabilityProvider.getTagsFor(testOutcome) == [
                TestTag.withName("Grow potatoes").andType("capability"),
                TestTag.withName("Grow new potatoes/A sample nested test with a capability").andType("story"),
                TestTag.withName("Grow potatoes/Grow new potatoes").andType("feature")] as Set
    }

    def "Should not associate a test case if there is no matching capability"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars).withCacheDisabled()
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = TestOutcome.inEnvironment(vars).forTest("someTest", ASampleTestWithNoCapability)
        then:
        capabilityProvider.getTagsFor(testOutcome) == [TestTag.withName("Nocapacities").andType("capability"),
                                                       TestTag.withName("Nocapacities/A sample test with no capability").andType("feature")] as Set
    }

    def "Should find the direct parent requirement of a test outcome"() {
        given: "We are using the default requirements provider"
            EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
            vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
            PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars)
            capabilityProvider.clearCache()
        when: "We load requirements with nested capability directories and no .narrative files"
            def testOutcome = TestOutcome.inEnvironment(vars).forTest("someTest", ASampleTestWithACapability)
        then:
            capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
            capabilityProvider.getParentRequirementOf(testOutcome).get().name == "A sample test with a capability"

            PackageRequirementsTagProvider capabilityProvider2 = packageRequirementsTagProviderUsing(vars)
            capabilityProvider2.getParentRequirementOf(testOutcome).isPresent()
            capabilityProvider2.getParentRequirementOf(testOutcome).get().name == "A sample test with a capability"

    }

    private PackageRequirementsTagProvider packageRequirementsTagProviderUsing(MockEnvironmentVariables vars) {
        PackageRequirementsTagProvider capabilityProvider = new PackageRequirementsTagProvider(vars)
        capabilityProvider.clearCache();
        return capabilityProvider;
    }

    def "Should find the direct parent requirement of a test outcome from an annotated story"() {
        given: "We are using the annotated provider"
        def vars = new MockEnvironmentVariables()
        vars.setProperty(ThucydidesSystemProperty.THUCYDIDES_ANNOTATED_REQUIREMENTS_DIR.propertyName, "annotatedstorieswithcontents")
        RequirementsTagProvider capabilityProvider = new PackageAnnotationBasedTagProvider(vars)
        when: "We load requirements we have an annotated test"
        def story = new Story(BuyApples.class)
        def testOutcome = TestOutcome.inEnvironment(vars).forTest("Title for test 1", BuyApples.class, story)
        then:
        capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
        capabilityProvider.getParentRequirementOf(testOutcome).get().narrative.renderedText == "A Narrative for test 1  " + System.lineSeparator() + "Multiple lines  "
    }

    def "Should find the direct parent requirement of a test outcome for nested requirements"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars).withCacheDisabled()
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = new TestOutcome("someTest", ASampleNestedTestWithACapability)
        then:
        capabilityProvider.getParentRequirementOf(testOutcome).isPresent()
        capabilityProvider.getParentRequirementOf(testOutcome).get().name == "A sample nested test with a capability"
    }

    def "Should associate a test case to a requirement based on its featurefile if provided"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars).withCacheDisabled()
        when: "We load requirements with nested capability directories and no .narrative files"
        def testOutcome = new TestOutcome("someTest", ASampleNestedTestWithACapability)
        then:
        capabilityProvider.getTagsFor(testOutcome) == [TestTag.withName("Grow potatoes").andType("capability"),
                                                       TestTag.withName("Grow potatoes/Grow new potatoes").andType("feature"),
                                                       TestTag.withName("Grow new potatoes/A sample nested test with a capability").andType("story")] as Set
    }

    def "Should find the requirement for a given tag"() {
        given: "We are using the default requirements provider"
        EnvironmentVariables vars = new MockEnvironmentVariables();
        and: "We define the root package in the 'thucydides.test.root' property"
        vars.setProperty("serenity.test.root", "net.thucydides.core.requirements.stories")
        PackageRequirementsTagProvider capabilityProvider = packageRequirementsTagProviderUsing(vars).withCacheDisabled()
        when: "We load requirements with nested capability directories and no .narrative files"
        def growPotatoesTag = TestTag.withName("Grow potatoes").andType("capability")
        then:
        Optional<Requirement> requirement = capabilityProvider.getRequirementFor(growPotatoesTag)
        requirement.get().getName() == "Grow potatoes"
        requirement.get().getType() == "capability"
    }


}

