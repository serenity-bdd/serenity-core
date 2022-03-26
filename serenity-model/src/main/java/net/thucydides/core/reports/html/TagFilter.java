package net.thucydides.core.reports.html;


import com.google.common.base.Splitter;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_REPORT_EXCLUDE_TAGS;

public class TagFilter {

    private final EnvironmentVariables environmentVariables;
    private final RequirementsService requirementsService;
    private final List<String> excludedTags;

    private final List<String> ALWAYS_HIDDEN_TAGS
            = Arrays.asList("manual-result","manual-test-evidence","manual-last-tested","singlebrowser","Duration");

    public TagFilter(){
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public TagFilter(EnvironmentVariables environmentVariables) {
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
        this.environmentVariables = environmentVariables;
        this.excludedTags = new ArrayList<>(EnvironmentSpecificConfiguration.from(environmentVariables).getListOfValues(SERENITY_REPORT_EXCLUDE_TAGS));
        this.excludedTags.addAll(EnvironmentSpecificConfiguration.from(environmentVariables).getListOfValues(ThucydidesSystemProperty.HIDDEN_TAGS));
        this.excludedTags.addAll(ALWAYS_HIDDEN_TAGS);
    }

    public List<String> filteredTagTypes(List<String> tagTypes) {

        List<String> filteredTags = new ArrayList<>(tagTypes);

        List<String> displayedTags = includedTagTypes();
        if (!displayedTags.isEmpty()) {
            filteredTags = onlyKeepAllowedTypes(filteredTags, displayedTags);
        }

        List<String> excludedTags = new ArrayList<>(excludedTagTypes());
        excludedTags.addAll(ALWAYS_HIDDEN_TAGS);

        return nonExcludedTags(removeUnwantedTags(filteredTags, excludedTags));
    }

    public boolean shouldDisplayTagWithType(String tagType) {
        return !filteredTagTypes(singletonList(tagType)).isEmpty();
    }

    public boolean shouldDisplayTag(TestTag tag) {
        TagExclusions exclusions = TagExclusions.usingEnvironment(environmentVariables);
        return exclusions.doNotExclude(tag);
    }


    public Set<TestTag> removeTagsOfType(Set<TestTag> tags, String... redundantTagTypes) {
        Set<TestTag> filteredTags = new HashSet<>();
        List<String> maskedTagTypes = Arrays.asList(redundantTagTypes);
        for (TestTag tag : tags) {
            if (!maskedTagTypes.contains(tag.getType())) {
                filteredTags.add(tag);
            }
        }
        return filteredTags;
    }


    public Set<TestTag> removeTagsWithName(Set<TestTag> tags, String name) {
        Set<TestTag> filteredTags = new HashSet<>();
        for (TestTag tag : tags) {
            if (!tag.getShortName().equalsIgnoreCase(name)) {
                filteredTags.add(tag);
            }
        }
        return filteredTags;
    }

    private List<String> onlyKeepAllowedTypes(List<String> tags, List<String> displayedTags) {
        List<String> allowedTags = new ArrayList<>();
        for (String tag : tags) {
            if (displayedTags.contains(tag.toLowerCase())) {
                allowedTags.add(tag);
            }
        }
        return allowedTags;
    }


    private List<String> removeUnwantedTags(List<String> tags, List<String> unwantedTags) {
        for (String tag : unwantedTags) {
            tags.remove(tag.toLowerCase());
            tags.remove(tag.toUpperCase());
        }
        return tags;
    }

    private List<String> nonExcludedTags(List<String> tags) {
        TagExclusions exclusions = TagExclusions.usingEnvironment(environmentVariables);
        return tags.stream()
                .filter(tag -> exclusions.doNotExclude(TestTag.withValue(tag)))
                .collect(Collectors.toList());
    }

    private List<String> includedTagTypes() {
        return asLowercaseList(ThucydidesSystemProperty.DASHBOARD_TAG_LIST.from(environmentVariables));
    }

    private List<String> excludedTagTypes() {
        return asLowercaseList(ThucydidesSystemProperty.DASHBOARD_EXCLUDED_TAG_LIST.from(environmentVariables));
    }


    private List<String> asLowercaseList(String displayTags) {
        if (StringUtils.isNotEmpty(displayTags)) {
            return Splitter.on(",").omitEmptyStrings().trimResults().splitToList(displayTags.toLowerCase());
        } else {
            return new ArrayList<>();
        }
    }

    public Set<TestTag> removeHiddenTagsFrom(Collection<TestTag> filteredTags) {
        List<String> hiddenTypes = requirementsService.getRequirementTypes().stream().map(type -> type + ":*").collect(Collectors.toList());
        hiddenTypes.addAll(excludedTags);

        return filteredTags.stream()
                .filter(tag -> doNotExclude(tag, hiddenTypes))
                .collect(Collectors.toSet());
    }

    private boolean doNotExclude(TestTag tag, List<String> hiddenTagExpressions) {
        return hiddenTagExpressions.stream()
                .noneMatch(hiddenTagExpression -> TagMatch.excluding(hiddenTagExpression).matches(tag));
    }

    public Set<String> rawTagTypes() {
        return new HashSet<>(asLowercaseList(ThucydidesSystemProperty.REPORT_RAW_TAG_LIST.from(environmentVariables)));
    }
}
