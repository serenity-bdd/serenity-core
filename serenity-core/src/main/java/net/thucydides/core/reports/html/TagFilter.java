package net.thucydides.core.reports.html;


import com.google.common.base.Splitter;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TagFilter {

    private final EnvironmentVariables environmentVariables;

    public TagFilter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<String> filteredTagTypes(List<String> tagTypes) {

        List<String> filteredTags = new ArrayList<>(tagTypes);

        List<String> displayedTags = includedTagTypes();
        if (!displayedTags.isEmpty()) {
            filteredTags = onlyKeepAllowedTypes(filteredTags, displayedTags);
        }

        List<String> excludedTags = excludedTagTypes();
        if (!excludedTags.isEmpty()) {
            filteredTags = removeUnwantedTags(filteredTags, excludedTags);
        }

        return filteredTags;
    }


    public Set<TestTag> removeTagsOfType(Set<TestTag> tags, String... redundantTagTypes) {
        Set<TestTag> filteredTags = new HashSet();
        List<String> maskedTagTypes = Arrays.asList(redundantTagTypes);
        for (TestTag tag : tags) {
            if (!maskedTagTypes.contains(tag.getType())) {
                filteredTags.add(tag);
            }
        }
        return filteredTags;
    }


    public Set<TestTag> removeTagsWithName(Set<TestTag> tags, String name) {
        Set<TestTag> filteredTags = new HashSet();
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
            if (tags.contains(tag.toLowerCase())) {
                tags.remove(tag.toLowerCase());
            }
            if (tags.contains(tag.toUpperCase())) {
                tags.remove(tag.toUpperCase());
            }
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
}
