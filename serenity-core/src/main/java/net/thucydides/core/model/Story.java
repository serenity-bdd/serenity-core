package net.thucydides.core.model;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.util.NameConverter;

import static net.thucydides.core.model.ReportType.ROOT;

/**
 * Represents a given user story or feature.
 * Used to record test results and so on.
 */
public class Story {

    private  String id;
    private  String storyName;
    private  String storyClassName;
    private  String path;
    private  String narrative;
    private  ApplicationFeature feature;
    private  RequirementType type;

    public enum RequirementType {
        story, feature
    }

    protected Story(final Class<?> userStoryClass) {
        this.id = userStoryClass.getCanonicalName();
        this.storyClassName = userStoryClass.getName();
        this.storyName = NameConverter.humanize(userStoryClass.getSimpleName());
        this.feature = findFeatureFrom(userStoryClass);
        this.path = pathOf(userStoryClass);
        this.type = RequirementType.story;
    }

    private String pathOf(Class<?> userStoryClass) {
        String canonicalName = userStoryClass.getCanonicalName();
        int lastDot = canonicalName.lastIndexOf(".");
        if (lastDot > 0) {
            return canonicalName.substring(0, lastDot);
        } else {
            return "";
        }
    }

    private ApplicationFeature findFeatureFrom(Class<?> userStoryClass) {
        if (getFeatureClass(userStoryClass) != null) {
            return ApplicationFeature.from(getFeatureClass(userStoryClass));
        }
        return null;
    }

    public Story(String id,
                 final String storyName,
                 final String storyClassName,
                 final String path,
                 final ApplicationFeature feature) {
        this.id = id;
        this.storyName = storyName;
        this.storyClassName = storyClassName;
        this.feature = feature;
        this.path = path;
        this.narrative = null;
        this.type = RequirementType.story;
    }


    public Story(String id,
                 final String storyName,
                 final String storyClassName,
                 final String path,
                 final ApplicationFeature feature,
                 final String narrative) {
        this(id, storyName,storyClassName, path, feature, narrative, RequirementType.story);
    }


    public Story(String id,
                 final String storyName,
                 final String storyClassName,
                 final String path,
                 final ApplicationFeature feature,
                 final String narrative,
                 final RequirementType type) {
        this.id = id;
        this.storyName = storyName;
        this.storyClassName = storyClassName;
        this.feature = feature;
        this.path = path;
        this.narrative = narrative;
        this.type = type;
    }

    protected Story(final String id,
                    final String storyName,
                    final ApplicationFeature feature,
                    final String path) {
        this.id = id;
        this.storyName = storyName;
        this.storyClassName = null;
        this.feature = feature;
        this.path = path;
        this.type = RequirementType.story;
    }



    public String getId() {
        return id;
    }

    /**
     * Obtain a story instance from a given story class.
     * Story instances are used for recording and reporting test results.
     */
    public static Story from(final Class<?> userStoryClass) {
        return new Story(userStoryClass);
    }

    /**
     * Create a story using a full class name as an id.
     * Note that the class may no longer exist, so the story needs to be able to exist beyond the life
     * of the original story class. This is used to deserialize stories from XML files.
     */
    public static Story withId(final String storyId, final String storyName) {
        return new Story(storyId, storyName, null, null, null);
    }

    public Story withNarrative(String narrative)  {
        return new Story(id, storyName, storyClassName, path, feature, narrative, type);
    }

    public static Story withIdAndPath(final String storyId, final String storyName, final String storyPath) {
        return new Story(storyId, storyName, null, storyPath, null);
    }

    public static Story called(final String storyName) {
        return new Story(storyName, storyName, null, null, null);
    }

    public static Story withId(final String storyId, final String storyName,
                               final String featureClassName, final String featureName) {
        return new Story(storyId, storyName, new ApplicationFeature(featureClassName, featureName), null);
    }


    public static Story withIdAndPathAndFeature(final String storyId, final String storyName, String storyPath,
                                                final String featureClassName, final String featureName) {
        return new Story(storyId, storyName, new ApplicationFeature(featureClassName, featureName), storyPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Story)) return false;

        Story story = (Story) o;

        if (!id.equals(story.id)) return false;
        if (path != null ? !path.equals(story.path) : story.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    /**
     * What feature does this story belong to?
     */
    public static Class<?> getFeatureClass(Class<?> userStoryClass) {
        if (userStoryClass != null) {
            Class<?> enclosingClass = userStoryClass.getEnclosingClass();
            if (isAFeature(enclosingClass)) {
                return enclosingClass;
            }
        }
        return null;
    }

    private static boolean isAFeature(Class<?> enclosingClass) {
        return (enclosingClass != null) && (enclosingClass.getAnnotation(Feature.class) != null);
    }

    /**
     * Returns the class representing the story that is tested by a given test class
     * This is indicated by the Story annotation.
     */
    public static Class<?> testedInTestCase(Class<?> testClass) {
        net.thucydides.core.annotations.Story story = testClass.getAnnotation(net.thucydides.core.annotations.Story.class);
        if (story != null) {
            return story.value();
        } else {
            return null;
        }
    }

    /**
     * Each story has a descriptive name.
     * This name is usually a human-readable version of the class name, or the story name for an easyb story.
     */
    public String getName() {
        return storyName;
    }

    public String getStoryClassName() {
        return storyClassName;
    }

    /**
     * Find the name of the report for this story for the specified report type (XML, HTML,...).
     */
    public String getReportName(final ReportType type) {
        return Stories.reportFor(this, type);
    }

    public String getReportName() {
        return getReportName(ROOT);
    }

    public ApplicationFeature getFeature() {
        return feature;
    }

    public String getPath() {
        return path;
    }

    public String getStoryName() {
        return storyName;
    }

    public String getNarrative() {
        return narrative;
    }

    public RequirementType getType() {
        return type;
    }

    public Story withPath(String storyPath) {
        return new Story(this.id, this.storyName, this.feature, storyPath);
    }

    public Story asFeature() {
        return new Story(this.id, this.storyName, this.storyClassName, this.path,this.feature,this.narrative, RequirementType.feature);
    }

    public TestTag asTag() {
        return TestTag.withName(storyName).andType(type.toString());
    }
}
