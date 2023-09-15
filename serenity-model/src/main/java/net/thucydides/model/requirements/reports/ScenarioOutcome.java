package net.thucydides.model.requirements.reports;

import net.thucydides.model.domain.ExternalLink;
import net.thucydides.model.domain.Rule;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.TestTag;
import net.thucydides.model.reports.html.TagFilter;

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

    default List<ExampleOutcome> getExampleOutcomes() { return new ArrayList<>(); }

    boolean hasExamples();

    String getNumberOfExamples();

    String getScenarioReport();

    List<String> getScenarioReportBadges();

    Integer getStepCount();

    ZonedDateTime getStartTime();

    Long getTimestamp();

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

    String getContext();
}
