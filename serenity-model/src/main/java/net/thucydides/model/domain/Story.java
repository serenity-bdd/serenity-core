package net.thucydides.model.domain;

import com.google.common.base.Splitter;
import net.serenitybdd.annotations.AnnotatedDescription;
import net.serenitybdd.annotations.Feature;
import net.serenitybdd.model.strings.FirstLine;
import net.thucydides.model.ThucydidesSystemProperty;
import net.serenitybdd.annotations.Epic;
import net.thucydides.model.domain.features.ApplicationFeature;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.reports.html.ReportNameProvider;
import net.thucydides.model.requirements.model.FeatureType;
import net.thucydides.model.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Represents a given user story or feature.
 * Used to record test results and so on.
 */
public class Story {

    private String id;
    private String storyName;
    private String displayName;
    private String storyClassName;
    private String path;
    private PathElements pathElements;
    private String parentName;
    private String narrative;
    private ApplicationFeature feature;
    private String type;

    protected Story(final Class<?> userStoryClass) {
        this.id = userStoryClass.getCanonicalName();
        this.storyClassName = userStoryClass.getName();
//        this.storyName = humanize(userStoryClass.getSimpleName());
        this.storyName = userStoryClass.getSimpleName();
//        this.displayName = AnnotatedStoryTitle.forClass(userStoryClass).orElse(humanize(userStoryClass.getSimpleName()));
        this.displayName = AnnotatedStoryTitle.forClass(userStoryClass).orElse(userStoryClass.getSimpleName());
        this.feature = findFeatureFrom(userStoryClass);
        this.path = pathOf(userStoryClass);
        this.narrative = AnnotatedDescription.forClass(userStoryClass).orElse("");
        // split path into a list of path elements
//        this.pathElements = pathElementsFromPackagePath(completePathOf(userStoryClass));
        this.pathElements = pathElementsFromPackagePath(path);
//        this.type = FeatureType.STORY.toString();
        this.type = FeatureType.FEATURE.toString();
    }

    private PathElements pathElementsFromPackagePath(String path) {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
        String configuredRootPath = ThucydidesSystemProperty.SERENITY_TEST_ROOT.from(environmentVariables, "");

        // The actual root path used in this path - empty if the configured root path is different from the path
        String rootPath = "";

        // strip root path from path if present and retain the actual root path used for this path.
        if (isNotEmpty(configuredRootPath) && path.startsWith(configuredRootPath)) {
            path = (path.equals(configuredRootPath)) ? "" : path.substring(configuredRootPath.length() + 1);
            rootPath = configuredRootPath;
        }

        if (path == null) {
            return PathElements.from(new ArrayList<>());
        }
        // We need to convert each element of the path excluding the root path into the display name version where necessary
        // For example: com.serenitydojo.wordle.integrationtests.api.APIExamples$WhenCreatingANewGame
        // becomes: Wordle API Specs, Creating a new game

        List<String> pathElementNames = Splitter.onPattern("\\.|\\$").omitEmptyStrings().splitToList(path);
        String cumulatedPath = "";
        List<PathElement> pathElements = new ArrayList<>();
        for (String pathElement : pathElementNames) {
            cumulatedPath = cumulatedPath + "." + pathElement;
            String completePath = rootPath + cumulatedPath;
            String displayName = displayNameOfPathElement(completePath, pathElement);
            pathElements.add(new PathElement(pathElement, displayName));
        }
        return PathElements.from(pathElements);
    }

    private String displayNameOfPathElement(String completePath, String pathElement) {
        return TestClassHierarchy.getInstance().displayNameFor(completePath).orElse(pathElement);
    }

    private PathElements pathElementsFromDirectoryPath(String path) {
        List<PathElement> pathElements = new ArrayList<>();
        if (path != null) {
            Path storyPath = null;
            try {
                storyPath = Paths.get(new URI(path));
            } catch (Throwable wellThatDidntWorkAsExpectedSoWeWillAssumeItsNotAURI) {
                storyPath = Paths.get(path);
            }
            for (int i = 0; i < storyPath.getNameCount(); i++) {
                pathElements.add(new PathElement(storyPath.getName(i).toString(), ""));
            }
        }
        return PathElements.from(pathElements);
    }

