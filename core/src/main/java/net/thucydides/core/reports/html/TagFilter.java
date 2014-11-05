package net.thucydides.core.reports.html;


import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TagFilter {

    private final EnvironmentVariables environmentVariables;

    public TagFilter(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<String> filteredTagTypes(List<String> tagTypes) {

        List<String> filteredTags = Lists.newArrayList(tagTypes);

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

    private List<String> onlyKeepAllowedTypes(List<String> tags, List<String> displayedTags) {
        List<String> allowedTags = Lists.newArrayList();
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
            return ImmutableList.copyOf(Splitter.on(",").omitEmptyStrings().trimResults().split(displayTags.toLowerCase()));
        } else {
            return Lists.newArrayList();
        }
    }
}
