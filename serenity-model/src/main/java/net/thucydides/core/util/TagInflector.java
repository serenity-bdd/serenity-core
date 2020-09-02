package net.thucydides.core.util;

import net.thucydides.core.reports.html.TagFilter;

import static org.apache.commons.lang3.StringUtils.lowerCase;

public class TagInflector {

    private static TagFilter tagFilter;

    public TagInflector(EnvironmentVariables environmentVariables) {
        tagFilter = new TagFilter(environmentVariables);
    }

    public InflectableTag ofTag(String tagType, String tagName) {
        return new InflectableTag(tagType, tagName);
    }

    public static final class InflectableTag {

        private String tagType;
        private String tagName;

        InflectableTag(String tagType, String tagName) {
            this.tagType = tagType;
            this.tagName = tagName;
        }

        public String toFinalView() {
            if (shouldFormatAsTitle()) {
                return asTitle();
            } else {
                return rawName();
            }
        }

        private boolean shouldFormatAsTitle() {
            return !tagFilter.rawTagTypes().contains(lowerCase(tagType));
        }

        private String asTitle() {
            return Inflector.getInstance().of(tagName).asATitle().toString();
        }

        private String rawName() {
            return tagName;
        }
    }
}
