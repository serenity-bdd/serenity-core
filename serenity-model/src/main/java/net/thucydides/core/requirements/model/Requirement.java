package net.thucydides.core.requirements.model;

import com.google.common.base.Preconditions;
import net.serenitybdd.core.collect.NewList;
import net.thucydides.core.model.TestTag;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.thucydides.core.model.TestTag.DEFAULT_TAG_TYPE;
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
    private List<TestTag> tags = new ArrayList<>();
    private Map<String, Collection<TestTag>> scenarioTags = new HashMap<>();
    private boolean containsNoScenarios = false;
    private FeatureBackgroundNarrative background;

    public Requirement() {
        // Used by Jackson
        children = new ArrayList<>();
        examples = new ArrayList<>();
        releaseVersions = new ArrayList<>();
        customFields = new ArrayList<>();
        scenarioTags = new HashMap<>();
    }

    protected Requirement(String name, String id, String displayName, String cardNumber, String parent, String type, CustomFieldValue narrative,
                          List<Requirement> children, List<Example> examples,
                          List<String> releaseVersions) {
        this(name, id, displayName, cardNumber, parent, type, "", narrative, children, examples, releaseVersions, Collections.EMPTY_LIST);
    }

    protected Requirement(String name, String id, String displayName, String cardNumber, String parent, String type, CustomFieldValue narrative) {
        this(name, id, displayName, cardNumber, parent, type, "", narrative, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
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
        this.path = normalized(path);
        this.parent = parent;
        this.narrative = narrative;
//        this.children = Collections.unmodifiableList(children);
//        this.examples = Collections.unmodifiableList(examples);
//        this.releaseVersions = Collections.unmodifiableList(releaseVersions);
//        this.customFields = Collections.unmodifiableList(customFields);
        this.children = children;
        this.examples = examples;
        this.releaseVersions = releaseVersions;
        this.customFields = customFields;
        this.featureFileName = featureFileName;
        this.tags = new ArrayList<>();
    }

    protected Requirement(String name, String id, String displayName, String cardNumber, String parent, String type, String path, CustomFieldValue narrative,
                          List<Requirement> children, List<Example> examples,
                          List<String> releaseVersions,
                          List<CustomFieldValue> customFields,
                          String featureFileName,
                          List<TestTag> tags,
                          Map<String, Collection<TestTag>> scenarioTags,
                          boolean containsNoScenarios,
                          FeatureBackgroundNarrative background) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(type);
        this.name = name;
        this.id = id;
        this.displayName = (displayName != null) ? displayName : name;
        this.cardNumber = cardNumber;
        this.type = type;
        this.path = normalized(path);
        this.parent = parent;
        this.narrative = narrative;
//        this.children = Collections.unmodifiableList(children);
//        this.examples = Collections.unmodifiableList(examples);
//        this.releaseVersions = Collections.unmodifiableList(releaseVersions);
//        this.customFields = Collections.unmodifiableList(customFields);
        this.children = children;
        this.examples = examples;
        this.releaseVersions = releaseVersions;
        this.customFields = customFields;
        this.featureFileName = featureFileName;
        this.tags = tags;
        this.scenarioTags = scenarioTags;
        this.containsNoScenarios = containsNoScenarios;
        this.background = background;
    }

    protected Requirement(String name,
                          String id,
                          String displayName,
                          String cardNumber,
                          String parent,
                          String type,
                          String path,
                          CustomFieldValue narrative,
                          List<Requirement> children,
                          List<Example> examples,
                          List<String> releaseVersions,
                          List<CustomFieldValue> customFields) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(type);
        this.name = name;
        this.id = id;
        this.displayName = (displayName != null) ? displayName : name;
        this.cardNumber = cardNumber;
        this.type = type;
        this.parent = parent;
        this.narrative = narrative;
