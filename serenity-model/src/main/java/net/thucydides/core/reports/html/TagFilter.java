package net.thucydides.core.reports.html;


import com.google.common.base.Splitter;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TagFilter {

    private final EnvironmentVariables environmentVariables;
    private final RequirementsService requirementsService;

    private final List<String> ALWAYS_HIDDEN_TAGS
            = Arrays.asList("manual-result","manual-test-evidence","manual-last-tested","singlebrowser");

    public TagFilter(EnvironmentVariables environmentVariables) {
        this.requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
        this.environmentVariables = environmentVariables;
    }

    public List<String> filteredTagTypes(List<String> tagTypes) {

        List<String> filteredTags = new ArrayList<>(tagTypes);

        List<String> displayedTags = includedTagTypes();
        if (!displayedTags.isEmpty()) {
            filteredTags = onlyKeepAllowedTypes(filteredTags, displayedTags);
        }

        List<String> excludedTags = new ArrayList<>(excludedTagTypes());
        excludedTags.addAll(ALWAYS_HIDDEN_TAGS);

        return removeUnwantedTags(filteredTags, excludedTags);
    }

    public boolean shouldDisplayTagWithType(String tagType) {
        return !filteredTagTypes(Collections.singletonList(tagType)).isEmpty();
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

    public Set<TestTag> removeHiddenTagsFrom(Set<TestTag> filteredTags) {
        List<String> hiddenTypes = requirementsService.getRequirementTypes();
        hiddenTypes.addAll(ALWAYS_HIDDEN_TAGS);
        Collection<TestTag> hiddenTags = hiddenTags();

        return filteredTags.stream()
                .filter(tag -> !hiddenTypes.contains(tag.getType()))
                .filter(tag -> !contains(hiddenTags,tag))
                .collect(Collectors.toSet());
    }

    private boolean contains(Collection<TestTag> tags, TestTag expectedTag) {
        return tags.stream().anyMatch(
                tag -> tag.getCompleteName().equalsIgnoreCase(expectedTag.getCompleteName())
        );
    }
    private Collection<TestTag> hiddenTags() {
        return Splitter.on(",")
                .splitToList(environmentVariables.optionalProperty("hidden.tags").orElse(""))
                .stream()
                .map(TestTag::withValue)
                .collect(Collectors.toList());
    }

    private Collection<String> hiddenTagTypes() {
        return null;
    }

    public Set<String> rawTagTypes() {
        return new HashSet<>(asLowercaseList(ThucydidesSystemProperty.REPORT_RAW_TAG_LIST.from(environmentVariables)));
    }
}
