package net.thucydides.core.model;

import com.google.common.base.Preconditions;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class TestTag implements Comparable<TestTag> {

    public static final TestTag EMPTY_TAG = new TestTag("","");

    private final String name;
    private final String type;

    private TestTag(String name, String type) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(type);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
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
        if (value.contains(":")) {
            return getTestTag(value, value.indexOf(":"));
        } else if (value.contains("=")) {
            return getTestTag(value, value.indexOf("="));
        } else {
            return TestTag.withName(value.trim()).andType("tag");
        }
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
        if ((this.getName().toLowerCase().endsWith("/" + testTag.getName().toLowerCase())) && (this.getType().equals(testTag.getType()))) {
            return true;
        }
        return false;
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

        if (!name.equalsIgnoreCase(testTag.name)) return false;
        if (!type.equalsIgnoreCase(testTag.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.toLowerCase().hashCode();
        result = 31 * result + type.toLowerCase().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TestTag{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
