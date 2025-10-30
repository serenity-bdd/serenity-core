package net.thucydides.model.requirements.model.cucumber

import spock.lang.Specification

import java.util.stream.Collectors

class WhenCheckingACollectionOfFeatureFilesForErrors extends Specification {

    def "Should find all the feature files in a directory"() {
        given:
        FeatureFileFinder finder = new FeatureFileFinder("src/test/resources/features/static_analyse_features")
        when:
        var featureFiles = finder.findFeatureFiles().collect(Collectors.toList())
        then:
        featureFiles.size() == 11
    }

    def "Should pass for real-world examples"() {
        given:
        FeatureFileFinder finder = new FeatureFileFinder("src/test/resources/features/ecommerce_features")
        FeatureFileChecker checker = new FeatureFileChecker();
        when:
        checker.check(finder.findFeatureFiles(), true)
        then:
        notThrown(Exception)
    }

    def "Should display a meaningful error message if there are empty scenario names"() {
        given:
            FeatureFileChecker checker = new FeatureFileChecker();
            FeatureFileFinder finder = new FeatureFileFinder("src/test/resources/features/static_analyse_features")
        when:
            checker.check(finder.findFeatureFiles(), true)
        then:
            InvalidFeatureFileException ex = thrown()
            ex.message.contains("Empty scenario names were found in file 'empty_scenario_names.feature'")
            ex.message.contains("The feature name in 'empty_feature_name.feature' is empty")
            ex.message.contains("The scenario name 'This scenario is duplicated' was duplicated in file 'duplicate_scenario_names.feature'")
    }
}
