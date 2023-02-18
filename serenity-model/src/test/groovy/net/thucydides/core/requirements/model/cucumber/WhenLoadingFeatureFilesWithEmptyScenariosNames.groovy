package net.thucydides.core.requirements.model.cucumber

import spock.lang.Specification

class WhenLoadingFeatureFilesWithEmptyScenariosNames extends Specification {

    def duplicateScenariosFeatureFile = "src/test/resources/features/static_analyse_features/empty_scenario_names.feature"

    def "Should display a meaningful error message if there are empty scenario names"() {
        when:
        CucumberParser parser = new CucumberParser()
        Optional<AnnotatedFeature> feature = parser.loadFeature(new File(duplicateScenariosFeatureFile))
        then:
        InvalidFeatureFileException ex = thrown()
            ex.message.contains("Empty scenario name")
    }
}