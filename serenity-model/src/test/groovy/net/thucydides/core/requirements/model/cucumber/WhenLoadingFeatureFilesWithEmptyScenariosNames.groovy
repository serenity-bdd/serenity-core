package net.thucydides.core.requirements.model.cucumber

import spock.lang.Specification

class WhenLoadingFeatureFilesWithEmptyScenariosNames extends Specification {

    def "Should display a meaningful error message if there are empty scenario names"() {
        given:
        def scenariosFeatureFile = "src/test/resources/features/static_analyse_features/empty_scenario_names.feature"
        when:
        CucumberParser parser = new CucumberParser()
        parser.loadFeature(new File(scenariosFeatureFile))
        then:
        InvalidFeatureFileException ex = thrown()
        ex.message.contains("Empty scenario name")
    }

    def "Should display a meaningful error message if there are empty example names"() {
        given:
        def scenariosFeatureFile = "src/test/resources/features/static_analyse_features/empty_example_names.feature"
        when:
        CucumberParser parser = new CucumberParser()
        parser.loadFeature(new File(scenariosFeatureFile))
        then:
        InvalidFeatureFileException ex = thrown()
        ex.message.contains("Empty scenario name")
    }

    def "Should display a meaningful error message if there are empty rule names"() {
        given:
        def scenariosFeatureFile = "src/test/resources/features/static_analyse_features/empty_rule_names.feature"
        when:
        CucumberParser parser = new CucumberParser()
        parser.loadFeature(new File(scenariosFeatureFile))
        then:
        InvalidFeatureFileException ex = thrown()
        ex.message.contains("Empty rule name")
    }
}
