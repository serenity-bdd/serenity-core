package net.thucydides.model.domain.features;

import com.google.common.base.Preconditions;
import net.thucydides.model.util.NameConverter;

/**
 * A feature represents a higher-level functionality that is illustrated by several user stories.
 * This class is used to represent a feature in the test outcomes and reports.
 * This class refers to an underlying class, the featureClass, which refers to the class used by the
 * API user to define the feature and the nested user stories, e.g.
 * <pre>
 *     <code>
 *         &#064;Feature
 *         public class MyFeature {
 *             public class MyUserStory1() {}
 *             public class MyUserStory2() {}
 *         }
 *     </code>
 * </pre>
 * These classes are then used in the test cases and easyb stories to refer to the tested user stories, e.g.
 * <pre>
 *     <code>
 *         &#064;Test
 *         &#064;TestsStory(MyUserStory1.class)
 *         public void should_do_this() {...}
 *     </code>
 * </pre>
 */
public class ApplicationFeature {


    private final String id;
    private final String name;


    public ApplicationFeature(final String id, final String name) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(name);
        this.id = id;
        this.name = name;
    }

    protected ApplicationFeature(final Class<?> featureClass) {
        Preconditions.checkNotNull(featureClass);
        this.id = featureClass.getCanonicalName();
        this.name = NameConverter.humanize(featureClass.getSimpleName());// AnnotatedStoryTitle.forClass(featureClass).orElse(NameConverter.humanize(featureClass.getSimpleName()));
    }

    public String getName() {
        return name;
    }

    /**
     * Obtain an application feature instance from a given feature class.
     * Feature instances are used for recording and reporting test results.
     */
    public static ApplicationFeature from(final Class<?> featureClass) {
        return new ApplicationFeature(featureClass);
    }

    public String getId() {
        return id;
    }

    private boolean idAndNameAreEqual(final ApplicationFeature that) {
        if (featureIdIsDifferent(that)) {
            return false;
        }
        if (featureNameIsDifferent(that)) {
            return false;
        }
        return true;
    }

    private boolean featureIdIsDifferent(final ApplicationFeature that) {
        return !getId().equals(that.id);
    }

    private boolean featureNameIsDifferent(final ApplicationFeature that) {
        return !getName().equals(that.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationFeature)) return false;

        ApplicationFeature that = (ApplicationFeature) o;

        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
