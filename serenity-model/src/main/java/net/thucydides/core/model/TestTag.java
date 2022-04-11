package net.thucydides.core.model;

import com.google.common.base.Preconditions;
import net.serenitybdd.core.strings.Joiner;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.compare;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class TestTag implements Comparable<TestTag> {

    public static final TestTag EMPTY_TAG = new TestTag("","");
    public static final String DEFAULT_TAG_TYPE = "tag";

    private final String name;
    private final String type;
    private final String displayName;

    private transient String normalisedName;
    private transient String normalisedType;

    private TestTag(String name, String type) {
        this(name,type, name);
    }

    private TestTag(String name, String type, String displayName) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(displayName);
        this.name = name;
        this.type = type;
        this.displayName = displayName;
    }

    /**
     * Issue tags contain links to an external issue system, so reports are not generated for them.
     */
    public boolean isIssueTag() {
        return type.equalsIgnoreCase("issue");
    }

    public String normalisedName() {
        if (normalisedName == null) { normalisedName = normalised(name); }
        return normalisedName;
    }

    private String normalised(String name) {
        return name.replaceAll("[\\s_-]"," ").toLowerCase();
    }

    public String normalisedType() {
        if (normalisedType == null) { normalisedType = normalised(type); }
        return normalisedType;
    }

    public String getCompleteName() {
        if (isEmpty(name) && isEmpty(type)) { return ""; }

        return Joiner.on("_").join(normalisedType(), normalisedName());
    }


    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return Optional.ofNullable(displayName).orElse(name);
    }

    public String getType() {
        return type;
    }

    public String getShortName() {
        return name.contains("/") ? name.substring(name.indexOf("/") + 1) : name;
    }
    public static TestTagBuilder withName(final String tagName) {
        return new TestTagBuilder(tagName);
    }

    public static TestTag withValue(String value) {
        value = stripLeadingAtSymbol(value);
        if (value.contains(":")) {
            return getTestTag(value, value.indexOf(":"));
        } else if (value.contains("=")) {
            return getTestTag(value, value.indexOf("="));
        } else {
            return TestTag.withName(value.trim()).andType(DEFAULT_TAG_TYPE);
        }
    }

    private static String stripLeadingAtSymbol(String value) {
        return value.startsWith("@") ? value.substring(1) : value;
    }

    public TestTag withDisplayName(String displayName) {
        return new TestTag(name, type, displayName);
    }

    private static TestTag getTestTag(String value, int separatorPosition) {
        String type = value.substring(0, separatorPosition).trim();
        String name = value.substring(separatorPosition + 1).trim();
        return TestTag.withName(name).andType(type);
    }

    @Override
    public int compareTo(TestTag otherTag) {
        int typeComparison = compare(getType(), otherTag.getType());
        if (typeComparison != 0) {
            return typeComparison;
        } else {
            return getName().compareToIgnoreCase(otherTag.getName());
        }
    }

    public boolean isAsOrMoreSpecificThan(TestTag testTag) {
        if (this.equals(testTag)) {
            return true;
        }
        if ((this.getType().equals(testTag.getType()) && testTag.normalisedName().replaceFirst(".*?([^.]+)$", "$1").equals(this.normalisedName()))) {
            return true;
        }
        return (this.normalisedName().endsWith("/" + testTag.normalisedName())) && (this.getType().equals(testTag.getType()));
    }

    public static class TestTagBuilder {
        private final String name;

        public TestTagBuilder(String name) {
            this.name = name;
        }
        
        public TestTag andType(String type) {
            return new TestTag(name, withoutTagSymbol(type));
        }

        private String withoutTagSymbol(String type) {
            return type.startsWith("@") ? type.substring(1) : type;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestTag testTag = (TestTag) o;

        if (!normalisedName().equals(testTag.normalisedName())) return false;
        return normalisedType().equals(testTag.normalisedType());
    }

    public boolean equalsIgnoreCase(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestTag testTag = (TestTag) o;

        if (!normalisedName().equalsIgnoreCase(testTag.normalisedName())) return false;
        return normalisedType().equalsIgnoreCase(testTag.normalisedType());
    }

    @Override
    public int hashCode() {
        int result = normalisedName().hashCode();
        result = 31 * result + normalisedType().hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (type.isEmpty()) {
            return name;
        } else {
            return type + ":" + name;
        }
    }
}
