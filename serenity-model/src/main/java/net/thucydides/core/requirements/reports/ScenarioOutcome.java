package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.Rule;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.reports.html.TagFilter;
import net.thucydides.core.requirements.model.Requirement;

import java.time.ZonedDateTime;
import java.util.*;

public interface ScenarioOutcome {

    String getName();

    String getSimplifiedName();

    String getTitle();

    String getId();

    String getType();

    TestResult getResult();

    String getResultStyle();

    String getDescription();

    List<String> getSteps();

    List<String> getExamples();

    boolean hasExamples();

    String getNumberOfExamples();

    String getScenarioReport();

    List<String> getScenarioReportBadges();

    Integer getStepCount();

    ZonedDateTime getStartTime();

    Long getDuration();

    Boolean isManual();

    String getFormattedStartTime();

    String getFormattedDuration();

    String getParentName();

    String getParentReport();

    Set<TestTag> getTags();

    Map<String, Collection<TestTag>> getExampleTags();

    Rule getRule();

    ExternalLink getExternalLink();

    Collection<TestTag> getScenarioTags();

    default boolean isBackground() { return "background".equalsIgnoreCase(getType());};

    default List<TestTag> getFilteredTags() {
        TagFilter tagFilter = new TagFilter();
        return new ArrayList<>(tagFilter.removeHiddenTagsFrom(getTags()));
    }
}
