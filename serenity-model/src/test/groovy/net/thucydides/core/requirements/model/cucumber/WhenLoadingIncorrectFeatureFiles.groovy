package net.thucydides.core.requirements.model.cucumber

import spock.lang.Specification

class WhenLoadingIncorrectFeatureFiles extends Specification {

    def invalidFeatureFile = "src/test/resources/features/maintain_my_todo_list/invalid.feature"

    def "Should display a meaningful error message if there is a Gherkin syntax error"() {
        when:
        CucumberParser parser = new CucumberParser()
        Optional<AnnotatedFeature> feature = parser.loadFeature(new File(invalidFeatureFile))
        then:
        InvalidFeatureFileException ex = thrown()
            ex.message.contains("Failed to parse resource")
    }
}