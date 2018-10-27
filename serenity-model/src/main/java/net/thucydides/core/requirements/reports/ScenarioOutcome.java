package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.TestResult;

import java.time.ZonedDateTime;
import java.util.List;

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

    Integer getStepCount();

    ZonedDateTime getStartTime();

    Long getDuration();

    boolean isManual();

    String getFormattedStartTime();

    String getFormattedDuration();

    String getParentName();

    String getParentReport();
}
