package net.thucydides.core.requirements

import net.thucydides.model.requirements.RequirementsMerger
import net.thucydides.model.requirements.model.Requirement
import spock.lang.Specification

/**
 * Created by john on 16/04/2015.
 */
class WhenMergingRequirementSets extends Specification {

    def "Merging two empty requirement sets should produce an empty set"() {
        when:
            def mergedRequirements = new RequirementsMerger().merge([],[])
        then:
            mergedRequirements == []
    }

    def "Merging one requirement set with another empty one should produce the original set"() {
        given:
            def initialRequirements = [Requirement.named("search").withType("feature").withNarrative("")]
        when:
            def mergedRequirements = new RequirementsMerger().merge(initialRequirements,[])
        then:
            mergedRequirements == initialRequirements
    }

    def "Merging two distinct sets"() {
        given:
            def initialRequirements = [Requirement.named("search").withType("feature").withNarrative("")]
        and:
            def newRequirements = [Requirement.named("purchase").withType("feature").withNarrative("")]
        when:
            def mergedRequirements = new RequirementsMerger().merge(initialRequirements,newRequirements)
        then:
            mergedRequirements.containsAll(initialRequirements) && mergedRequirements.containsAll(newRequirements)
    }

    def "Merging matching requirements"() {
        given:
            def initialRequirements = [Requirement.named("search").withOptionalCardNumber("CARD-1")
                                                                  .withType("feature").withNarrative("")
                                                                  .withReleaseVersions(["1"])]
        and:
            def newRequirements = [Requirement.named("search").withType("feature").withNarrative("Search for stuff")
                                           .withReleaseVersions(["1.1","1.1.1"])]
        when:
            def mergedRequirements = new RequirementsMerger().merge(initialRequirements,newRequirements)
        then:
            mergedRequirements.size() == 1
            mergedRequirements[0].name == "search"
            mergedRequirements[0].type == "feature"
            mergedRequirements[0].cardNumber == "CARD-1"
            mergedRequirements[0].narrative.text == "Search for stuff"
            mergedRequirements[0].releaseVersions == ["1","1.1","1.1.1"]
    }

    def "Merging matching requirements with children"() {
        given:
        def initialRequirements = [Requirement.named("search").withOptionalCardNumber("CARD-1")
                                           .withType("feature").withNarrative("")
                                           .withChildren([Requirement.named("search high").withType("story").withNarrative("")])]
        and:
        def newRequirements = [Requirement.named("search").withType("feature").withNarrative("")
                                           .withChildren([Requirement.named("search low").withType("story").withNarrative("")])]
        when:
        def mergedRequirements = new RequirementsMerger().merge(initialRequirements,newRequirements)
        then:
        mergedRequirements.size() == 1
        mergedRequirements[0].name == "search"
        mergedRequirements[0].type == "feature"
        mergedRequirements[0].children.size() == 2
    }

}
