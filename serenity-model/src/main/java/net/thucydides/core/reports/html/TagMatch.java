package net.thucydides.core.reports.html;

import net.thucydides.core.model.TestTag;

public class TagMatch {
    private final String excludedTagExpression;

    public TagMatch(String excludedTagExpression) {
        this.excludedTagExpression = excludedTagExpression;
    }

    public static TagMatch excluding(String excludedTagExpression) {
        return new TagMatch(excludedTagExpression);
    }
    public boolean matches(TestTag tag) {
        if (tag.equals(TestTag.withValue(excludedTagExpression))) {
            return true;
        }
        if (excludedTagExpression.endsWith(":*")) {
            String tagType = excludedTagExpression.replace(":*","").trim();
            return tag.getType().trim().equalsIgnoreCase(tagType.trim());
        }
        if (excludedTagExpression.startsWith("*:")) {
            String tagValue = excludedTagExpression.replace("*:","").trim();
            return tag.getName().trim().equalsIgnoreCase(tagValue.trim());
        }
        return false;
    }
}
