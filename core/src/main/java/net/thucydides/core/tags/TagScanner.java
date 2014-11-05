package net.thucydides.core.tags;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static net.thucydides.core.tags.TagConverters.fromStringValuesToTestTags;

public class TagScanner {

    private final EnvironmentVariables environmentVariables;

    public TagScanner(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean shouldRunClass(Class<?> testClass) {
        List<TestTag> expectedTags = expectedTags();

        if (expectedTags.isEmpty()) {
            return true;
        } else {
            return testClassContainsAtLeastOneExpectedTag(testClass, expectedTags);
        }
    }

    public boolean shouldRunMethod(Class<?> testClass, String methodName) {
        List<TestTag> expectedTags = expectedTags();

        if (expectedTags.isEmpty()) {
            return true;
        } else {
            return testMethodContainsAtLeastOneExpectedTag(testClass, methodName, expectedTags);
        }
    }

    private boolean testClassContainsAtLeastOneExpectedTag(Class<?> testClass, List<TestTag> expectedTags) {
        List<TestTag> tags = TestAnnotations.forClass(testClass).getTags();
        return tagsMatch(expectedTags, tags);
    }

    private boolean testMethodContainsAtLeastOneExpectedTag(Class<?> testClass, String methodName, List<TestTag> expectedTags) {
        List<TestTag> tags = TestAnnotations.forClass(testClass).getTagsForMethod(methodName);
        return tagsMatch(expectedTags, tags);
    }

    private boolean tagsMatch(List<TestTag> expectedTags, List<TestTag> tags) {
        for (TestTag expectedTag : expectedTags) {
            if (tags.contains(expectedTag)) {
                return true;
            }
        }
        return false;
    }

    private List<TestTag> expectedTags() {
        String tagListValue = environmentVariables.getProperty(ThucydidesSystemProperty.TAGS);
        if (StringUtils.isNotEmpty(tagListValue)) {
            List<String> tagList = Lists.newArrayList(Splitter.on(",").trimResults().split(tagListValue));
            return convert(tagList, fromStringValuesToTestTags());
        } else {
            return Lists.newArrayList();
        }
    }


}