    public static String pathOf(Class<?> userStoryClass) {
        String canonicalName = userStoryClass.getCanonicalName();
        return stripRootPathFrom(canonicalName);
    }

    private static String stripRootPathFrom(String testOutcomePath) {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();

        if (testOutcomePath == null) {
            return "";
        }
        String rootPath = ThucydidesSystemProperty.SERENITY_TEST_ROOT.from(environmentVariables);
        if (StringUtils.isNotEmpty(rootPath) && testOutcomePath.startsWith(rootPath) && (!testOutcomePath.equals(rootPath))) {
            return testOutcomePath.substring(rootPath.length() + 1);
        } else {
            return testOutcomePath;
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
                 final String displayName,
                 final String path,
                 final ApplicationFeature feature) {
        this.id = id;
        this.storyName = storyName;
        this.storyClassName = storyClassName;
        this.displayName = displayName;
        this.feature = feature;
        this.path = normalisedPath(path);
        this.pathElements = pathElementsFromDirectoryPath(this.path);
        this.narrative = null;
        this.type = FeatureType.FEATURE.toString();
//        this.type = FeatureType.STORY.toString();
    }


    public Story(String id,
                 final String storyName,
                 final String storyClassName,
                 final String displayName,
                 final String path,
                 final PathElements pathElements,
                 final ApplicationFeature feature,
                 final String narrative) {
        this(id, storyName, storyClassName, displayName, path, pathElements, feature, narrative, FeatureType.FEATURE.toString());
    }


    public Story(String id,
                 final String storyName,
                 final String storyClassName,
                 final String displayName,
                 final String path,
                 final PathElements pathElements,
                 final ApplicationFeature feature,
                 final String narrative,
                 final String type) {
        this.id = id;
        this.storyName = storyName;
        this.storyClassName = storyClassName;
        this.displayName = displayName;
        this.feature = feature;
        this.path = path;
        this.pathElements = pathElements;
        this.narrative = narrative;
        this.type = type;
    }

    protected Story(final String id,
                    final String storyName,
                    final String displayName,
                    final ApplicationFeature feature,
                    final String path) {
        this.id = id;
        this.storyName = storyName;
        this.displayName = displayName;
        this.storyClassName = null;
        this.feature = feature;
        this.path = normalisedPath(path);
        this.pathElements = pathElementsFromDirectoryPath(this.path);
        this.type = FeatureType.FEATURE.toString();
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

    public Story withNarrative(String narrative) {
        return new Story(id, storyName, storyClassName, displayName, path, pathElements, feature, narrative, type);
    }

    public Story withType(String type) {
        return new Story(id, storyName, storyClassName, displayName, path, pathElements, feature, narrative, type);
    }

    public static Story withIdAndPath(final String storyId, final String storyName, final String storyPath) {
        return new Story(storyId, storyName, null, storyName, storyPath, null);
    }

    public Story withDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public Story withStoryName(String name) {
        this.storyName = name;
        return this;
    }

    public static Story called(final String storyName) {
        return new Story(storyName, storyName, null, null, null);
    }

    public static Story withId(final String storyId, final String storyName,
                               final String featureClassName, final String featureName) {
        return new Story(storyId, storyName, storyName, new ApplicationFeature(featureClassName, featureName), null);
    }


    public static Story withIdAndPathAndFeature(final String storyId, final String storyName, String storyPath,
                                                final String featureClassName, final String featureName) {
        return new Story(storyId, storyName, storyName, new ApplicationFeature(featureClassName, featureName), storyPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Story)) return false;

        Story story = (Story) o;

        if (!id.equals(story.id)) return false;
        return Objects.equals(path, story.path);
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
     * This is indicated by the Story annotation's storyClass attribute.
     */
    public static Class<?> testedInTestCase(Class<?> testClass) {
        net.serenitybdd.annotations.Story story = testClass.getAnnotation(net.serenitybdd.annotations.Story.class);
        if (story != null && story.storyClass() != void.class) {
            return story.storyClass();
        }
        return null;
    }

    /**
     * Returns the string-based story name defined on a class via {@code @Story("name")}, or null if not present.
     */
    public static String storyNameDefinedIn(Class<?> testClass) {
        net.serenitybdd.annotations.Story story = testClass.getAnnotation(net.serenitybdd.annotations.Story.class);
        if (story != null && !story.value().isEmpty()) {
            return story.value();
        }
        return null;
    }

    /**
     * Returns the string-based story name defined on a method via {@code @Story("name")}, or null if not present.
     */
    public static String storyNameDefinedIn(java.lang.reflect.Method method) {
        net.serenitybdd.annotations.Story story = method.getAnnotation(net.serenitybdd.annotations.Story.class);
        if (story != null && !story.value().isEmpty()) {
            return story.value();
        }
        return null;
    }

    /**
     * Builds a Story with a proper requirements hierarchy from string-based {@code @Epic}, {@code @Feature},
     * and {@code @Story} annotations on the given class. Walks the superclass chain so that annotations
     * on parent classes are inherited. Returns null if no string-based annotations are present.
     *
     * <p>The hierarchy is: Epic &gt; Feature &gt; Story. The path is built from whichever levels are present.</p>
     */
    public static Story fromAnnotationsOn(Class<?> testClass) {
        return fromAnnotationsOn(testClass, null);
    }

    /**
     * Builds a Story with a proper requirements hierarchy from string-based {@code @Epic}, {@code @Feature},
     * and {@code @Story} annotations on the given class. When {@code @Story} is absent but {@code @Feature}
     * or {@code @Epic} is present, uses the {@code defaultStoryName} (typically from {@code @DisplayName})
     * as the story name, nesting it under the feature/epic in the hierarchy.
     *
     * @param testClass the test class to read annotations from
     * @param defaultStoryName fallback story name when no {@code @Story} annotation is present (e.g. from {@code @DisplayName})
     * @return a Story with the requirements hierarchy, or null if no annotations are present
     */
    public static Story fromAnnotationsOn(Class<?> testClass, String defaultStoryName) {
        String storyName = null;
        String featureName = null;
        String epicName = null;

        // Walk the class hierarchy to find the first occurrence of each annotation
        Class<?> current = testClass;
        while (current != null && current != Object.class) {
            if (storyName == null) {
                net.serenitybdd.annotations.Story storyAnn = current.getAnnotation(net.serenitybdd.annotations.Story.class);
                if (storyAnn != null && !storyAnn.value().isEmpty()) {
                    storyName = storyAnn.value();
                }
            }
            if (featureName == null) {
                Feature featureAnn = current.getAnnotation(Feature.class);
                if (featureAnn != null && !featureAnn.value().isEmpty()) {
                    featureName = featureAnn.value();
                }
            }
            if (epicName == null) {
                Epic epicAnn = current.getAnnotation(Epic.class);
                if (epicAnn != null) {
                    epicName = epicAnn.value();
                }
            }
            current = current.getSuperclass();
        }

        if (storyName == null && featureName == null && epicName == null) {
            return null;
        }

        // When @Story is absent but @Feature/@Epic is present, use the default story name
        if (storyName == null && defaultStoryName != null && (featureName != null || epicName != null)) {
            storyName = defaultStoryName;
        }

        // Use the most specific annotation as the story name, falling back up the hierarchy
        String effectiveStoryName = (storyName != null) ? storyName
                : (featureName != null) ? featureName
                : epicName;

        // Build the path from the hierarchy levels that are present
        StringBuilder pathBuilder = new StringBuilder();
        if (epicName != null) {
            pathBuilder.append(epicName);
        }
        if (featureName != null) {
            if (pathBuilder.length() > 0) { pathBuilder.append("/"); }
            pathBuilder.append(featureName);
        }
        if (storyName != null) {
            if (pathBuilder.length() > 0) { pathBuilder.append("/"); }
            pathBuilder.append(storyName);
        }
        String path = pathBuilder.toString();

        // Build path elements with proper display names
        List<PathElement> elements = new ArrayList<>();
        if (epicName != null) {
            elements.add(new PathElement(epicName, epicName));
        }
        if (featureName != null) {
            elements.add(new PathElement(featureName, featureName));
        }
        if (storyName != null) {
            elements.add(new PathElement(storyName, storyName));
        }
        PathElements pathElements = PathElements.from(elements);

        // Build parent feature if @Feature is present
        ApplicationFeature feature = (featureName != null)
                ? new ApplicationFeature(featureName, featureName) : null;

        return new Story(effectiveStoryName, effectiveStoryName, null, effectiveStoryName,
                path, pathElements, feature, null, FeatureType.STORY.toString());
    }

    /**
     * Each story has a descriptive name.
     * This name is usually a human-readable version of the class name, or the story name for an easyb story.
     */
    public String getName() {
        return storyName;
    }

    public String getDisplayName() {
        return StringUtils.isNotEmpty(displayName) ? displayName : storyName;
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
        return new ReportNameProvider().forRequirement(this.asTag());
    }

    public ApplicationFeature getFeature() {
        return feature;
    }

    public String getPath() {
        if (pathElements == null || pathElements.isEmpty()) {
            return path;
        }
        return pathElements.asPath();
    }

    public String getRelativePath(List<String> rootElements) {
        if (pathElements.isEmpty()) {
            return path;
        }
        return pathElements.asPath();
    }

    public PathElements getParentPathElements() {
        return pathElements;
    }

    public PathElements getPathElements() {
        return pathElements;
    }

    public String getStoryName() {
        return storyName;
    }

    public String getNarrative() {
        return narrative;
    }

    public String getNarrativeSummary() {
        return FirstLine.of(narrative);
    }

    public String getType() {
        return type;
    }

    public Story withPath(String path) {
        this.path = path;
        this.type = FeatureType.forFilename(path).toString();
        return this;
    }

    public Story asFeature() {
        this.type = FeatureType.FEATURE.toString();
        return this;
    }

    public TestTag asTag() {
        return asQualifiedTag();// TestTag.withName(storyName).andType(type.toString());
    }

    //    private TestTag qualifiedTag;
    public TestTag asQualifiedTag() {
        if (getPathElements() == null) {
            return TestTag.withName(storyName).andType(type);
        }
        String qualifiedName = getPathElements().stream()
                .map(PathElement::getName)
                .collect(Collectors.joining("/"));

        return TestTag.withName(qualifiedName).andType(type).withDisplayName(getDisplayName());
    }


    /**
     * A tag with no more than one level of parents, that can be used to find matching requirements in the requirements hierarchy.
     */
    private TestTag singleParentTag;

    public TestTag asSingleParentTag() {

        if (singleParentTag == null) {
            String lastElementOfPath = (getParentPathElements() == null || getParentPathElements().isEmpty()) ? "" : getParentPathElements().get(getParentPathElements().size() - 1).getName();
            singleParentTag = (isNotEmpty(lastElementOfPath)) ?
                    TestTag.withName(lastElementOfPath + "/" + storyName).andType(type) :
                    TestTag.withName(storyName).andType(type);
        }
        return singleParentTag;
    }

    private String normalisedPath(String path) {
        if (path == null) {
            return path;
        }
        // Strip initial reference to 'classpath:features/' or 'src/test/resources/features/' at the start of the path
        String normalisedPath = path;
        if (normalisedPath.startsWith("classpath:features/")) {
            normalisedPath = normalisedPath.substring("classpath:features/".length());
        }
        // Remove trailing feature file name if present
        if (normalisedPath.endsWith(".feature") || normalisedPath.endsWith(".story")) {
            normalisedPath = normalisedPath.substring(0, normalisedPath.lastIndexOf("."));// relativeFeaturePath(normalisedPath);
        }
        return normalisedPath;
    }
}
