package net.thucydides.core.requirements.model.cucumber

import spock.lang.Specification

class WhenLoadingFeatureFilesWithEmptyFeatureName extends Specification {

    def emptyFeatureNameFile = "src/test/resources/features/static_analyse_features/empty_feature_name.feature"

    def "Should display a meaningful error message if there are empty feature names"() {
        when:
        CucumberParser parser = new CucumberParser()
        parser.loadFeature(new File(emptyFeatureNameFile))
        then:
        InvalidFeatureFileException ex = thrown()
            ex.message.contains("The feature name in 'empty_feature_name.feature' is empty")
    }
}
