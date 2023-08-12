package net.thucydides.model.requirements.model.cucumber

import spock.lang.Specification

class WhenLoadingFeatureFilesWithDuplicateScenarios extends Specification {

    def duplicateScenariosFeatureFile = "src/test/resources/features/static_analyse_features/duplicate_scenario_names.feature"

    def "Should display a meaningful error message if there are duplicate scenario names"() {
        when:
        CucumberParser parser = new CucumberParser()
        parser.loadFeature(new File(duplicateScenariosFeatureFile))
        then:
        InvalidFeatureFileException ex = thrown()
            ex.message.contains("The scenario name 'This scenario is duplicated' was duplicated")
    }
}
