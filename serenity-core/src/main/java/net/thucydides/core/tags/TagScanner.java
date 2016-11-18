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
        List<TestTag> providedTags = providedTags();

        if (providedTags.isEmpty()) {
            return true;
        } else {
            return
                    testClassContainsAtLeastOneExpectedTag(testClass, providedTags)
                            && testClassDoesNotContainAtLeastOneNegatedTag(testClass, providedTags);
        }
    }

    public boolean shouldRunMethod(Class<?> testClass, String methodName) {
        List<TestTag> providedTags = providedTags();

        if (providedTags.isEmpty()) {
            return true;
        } else {
            return testMethodContainsAtLeastOneExpectedTag(testClass, methodName, providedTags)
                    && testMethodDoesNotContainAtLeastOneNegatedTag(testClass, methodName, providedTags);
        }
    }

    private boolean testClassContainsAtLeastOneExpectedTag(Class<?> testClass, List<TestTag> expectedTags) {
        List<TestTag> tags = TestAnnotations.forClass(testClass).getTags();
        return tagsMatch(expectedTags, positive(tags));
    }

    private boolean testClassDoesNotContainAtLeastOneNegatedTag(Class<?> testClass, List<TestTag> negatedTags) {
        List<TestTag> tags = TestAnnotations.forClass(testClass).getTags();
        return !tagsMatch(negatedTags, negative(tags));
    }

    private List<TestTag> positive(List<TestTag> tags) {
        List<TestTag> positiveTags = Lists.newArrayList();
        for (TestTag tag : tags) {
            if (!tag.getName().startsWith("!")) {
                positiveTags.add(tag);
            }
        }
        return positiveTags;
    }

    private List<TestTag> negative(List<TestTag> tags) {
        List<TestTag> negativeTags = Lists.newArrayList();
        for (TestTag tag : tags) {
            if (tag.getName().startsWith("!")) {
                negativeTags.add(tag);
            }
        }
        return negativeTags;
    }


    private boolean testMethodContainsAtLeastOneExpectedTag(Class<?> testClass, String methodName, List<TestTag> expectedTags) {
        List<TestTag> tags = TestAnnotations.forClass(testClass).getTagsForMethod(methodName);
        return tagsMatch(expectedTags, positive(tags));
    }

    private boolean testMethodDoesNotContainAtLeastOneNegatedTag(Class<?> testClass, String methodName, List<TestTag> expectedTags) {
        List<TestTag> tags = TestAnnotations.forClass(testClass).getTagsForMethod(methodName);
        return !tagsMatch(expectedTags, negative(tags));
    }



    private boolean tagsMatch(List<TestTag> expectedTags, List<TestTag> tags) {
        for (TestTag expectedTag : expectedTags) {
            if (tags.contains(expectedTag)) {
                return true;
            }
        }
        return false;
    }

    private List<TestTag> providedTags() {
        String tagListValue = environmentVariables.getProperty(ThucydidesSystemProperty.TAGS);
        if (StringUtils.isNotEmpty(tagListValue)) {
            List<String> tagList = Lists.newArrayList(Splitter.on(",").trimResults().split(tagListValue));
            return convert(tagList, fromStringValuesToTestTags());
        } else {
            return Lists.newArrayList();
        }
    }

    private List<TestTag> negatedTags() {
        String tagListValue = environmentVariables.getProperty(ThucydidesSystemProperty.TAGS);
        if (StringUtils.isNotEmpty(tagListValue)) {
            List<String> tagList = Lists.newArrayList(Splitter.on(",").trimResults().split(tagListValue));
            return convert(tagList, fromStringValuesToTestTags());
        } else {
            return Lists.newArrayList();
        }
    }

}
