package net.thucydides.core.requirements.model.cucumber

import spock.lang.Specification

class WhenLoadingFeatureFilesWithEmptyFeatureName extends Specification {

    def emptyFeatureNameFile = "src/test/resources/features/static_analyse_features/empty_feature_name.feature"

    def "Should display a meaningful error message if there are empty feature names"() {
        when:
        CucumberParser parser = new CucumberParser()
        Optional<AnnotatedFeature> feature = parser.loadFeature(new File(emptyFeatureNameFile))
        then:
        InvalidFeatureFileException ex = thrown()
            ex.message.contains("Empty feature name")
    }
}