//        this.children = Collections.unmodifiableList(children);
//        this.examples = Collections.unmodifiableList(examples);
//        this.releaseVersions = Collections.unmodifiableList(releaseVersions);
//        this.customFields = Collections.unmodifiableList(customFields);
        this.children = children;
        this.examples = examples;
        this.releaseVersions = releaseVersions;
        this.customFields = customFields;
        this.path = normalized(path);
        this.tags = new ArrayList<>();
    }


    private String normalized(String path) {
        return path.replaceAll("\\\\","/");
    }

    public Requirement withNoScenarios() {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative,
                this.children, this.examples, this.releaseVersions, this.customFields, this.name, this.tags, this.scenarioTags, true, this.background);
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

    public String getDisplayName(boolean includeParent) {
        if (includeParent) {
            String parentName = StringUtils.isNotEmpty(getParent()) ? " (" + getParent() + ")" : "";
            return getDisplayName() + parentName;
        } else {
            return getDisplayName();
        }
    }

    public String getDisplayNameWithParent() {
        String parentName = StringUtils.isNotEmpty(getParent()) ? getParent() + " > " : "";
        return parentName + getDisplayName();
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
        return children; //NewList.copyOf(children);
    }

    public Stream<Requirement> getChildrenAsStream() {
        return children.stream();
    }

    public boolean hasChild(Requirement child) {
        return children.contains(child);
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
        return getOrder().compareTo(((Requirement) otherRequirement).getOrder());
    }

    public static RequirementBuilderNameStep named(String name) {
        return new RequirementBuilderNameStep(name);
    }

    public Requirement definedInFile(File featureFile) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative,
                this.children, this.examples, this.releaseVersions, this.customFields, featureFile.getName(), this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }

    public Requirement withChildren(List<Requirement> children) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }

    public void setChildren(List<Requirement> children) {
//        this.children = Collections.unmodifiableList(children);
        this.children = children;
    }

    public Requirement withParent(String parent) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, parent, this.type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }


    public Requirement withType(String type) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }

    public Requirement withDisplayName(String displayName) {
        return new Requirement(this.name, this.id, displayName, this.cardNumber, this.parent, type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }

    public Requirement withFeatureFileyName(String featureFileName) {
        return new Requirement(this.name, this.id, displayName, this.cardNumber, this.parent, type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }

    public Requirement withExample(Example example) {
        List<Example> updatedExamples = new ArrayList<>(examples);
        updatedExamples.add(example);
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative, children, updatedExamples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }

    public Requirement withExamples(List<Example> examples) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }

    public Requirement withReleaseVersions(List<String> releaseVersions) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }

    public Requirement withCustomFields(List<CustomFieldValue> customFields) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, this.path, this.narrative, children, examples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }


    public Requirement withPath(String path) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, this.parent, this.type, path, this.narrative, children, examples, releaseVersions, customFields, featureFileName, this.tags, this.scenarioTags, this.containsNoScenarios, this.background);
    }

    public boolean hasChildren() {
        return (children != null) && (!children.isEmpty());
    }

    public List<Requirement> getNestedChildren() {
        List<Requirement> nestedChildren = new ArrayList<>();
        for (Requirement child : children) {
            nestedChildren.add(child);
            nestedChildren.addAll(child.getNestedChildren());
        }
        return NewList.copyOf(nestedChildren);
    }

    public List<TestTag> getTags() {
        return new ArrayList<>(tags);
    }

    public Map<String, Collection<TestTag>> getScenarioTags() {
        return scenarioTags;
    }

    public TestTag asTag() {
        return TestTag.withName(qualifiedName()).andType(getType()).withDisplayName(displayName);
    }

    public TestTag asUnqualifiedTag() {
        return TestTag.withName(getName()).andType(getType()).withDisplayName(displayName);
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
        boolean namesMatch = (name != null ? name.equals(that.name) : that.name == null);
        boolean typesMatch = (type != null ? type.equals(that.type) : that.type == null);
        boolean cardNumberMatches = (StringUtils.isNotEmpty(cardNumber) ? cardNumber.equals(that.cardNumber) : that.cardNumber == null);
        boolean parentsMatch = (parent != null) ? this.parent.equals(that.parent) : that.parent == null;

        return namesMatch && typesMatch && cardNumberMatches && parentsMatch;
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
        return new Requirement(name, id, displayName, cardNumber, parent, type, narrative, newChildren, examples, releaseVersions).withTags(this.tags);
    }

    public CustomFieldSetter withCustomField(String fieldName) {
        return new CustomFieldSetter(this, fieldName);
    }

    public List<CustomFieldValue> getCustomFieldValues() {
        return NewList.copyOf(customFields);
    }

    public Optional<CustomFieldValue> getCustomField(String fieldName) {
        for (CustomFieldValue field : customFields) {
            if (field.getName().equals(fieldName)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    public List<String> getCustomFields() {
        List<String> customFieldNames = new ArrayList<>();
        for (CustomFieldValue field : customFields) {
            customFieldNames.add(field.getName());
        }
        return customFieldNames;
    }

    public String getFeatureFileName() {
        return featureFileName;
    }

    public String qualifiedName() {
        return (StringUtils.isNotEmpty(qualifier())) ? qualifier() + "/" + getName() : getName();
    }

    public String qualifier() {
        return (getParent() != null) ? getParent() : null;
    }

    public boolean matchesTag(TestTag testTag) {
        return (getTags().contains(testTag) || asTag().isAsOrMoreSpecificThan(testTag));
    }

    public Requirement merge(Requirement newRequirement) {

        String mergedCardNumber = isEmpty(cardNumber) ? newRequirement.cardNumber : cardNumber;
        String mergedDisplayName = (isEmpty(displayName) || name.equalsIgnoreCase(displayName)) ? newRequirement.displayName : displayName;
        String mergedNarrativeText = isEmpty(narrative.getText()) ? newRequirement.narrative.getText() : narrative.getText();
        String mergedPath = isEmpty(path) ? newRequirement.path : path;
        String mergedFeatureFileName = isEmpty(featureFileName) ? newRequirement.featureFileName : featureFileName;
        List<String> mergedReleasVersions = ListUtils.union(releaseVersions, newRequirement.releaseVersions);
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
                .withChildren(mergedChildren)
                .withTags(tags)
                .withBackground(background);
    }

    private List<Requirement> mergeRequirementLists(List<Requirement> existingChilden, List<Requirement> newChildren) {
        List<Requirement> mergedChildren = new ArrayList<>(existingChilden);
        for (Requirement newChild : newChildren) {
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
                children, examples, releaseVersions, customFields, featureFileName, tags, this.scenarioTags, containsNoScenarios, this.background);
    }

    public String getPath() {
        return path;
    }

    public Requirement withTags(List<TestTag> tags) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, parent, this.type, this.path, this.narrative,
                children, examples, releaseVersions, customFields, featureFileName, tags, this.scenarioTags, containsNoScenarios, this.background);
    }

    public Requirement withScenarioTags(Map<String, Collection<TestTag>> scenarioTags) {
        if(!tags.isEmpty() && this.scenarioTags.isEmpty()) {
            List<TestTag> testTags = tags.stream().filter(testTag -> DEFAULT_TAG_TYPE.equals(testTag.getType())).collect(Collectors.toList());
            for (Collection<TestTag> currentScenarioTag : scenarioTags.values()) {
                currentScenarioTag.addAll(testTags);
            }
        }
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, parent, this.type, this.path, this.narrative,
                children, examples, releaseVersions, customFields, featureFileName, this.tags, scenarioTags, containsNoScenarios, this.background);
    }

    public boolean hasTag(TestTag tag) {
        return getTags().contains(tag);
    }

    public boolean containsNoScenarios() {
        return containsNoScenarios;
    }

    public Requirement withBackground(FeatureBackgroundNarrative background) {
        return new Requirement(this.name, this.id, this.displayName, this.cardNumber, parent, this.type, this.path, this.narrative,
                children, examples, releaseVersions, customFields, featureFileName, this.tags, scenarioTags, containsNoScenarios, background);
    }

    public FeatureBackgroundNarrative getBackground() {
        return background;
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
                    customFields).withTags(requirement.tags);
        }

        public Requirement setTo(String value) {
            return setTo(value, null);
        }
    }

    public String getOrder() {
        return ((path != null) ? path : "") + (featureFileName != null ? featureFileName : "") + getDisplayName();
    }

    public Stream<Requirement> stream() {
        return Stream.concat(Stream.of(this), getChildren().stream().flatMap(Requirement::stream));
    }

    public Collection<TestTag> tagsOfType(List<String> tagTypes) {
        return getTags()
                .stream()
                .filter(tag -> tagTypes.contains(tag.getType()))
                .collect(Collectors.toList());
    }
}
