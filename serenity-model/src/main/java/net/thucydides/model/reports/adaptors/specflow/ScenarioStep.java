package net.thucydides.model.reports.adaptors.specflow;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.domain.TestResult;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class ScenarioStep {

    private static final String SUCCESS_LEAD = "-> done: ";
    private static final String PENDING_LEAD = "-> No matching step definition";
    private static final String FAILURE_LEAD = "-> error: ";
    private static final String SKIPPED_LEAD = "-> skipped";
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final String title;
    private final TestResult result;
    private final Optional<? extends Throwable> exception;
    private final Optional<BigDecimal> duration;

    public ScenarioStep(List<String> resultLines) {
        title = resultLines.get(0).trim();
        duration = parseDuration(tail(resultLines));
        result = parseResult(tail(resultLines));
        exception = parseException(tail(resultLines));
    }

    private Optional<BigDecimal> parseDuration(List<String> resultLines) {
        String resultLine = resultLines.get(0);
        if (containsDurationValue(resultLine)){
            String durationText = resultLine.substring(resultLine.lastIndexOf("(") + 1);
            durationText = durationText.substring(0,durationText.lastIndexOf("s)"));
            BigDecimal durationValue = new BigDecimal(durationText).multiply(new BigDecimal("1000"))
                                                                   .round(new MathContext(0, RoundingMode.HALF_UP));
            return Optional.of(durationValue);
        }
        return Optional.empty();
    }

    private boolean containsDurationValue(String resultLine) {
        return ((resultLine.lastIndexOf("(") > 0) && (resultLine.lastIndexOf("s)") > 0));
    }

    private TestResult parseResult(List<String> resultLines) {
        String firstLine = resultLines.get(0).trim();
        if (firstLine.startsWith(SUCCESS_LEAD)) {
            return TestResult.SUCCESS;
        } else if (firstLine.startsWith(PENDING_LEAD)) {
            return TestResult.PENDING;
        } else if (firstLine.startsWith(FAILURE_LEAD)) {
            return TestResult.FAILURE;
        } else  if (firstLine.startsWith(SKIPPED_LEAD)) {
            return TestResult.SKIPPED;
        }
        return TestResult.UNDEFINED;
    }

    private Optional<? extends Throwable> parseException(List<String> resultLines) {
        String firstLine = resultLines.get(0).trim();
        if (firstLine.startsWith(FAILURE_LEAD)) {
            StringBuilder errorMessage = new StringBuilder(firstLine.substring(FAILURE_LEAD.length()));
            for(int i = 1; i < resultLines.size(); i++) {
                errorMessage.append(NEW_LINE);
                errorMessage.append(resultLines.get(i));
            }
            return Optional.of(new AssertionError(errorMessage.toString()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<BigDecimal> getDuration() {
        return duration;
    }

    public TestResult getResult() {
        return result;
    }

    public String getTitle() {
        return title;
    }

    public Optional<? extends Throwable> getException() {
        return exception;
    }

    private List<String> tail(List<String> lines) {
        return NewList.copyOf(lines.subList(1, lines.size()));
    }


}
