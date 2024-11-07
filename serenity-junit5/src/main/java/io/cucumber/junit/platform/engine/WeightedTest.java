package io.cucumber.junit.platform.engine;

import io.cucumber.tagexpressions.Expression;
import io.cucumber.tagexpressions.TagExpressionParser;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.ClasspathResourceSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static io.cucumber.junit.platform.engine.TestWeightCalculator.calculateWeight;

public class WeightedTest {
    private final TestDescriptor scenario;
    private final int weight;
    private final List<String> tags;

    WeightedTest(TestDescriptor scenario) {
        this(scenario, calculateWeight(scenario));
    }

    public WeightedTest(TestDescriptor scenario, int weight) {
        this.scenario = scenario;
        this.weight = weight;
        this.tags = parseTags(scenario);
    }

    List<String> getTags() {
        return tags;
    }

    String getSourceFile() {
        return this.scenario.getSource()
            .map(ClasspathResourceSource.class::cast)
            .map(ClasspathResourceSource::getClasspathResourceName)
            .orElse(null);
    }

    String getDisplayName() {
        return this.scenario.getDisplayName();
    }

    int getWeight() {
        return weight;
    }

    void removeFromHierarchy() {
        Optional<TestDescriptor> featureOpt = scenario.getParent();
        scenario.removeFromHierarchy();
        featureOpt
            .filter(feature -> feature.getChildren().isEmpty())
            .ifPresent(TestDescriptor::prune);
    }

    boolean isTagMatchingFilter(String tagFilter) {
        if (isNullOrEmpty(tagFilter)) {
            return true;
        }

        Expression expression = TagExpressionParser.parse(tagFilter);
        return expression.evaluate(tags);
    }

    private List<String> parseTags(TestDescriptor test) {
        if (test.isTest()) {
            return test.getTags().stream()
                .map(tag -> "@" + tag)
                .collect(Collectors.toList());
        } else {
            return test.getChildren().stream()
                .map(this::parseTags)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        }
    }

    @Override
    public String toString() {
        return String.format("%s:%s(%s - %d)", getSourceFile(), getDisplayName(), tags, weight);
    }
}
