package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestTag;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
}
