package net.thucydides.model.requirements.model.cucumber;

import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.Rule;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.thucydides.model.requirements.model.cucumber.FeatureFileAnaysisErrors.DUPLICATE_FEATURE_NAME;

/**
 * Check whether a stream of files contains valid feature files, and throw an InvalidFeatureFileException if one is either not valid Gherkin syntax,
 * or contains errors or inconsistencies such as empty or duplicate scenario names
 */
public class FeatureFileChecker {

    CucumberParser cucumberParser = new CucumberParser();

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureFileChecker.class);

    public void check(Stream<File> files, boolean allowDuplicateFeatureNames) {

        List<String> featureFileNames = new ArrayList<>();
        // Features can have duplicate names but a feature file name and parent directory name should be unique
        ConcurrentHashMap<String, List<File>> pathNamesToFeatureFiles = new ConcurrentHashMap<>();
        Map<String, String> featureFileToResult = new TreeMap<>();

        List<String> errorMessages = files
                .filter(File::isFile)
                .map(featureFile -> {
                    try {
                        Optional<AnnotatedFeature> loadedFeature = cucumberParser.loadFeature(featureFile);
                        loadedFeature.ifPresent(
                                annotatedFeature -> {
                                    recordFeaturePath(pathNamesToFeatureFiles, featureFile, annotatedFeature);
                                    featureFileNames.add(annotatedFeature.getFeature().getName());
                                    checkTagsIn(annotatedFeature.getFeature());
                                }
                        );
                        return Optional.empty();
                    } catch (Throwable invalidFeatureFile) {
                        return Optional.of("* Error found in feature file: " + shortenedFeatureFilePath(featureFile.getAbsolutePath())
                                + System.lineSeparator()
                                + " -> " + invalidFeatureFile.getMessage());
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Object::toString)
                .distinct()
                .collect(Collectors.toList());

        // Check for duplicate feature names
        // Feature file names should be unique, or unique within a folder
        if (!allowDuplicateFeatureNames) {
            errorMessages.addAll(
                    checkForDuplicateFeatureNames(featureFileNames)
            );
        }

        if (!errorMessages.isEmpty()) {
            throw new InvalidFeatureFileException(
                    "INVALID FEATURE FILES FOUND:" + System.lineSeparator() +
                    errorMessages.stream().collect(Collectors.joining(System.lineSeparator())));
        }
    }

    private String shortenedFeatureFilePath(String absolutePath) {
        return (absolutePath.contains("/features/")) ? absolutePath.substring(absolutePath.indexOf("/features/") + 10) : absolutePath;
    }

    private void checkTagsIn(Feature feature) {
        checkTags(feature.getTags());
        feature.getChildren().forEach(
                child -> {
                    if (child.getScenario().isPresent()) {
                        checkTagsInScenario(child.getScenario().get());
                    } else if (child.getRule().isPresent()) {
                        checkTagsInRule(child.getRule().get());
                    }
                }
        );
    }

    private void checkTagsInScenario(Scenario scenario) {
        checkTags(scenario.getTags());
        if (!scenario.getExamples().isEmpty()) {
            List<Tag> exampleTags = scenario.getExamples()
                    .stream()
                    .flatMap(examples -> examples.getTags().stream())
                    .collect(Collectors.toList());
            checkTags(exampleTags);
        }
    }

    private void checkTagsInRule(Rule rule) {
        checkTags(rule.getTags());
        rule.getChildren().forEach(
                child -> {
                    if (child.getScenario().isPresent()) {
                        checkTagsInScenario(child.getScenario().get());
                    }
                }
        );
    }

    private void checkTags(List<Tag> tags) {
        for(Tag tag : tags) {
            if (tag.getName().trim().endsWith(":")) {
                throw new InvalidFeatureFileException("Invalid tag format at " + tag.getLocation() + " - tags in the format <name>:<value> (e.g. '@color:red') must have a value after the colon");
            }
            if (tag.getName().trim().endsWith("=")) {
                throw new InvalidFeatureFileException("Invalid tag format at " + tag.getLocation() + " - tags in the format <name>=<value> (e.g. '@color=red') must have a value after the equals sign");
            }
        }
    }


    private Collection<String> checkForDuplicateFeatureNames(List<String> featureFileNames) {
        // Return the list of duplicate feature names in featureFileNames
        return featureFileNames.stream()
                .collect(Collectors.groupingBy(name -> name))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(entry -> "Duplicate feature name found: '" + entry.getKey() + "'")
                .collect(Collectors.toList());
    }

    private String duplicateFeaturePathsError(String key, List<File> value) {
        String featureFilesWithDuplicates = value.stream()
                .map(file -> "      - " + file.getPath())
                .collect(Collectors.joining(System.lineSeparator()));

        return String.format("* " + DUPLICATE_FEATURE_NAME, key, featureFilesWithDuplicates);
    }

    private static void recordFeaturePath(ConcurrentHashMap<String, List<File>> pathNamesToFeatureFiles,
                                          File featureFile,
                                          AnnotatedFeature loadedFeature) {
        String featureName = loadedFeature.getFeature().getName();
        String parentName = new File(featureFile.getParent()).getName();
        String localFeaturePath = parentName + "/" + featureName;

        if (pathNamesToFeatureFiles.containsKey(localFeaturePath)) {
            pathNamesToFeatureFiles.get(localFeaturePath).add(featureFile);
        } else {
            List<File> featureFiles = new ArrayList<>();
            featureFiles.add(featureFile);
            pathNamesToFeatureFiles.put(localFeaturePath, featureFiles);
        }
    }
}
