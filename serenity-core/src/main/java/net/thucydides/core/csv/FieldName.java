package net.thucydides.core.csv;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class to convert CSV headings to valid JavaBean property names.
 */
public final class FieldName {

    private final String columnHeading;

    public FieldName(final String columnHeading) {
        this.columnHeading = columnHeading;
    }

    public String inNormalizedForm() {

        String[] words = columnHeading.trim().split("\\s");

        StringBuffer buffer = new StringBuffer();
        boolean isFirstWord = true;
        for(String word : words) {
            if (isFirstWord) {
                buffer.append(word.toLowerCase());
                isFirstWord = false;
            } else {
                buffer.append(StringUtils.capitalize(word.toLowerCase()));
            }
        }
        return buffer.toString();
    }

    public static FieldName from(final String columnHeading) {
        return new FieldName(columnHeading);
    }
}