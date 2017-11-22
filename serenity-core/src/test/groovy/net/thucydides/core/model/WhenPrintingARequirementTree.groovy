package net.thucydides.core.model

import net.thucydides.core.requirements.model.Requirement
import net.thucydides.core.requirements.model.RequirementTree
import spock.lang.Specification

class WhenPrintingARequirementTree extends Specification {

    def "an empty requirements tree should show that there are no requirements"() {
        given: "no requirements were found"
            List<Requirement> requirements = []
        when: "we print the requirements tree"
            def printedRequirements = RequirementTree.withRequirements(requirements).toString()
        then: "the tree should show that no requirements were found"
            printedRequirements == "NO REQUIREMENTS FOUND"

    }

    def "a flat list of requirements"() {
        given:
            List<Requirement> requirements = [
                    Requirement.named("fruit").withTypeOf("capability"),
                    Requirement.named("veges").withTypeOf("capability"),
            ]
        when:
            def printedRequirements = RequirementTree.withRequirements(requirements).toString()
        then:
        printedRequirements.contains("REQUIREMENTS:")
        printedRequirements.contains("    - capability: fruit")
        printedRequirements.contains("    - capability: vege")
    }

    def "a nested  list of requirements"() {
        given:
        List<Requirement> requirements = [
                Requirement.named("fruit").withTypeOf("capability").
                        withChild(Requirement.named("apple").withTypeOf("feature")).
                        withChild(Requirement.named("pear").withTypeOf("feature")),
                Requirement.named("veges").withTypeOf("capability").
                        withChild(Requirement.named("cucumber").withTypeOf("feature")),
        ]
        when:
        def printedRequirements = RequirementTree.withRequirements(requirements).toString()
        then:
        printedRequirements.contains("REQUIREMENTS:")
        printedRequirements.contains("    - capability: fruit")
        printedRequirements.contains("        - feature: apple")
        printedRequirements.contains("        - feature: pear")
        printedRequirements.contains("    - capability: vege")
        printedRequirements.contains("        - feature: cucumber")
    }

}

