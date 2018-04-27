package net.thucydides.core.requirements.model;

import com.google.common.base.Preconditions;
import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.model.TestTag;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * A requirement represents a high-level business goal that will appear in the result summary report.
 * This report summarizes the state of the application in terms of what /** have been implemented.
 * Capabilities are implemented via <em>features</em>, which in turn are tested by scenarios.
 */
public class Requirement implements Comparable {

    private String displayName;
    private String name;
    private String id;
    private String type;
    private String path;
    private String featureFileName;
    private CustomFieldValue narrative;
    private String cardNumber;
    private List<Requirement> children;
    private String parent;
    private List<Example> examples;
    private List<String> releaseVersions;
    private List<CustomFieldValue> customFields;

    public Requirement() {
        // Used by Jackson
        children = new ArrayList<>();
        examples = new ArrayList<>();
        releaseVersions = new ArrayList<>();
        customFields = new ArrayList<>();
    }

    protected Requirement(String name, String id, String displayName, String cardNumber, String parent, String type, CustomFieldValue narrative,
                          List<Requirement> children, List<Example> examples,
                          List<String> releaseVersions) {
        this(name, id, displayName, cardNumber, parent, type, "", narrative, children, examples,releaseVersions, Collections.EMPTY_LIST);
    }

    protected Requirement(String name, String id, String displayName, String cardNumber, String parent, String type, CustomFieldValue narrative) {
        this(name, id, displayName, cardNumber, parent, type, "", narrative, Collections.EMPTY_LIST, Collections.EMPTY_LIST,Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    protected Requirement(String name, String id, String displayName, String cardNumber, String parent, String type, String path, CustomFieldValue narrative,
                          List<Requirement> children, List<Example> examples,
                          List<String> releaseVersions,
                          List<CustomFieldValue> customFields,
                          String featureFileName) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(type);
        this.name = name;
        this.id = id;
        this.displayName = (displayName != null) ? displayName : name;
        this.cardNumber = cardNumber;
        this.type = type;
        this.path = path;
        this.parent = parent;
        this.narrative = narrative;
        this.children = new ArrayList<>(children);
        this.examples = new ArrayList<>(examples);
        this.releaseVersions = NewList.copyOf(releaseVersions);
        this.customFields = NewList.copyOf(customFields);
        this.featureFileName = featureFileName;
    }

    protected Requirement(String name, String id, String displayName, String cardNumber, String parent, String type, String path,
                          CustomFieldValue narrative,
                          List<Requirement> children,
                          List<Example> examples,
                          List<String> releaseVersions,
                          List<CustomFieldValue> customFields) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(type);
        this.name = name;
        this.id = id;
        this.displayName = (displayName != null) ? displayName : name;;
        this.cardNumber = cardNumber;
        this.type = type;
        this.parent = parent;
        this.narrative = narrative;
        this.children = NewList.copyOf(children);
        this.examples = NewList.copyOf(examples);
        this.releaseVersions = NewList.copyOf(releaseVersions);
        this.customFields = NewList.copyOf(customFields);
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return (displayName != null) ? displayName : name;
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

    public int getChildrenCount() {
        return children.size();
    }

    public String getParent() {
        return parent;
    }

    public List<Requirement> getChildren() {
        return NewList.copyOf(children);
    }

    public List<Example> getExamples() {
        return NewList.copyOf(examples);
    }

    public Boolean hasExamples() {
        return !examples.isEmpty();
    }

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

    public Requirement definedInFile(File featureFile) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type,  this.path, this.narrative,
                                this.children, this.examples, this.releaseVersions,  this.customFields, featureFile.getName());
    }

    public Requirement withChildren(List<Requirement> children) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type,  this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName);
    }

    public void setChildren(List<Requirement> children) {
        this.children = NewList.copyOf(children);
    }

    public Requirement withParent(String parent) {
        return new Requirement(this.name,  this.id, this.displayName, this.cardNumber, parent, this.type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName);
    }


    public Requirement withType(String type) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName);
    }

    public Requirement withDisplayName(String displayName) {
        return new Requirement(this.name, this.id, displayName, this.cardNumber, this.parent, type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName);
    }

    public Requirement withFeatureFileyName(String featureFileName) {
        return new Requirement(this.name, this.id, displayName, this.cardNumber, this.parent, type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName);
    }

    public Requirement withExample(Example example) {
        List<Example> updatedExamples = new ArrayList<>(examples);
        updatedExamples.add(example);
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative, children, updatedExamples, releaseVersions, customFields, featureFileName);
    }

    public Requirement withExamples(List<Example> examples) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName);
    }

    public Requirement withReleaseVersions(List<String> releaseVersions) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName);
    }

    public Requirement withCustomFields(List<CustomFieldValue> customFields) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName);
    }


    public Requirement withPath(String path) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, path, this.narrative, children, examples, releaseVersions, customFields, featureFileName);
    }

    public boolean hasChildren() {
        return (children != null) && (!children.isEmpty());
    }

    public List<Requirement> getNestedChildren() {
        List<Requirement> nestedChildren = new ArrayList<>();
        for(Requirement child : children) {
            nestedChildren.add(child);
            nestedChildren.addAll(child.getNestedChildren());
        }
        return NewList.copyOf(nestedChildren);
    }

    public TestTag asTag() {
        return TestTag.withName(qualifiedName()).andType(getType());
    }

    public TestTag asUnqualifiedTag() {
        return TestTag.withName(getName()).andType(getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Requirement that = (Requirement) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    public boolean matches(Requirement that) {
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (StringUtils.isNotEmpty(cardNumber) ? !cardNumber.equals(that.cardNumber) : that.cardNumber != null) return false;

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
        List<Requirement> newChildren = new ArrayList(children);
        newChildren.remove(child);
        newChildren.add(child);
        return new Requirement(name, id, displayName,cardNumber,parent, type, narrative, newChildren, examples,releaseVersions);
    }

    public CustomFieldSetter withCustomField(String fieldName) {
        return new CustomFieldSetter(this, fieldName);
    }

    public List<CustomFieldValue> getCustomFieldValues() {
        return NewList.copyOf(customFields);
    }

    public Optional<CustomFieldValue> getCustomField(String fieldName) {
        for(CustomFieldValue field : customFields) {
            if (field.getName().equals(fieldName)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    public List<String> getCustomFields() {
        List<String> customFieldNames = new ArrayList<>();
        for(CustomFieldValue field : customFields) {
            customFieldNames.add(field.getName());
        }
        return customFieldNames;
    }

    public String getFeatureFileName() {
        return featureFileName;
    }

    public String  qualifiedName() {
        return (StringUtils.isNotEmpty(qualifier())) ? qualifier() + "/" + getName() : getName();
    }

    public String qualifier() {
        return (getParent() != null) ? getParent() : null;
    }

    public boolean matchesTag(TestTag testTag) {
        TestTag requirementTag = asTag();
        return requirementTag.isAsOrMoreSpecificThan(testTag);
    }

    public Requirement merge(Requirement newRequirement) {

        String mergedCardNumber = isEmpty(cardNumber) ? newRequirement.cardNumber : cardNumber;
        String mergedDisplayName= (isEmpty(displayName) || name.equalsIgnoreCase(displayName))  ? newRequirement.displayName : displayName;
        String mergedNarrativeText = isEmpty(narrative.getText()) ? newRequirement.narrative.getText() : narrative.getText();
        String mergedPath = isEmpty(path) ? newRequirement.path : path;
        String mergedFeatureFileName = isEmpty(featureFileName) ? newRequirement.featureFileName : featureFileName;
        List<String> mergedReleasVersions = ListUtils.union(releaseVersions, newRequirement.releaseVersions) ;
        List<Example> mergedExamples = ListUtils.union(examples, newRequirement.examples);
        List<CustomFieldValue> mergedCustomFields = ListUtils.union(customFields, newRequirement.customFields);

        List<Requirement> mergedChildren = mergeRequirementLists(children, newRequirement.children);

        return Requirement.named(name)
                .withOptionalParent(parent)
                .withOptionalCardNumber(mergedCardNumber)
                .withType(type)
                .withNarrative(mergedNarrativeText)
                .withPath(mergedPath)
                .withDisplayName(mergedDisplayName)
                .withReleaseVersions(mergedReleasVersions)
                .withExamples(mergedExamples)
                .withCustomFields(mergedCustomFields)
                .withFeatureFileyName(mergedFeatureFileName)
                .withChildren(mergedChildren);
    }

    private List<Requirement> mergeRequirementLists(List<Requirement> existingChilden, List<Requirement> newChildren) {
        List<Requirement> mergedChildren = new ArrayList<>(existingChilden);
        for(Requirement newChild : newChildren) {
            if (mergedChildren.contains(newChild)) {
                Requirement existingChild = mergedChildren.remove(mergedChildren.indexOf(newChild));
                mergedChildren.add(existingChild.merge(newChild));
            } else {
                mergedChildren.add(newChild);
            }
        }
        return NewList.copyOf(mergedChildren);
    }

    public Requirement withNarrative(String narrativeText) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, parent, this.type, this.path, new CustomFieldValue("Narrative", narrativeText),
                               children, examples, releaseVersions, customFields, featureFileName);
    }

    public String getPath() {
        return path;
    }

    public static class CustomFieldSetter {

        Requirement requirement;
        String fieldName;

        public CustomFieldSetter(Requirement requirement, String fieldName) {
            this.requirement = requirement;
            this.fieldName = fieldName;
        }

        public Requirement setTo(String value, String renderedValue) {
            List<CustomFieldValue> customFields = new ArrayList(requirement.getCustomFieldValues());
            customFields.add(new CustomFieldValue(fieldName, value, renderedValue));
            return new Requirement(requirement.name, requirement.id, requirement.displayName,
                    requirement.cardNumber, requirement.parent, requirement.type, requirement.path, requirement.narrative,
                    requirement.children, requirement.examples, requirement.releaseVersions,
                    customFields);
        }

        public Requirement setTo(String value) {
            return setTo(value, null);
        }
    }
}
