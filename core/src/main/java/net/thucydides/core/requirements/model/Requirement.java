package net.thucydides.core.requirements.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.model.TestTag;

import java.util.Collections;
import java.util.List;

/**
 * A capability represents a high-level business goal that will appear in the result summary report.
 * This report summarizes the state of the application in terms of what capabilities have been implemented.
 * Capabilities are implemented via <em>features</em>, which in turn are tested by scenarios.
 */
public class Requirement implements Comparable {

    private String displayName;
    private String name;
    private String type;
    private CustomFieldValue narrative;
    private String cardNumber;
    private List<Requirement> children;
    private String parent;
    private List<Example> examples;
    private List<String> releaseVersions;
    private List<CustomFieldValue> customFields;

    public Requirement() {
        // Used by Jackson
        children = Lists.newArrayList();
        examples = Lists.newArrayList();
        releaseVersions = Lists.newArrayList();
        customFields = Lists.newArrayList();
    }

    protected Requirement(String name, String displayName, String cardNumber, String parent, String type, CustomFieldValue narrative,
                          List<Requirement> children, List<Example> examples,
                          List<String> releaseVersions) {
        this(name, displayName, cardNumber, parent, type, narrative, children, examples,releaseVersions, Collections.EMPTY_LIST);
    }

    protected Requirement(String name, String displayName, String cardNumber, String parent, String type, CustomFieldValue narrative) {
        this(name, displayName, cardNumber, parent, type, narrative, Collections.EMPTY_LIST, Collections.EMPTY_LIST,Collections.EMPTY_LIST,
                 Collections.EMPTY_LIST);
    }

    protected Requirement(String name, String displayName, String cardNumber, String parent, String type,  CustomFieldValue narrative,
                          List<Requirement> children, List<Example> examples,
                          List<String> releaseVersions,
                          List<CustomFieldValue> customFields) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(type);
        this.name = name;
        this.displayName = displayName;
        this.cardNumber = cardNumber;
        this.type = type;
        this.parent = parent;
        this.narrative = narrative;
        this.children = ImmutableList.copyOf(children);
        this.examples = ImmutableList.copyOf(examples);
        this.releaseVersions = ImmutableList.copyOf(releaseVersions);
        this.customFields = ImmutableList.copyOf(customFields);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public String childType() {
        return (!children.isEmpty()) ? children.get(0).getType() : null;
    }

    public CustomFieldValue getNarrative() {
        return narrative;
    }

    public List<String> getReleaseVersions() {
        return releaseVersions;
    }

    @JsonIgnore
    public int getChildrenCount() {
        return children.size();
    }

    public String getParent() {
        return parent;
    }

    public List<Requirement> getChildren() {
        return ImmutableList.copyOf(children);
    }

    public List<Example> getExamples() {
        return ImmutableList.copyOf(examples);
    }

    public Boolean hasExamples() {
        return !examples.isEmpty();
    }

    @JsonIgnore
    public int getExampleCount() {
        return examples.size();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int compareTo(Object otherRequirement) {
        return name.compareTo(((Requirement) otherRequirement).getName());
    }

    public static RequirementBuilderNameStep named(String name) {
        return new RequirementBuilderNameStep(name);
    }

    public Requirement withChildren(List<Requirement> children) {
        return new Requirement(this.name, this.displayName, this.cardNumber, this.parent, this.type,  this.narrative, children, examples, releaseVersions);
    }

    public Requirement withParent(String parent) {
        return new Requirement(this.name, this.displayName, this.cardNumber, parent, this.type, this.narrative, children, examples, releaseVersions);
    }


    public Requirement withType(String type) {
        return new Requirement(this.name, this.displayName, this.cardNumber, this.parent, type, this.narrative, children, examples, releaseVersions);
    }

    public Requirement withExample(Example example) {
        List<Example> updatedExamples = Lists.newArrayList(examples);
        updatedExamples.add(example);
        return new Requirement(this.name, this.displayName, this.cardNumber, this.parent, this.type, this.narrative, children, updatedExamples, releaseVersions);
    }

    public Requirement withExamples(List<Example> examples) {
        return new Requirement(this.name, this.displayName, this.cardNumber,  this.parent, this. type,this.narrative, children, examples, releaseVersions);
    }

    public Requirement withReleaseVersions(List<String> releaseVersions) {
        return new Requirement(this.name, this.displayName, this.cardNumber, this.parent, this.type, this.narrative, children, examples, releaseVersions);
    }

    public boolean hasChildren() {
        return (children != null) && (!children.isEmpty());
    }

    public List<Requirement> getNestedChildren() {
        List<Requirement> nestedChildren = Lists.newArrayList();
        for(Requirement child : children) {
            nestedChildren.add(child);
            nestedChildren.addAll(child.getNestedChildren());
        }
        return ImmutableList.copyOf(nestedChildren);
    }

    public TestTag asTag() {

        return TestTag.withName(qualifiedName()).andType(getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Requirement that = (Requirement) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    public boolean matches(Requirement that) {
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (cardNumber != null ? !cardNumber.equals(that.cardNumber) : that.cardNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' + " parent = '" + parent + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                '}';
    }

    public Requirement withChild(Requirement child) {
        List<Requirement> newChildren = Lists.newArrayList(children);
        newChildren.remove(child);
        newChildren.add(child);
        return new Requirement(name,displayName,cardNumber,parent, type, narrative, newChildren, examples,releaseVersions);
    }

    public CustomFieldSetter withCustomField(String fieldName) {
        return new CustomFieldSetter(this, fieldName);
    }

    public List<CustomFieldValue> getCustomFieldValues() {
        return ImmutableList.copyOf(customFields);
    }

    public Optional<CustomFieldValue> getCustomField(String fieldName) {
        for(CustomFieldValue field : customFields) {
            if (field.getName().equals(fieldName)) {
                return Optional.of(field);
            }
        }
        return Optional.absent();
    }

    public List<String> getCustomFields() {
        List<String> customFieldNames = Lists.newArrayList();
        for(CustomFieldValue field : customFields) {
            customFieldNames.add(field.getName());
        }
        return customFieldNames;
    }

    public String qualifiedName() {
        return (qualifier() != null) ? qualifier() + "/" + getName() : getName();
    }

    public String qualifier() {
        if (getCardNumber() != null) {
            return getCardNumber();
        }
        return (getParent() != null) ? getParent() : null;
//        if (getCardNumber() != null) {
//            return getCardNumber();
//        } else
//        if (getParent() != null) {
//            return getParent();
//        } else {
//            return null;
//        }
    }

    public boolean matchesTag(TestTag testTag) {
        TestTag requirementTag = asTag();
        return requirementTag.isAsOrMoreSpecificThan(testTag);
    }

    public class CustomFieldSetter {

        Requirement requirement;
        String fieldName;

        public CustomFieldSetter(Requirement requirement, String fieldName) {
            this.requirement = requirement;
            this.fieldName = fieldName;
        }

        public Requirement setTo(String value, String renderedValue) {
            List<CustomFieldValue> customFields = Lists.newArrayList(requirement.getCustomFieldValues());
            customFields.add(new CustomFieldValue(fieldName, value, renderedValue));
            return new Requirement(requirement.name, requirement.displayName,
                    requirement.cardNumber, requirement.parent, requirement.type, requirement.narrative,
                    requirement.children, requirement.examples, requirement.releaseVersions,
                    customFields);
        }

        public Requirement setTo(String value) {
            return setTo(value, null);
        }
    }
}
