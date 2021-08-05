package net.thucydides.core.requirements.model.cucumber


import spock.lang.Specification

class WhenLoadingFeaturesInOtherLanguage extends Specification {

    def spanishFeatureFile = "src/test/resources/features/maintain_my_todo_list/feature_en_otro_idioma.feature"

    def "should load the feature no matters the encoding language"() {
        when:
        CucumberParser parser = new CucumberParser()
        Optional<AnnotatedFeature> spanishFeature = parser.loadFeature(new File(spanishFeatureFile))

        then: "the feature file should be loaded"
        spanishFeature.isPresent()
        spanishFeature.get().scenarioDefinitions.size() == 2

        and: "the scenarios should be read without problems"
        def feature = spanishFeature.get().feature
        def firstScenario = ReferencedScenario.in(feature).withName("Escenario número 1").asGivenWhenThen()
        firstScenario.get().contains("Dado que Ricardo está realizando una prueba sobre serenity")
        firstScenario.get().contains("Entonces al ejecutar la prueba se debe obtener un resultado éxitoso")
    }
}
