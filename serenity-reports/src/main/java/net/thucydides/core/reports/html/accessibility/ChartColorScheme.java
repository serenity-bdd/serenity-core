package net.thucydides.core.reports.html.accessibility;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.*;
import java.util.stream.Collectors;

import static net.thucydides.core.ThucydidesSystemProperty.SERENITY_REPORT_ACCESSIBILITY;
import static net.thucydides.core.model.TestResult.*;

/**
 * We can switch between color schemes by setting the serenity.report.accessibility property to true.
 */
public class ChartColorScheme {
    private final EnvironmentVariables environmentVariables;

    private static final List<TestResult> CHART_STATUS_VALUES = Arrays.asList(
            SUCCESS, PENDING, IGNORED, SKIPPED, ABORTED, FAILURE, ERROR, COMPROMISED
    );

    private static final List<String> STANDARD_COLORS = Arrays.asList(
            "'rgba(153,204,51,0.5)'",      // PASSING
            "'rgba(165, 199, 238, 0.5)'",  // PENDING
            "'rgba(168, 168, 168, 0.5)'",  // IGNORED
            "'rgba(238, 224, 152, 0.75)'", // SKIPPED
            "'rgba(255, 153, 102, 0.5)'",  // ABORTED
            "'rgba(255, 22, 49, 0.5)'",    // FAILED
            "'rgba(255, 97, 8, 0.5)'",     // ERROR
            "'rgba(255, 104, 255, 0.5)'",   // COMPROMISED
            "'rgba(83, 50, 168, 0.5)'"   // UNDEFINED
    );
    private static final List<String> ACCESSIBLE_COLORS = Arrays.asList(
            "'rgba(153,204,51,0.5)'", // PASSING
            "pattern.draw('line', 'rgba(165, 199, 238, 0.5)')", // PENDING
            "pattern.draw('line-vertical', 'rgba(168, 168, 168, 0.5)')", // IGNORED
            "pattern.draw('diagonal', 'rgba(238, 224, 152, 0.75)')",// SKIPPED
            "pattern.draw('diagonal-right-left', 'rgba(255, 153, 102, 0.5)')", // ABORTED
            "pattern.draw('zigzag-vertical', 'rgba(255, 22, 49, 0.5)')",   // FAILED
            "pattern.draw('zigzag', 'rgba(255, 97, 8, 0.5)')",    // ERROR
            "pattern.draw('square', 'rgba(255, 104, 255, 0.5)')",  // COMPROMISED
            "pattern.draw('diamond', 'rgba(83, 50, 168, 0.5)')"  // COMPROMISED
    );
    private static final List<String> STANDARD_MANUAL_COLORS = Arrays.asList(
            "'rgba(153,204,51,0.25)'",      // PASSING
            "'rgba(165, 199, 238, 0.25)'",  // PENDING
            "'rgba(168, 168, 168, 0.25)'",  // IGNORED
            "'rgba(238, 224, 152, 0.375)'", // SKIPPED
            "'rgba(255, 153, 102, 0.25)'",  // ABORTED
            "'rgba(255, 22, 49, 0.25)'",    // FAILED
            "'rgba(255, 97, 8, 0.25)'",     // ERROR
            "'rgba(255, 104, 255, 0.25)'",  // COMPROMISED
            "'rgba(83, 50, 168, 0.25)'"   // COMPROMISED
    );
    private static final List<String> ACCESSIBLE_MANUAL_COLORS = Arrays.asList(
            "'rgba(153,204,51,0.25)'", // PASSING
            "pattern.draw('line', 'rgba(165, 199, 238, 0.25)')", // PENDING
            "pattern.draw('line-vertical', 'rgba(168, 168, 168, 0.25)')", // IGNORED
            "pattern.draw('diagonal', 'rgba(238, 224, 152, 0.375)')",// SKIPPED
            "pattern.draw('diagonal-right-left', 'rgba(255, 153, 102, 0.25)')", // ABORTED
            "pattern.draw('zigzag-vertical', 'rgba(255, 22, 49, 0.25)')",   // FAILED
            "pattern.draw('zigzag', 'rgba(255, 97, 8, 0.25)')",    // ERROR
            "pattern.draw('triangle', 'rgba(255, 104, 255, 0.25)')",  // COMPROMISED
            "pattern.draw('diamond', 'rgba(83, 50, 168, 0.25)')"  // UNDEFINED
    );

    private static final List<String> BORDER_COLORS = Arrays.asList(
            "'rgba(153,204,51,1)'",      // PASSING
            "'rgba(165, 199, 238, 1)'",  // PENDING
            "'rgba(168, 168, 168, 1)'",  // IGNORED
            "'rgba(238, 224, 152, 1)'",  // SKIPPED
            "'rgba(255, 153, 102, 1)'",  // ABORTED
            "'rgba(255, 22, 49, 1)'",  // FAILED
            "'rgba(255, 97, 8, 1)'",     // ERROR
            "'rgba(255, 104, 255, 1)'", // COMPROMISED
            "'rgba(83, 50, 168, 1)'" // UNDEFINED
    );

    private final static Map<EnvironmentVariables, ChartColorScheme> COLOR_SCHEMES = new HashMap<>();

    public ChartColorScheme(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static ChartColorScheme forEnvironment(EnvironmentVariables environmentVariables) {
        if (!COLOR_SCHEMES.containsKey(environmentVariables)) {
            COLOR_SCHEMES.put(environmentVariables, new ChartColorScheme(environmentVariables));
        }
        return COLOR_SCHEMES.get(environmentVariables);
    }

    public String backgroundColorFor(String testStatus) {
        int valueIndex = CHART_STATUS_VALUES.indexOf(TestResult.valueOf(testStatus));
        if (isInAccessibleMode()) {
            return ACCESSIBLE_COLORS.get(valueIndex);
        } else {
            return STANDARD_COLORS.get(valueIndex);
        }
    }

    public String getBackgroundColors() {
        if (isInAccessibleMode()) {
            return formatted(ACCESSIBLE_COLORS);
        } else {
            return formatted(STANDARD_COLORS);
        }
    }

    public String gradientColors(RGB start, RGB end, int steps) {

        RGB diff = start.minus(end);
        double alphaInc = diff.getAlpha() / (steps * 1.0);
        int redInc = (int) Math.round(diff.getRed() / (steps * 1.0));
        int greenInc = (int) Math.round(diff.getGreen() / (steps * 1.0));
        int blueInc = (int) Math.round(diff.getBlue() / (steps * 1.0));

        RGB colorStep = new RGB(redInc, greenInc, blueInc, alphaInc);

        List<RGB> gradientColors = new ArrayList<>();
        RGB color = start;
        for(int i = 0; i < steps; i++) {
            gradientColors.add(color);
            color = color.minus(colorStep);
        }
        return formatted( gradientColors.stream().map( rgb -> "'" + rgb.toString() + "'").collect(Collectors.toList()));
    }

    public String getManualBackgroundColors() {
        if (isInAccessibleMode()) {
            return formatted(ACCESSIBLE_MANUAL_COLORS);
        } else {
            return formatted(STANDARD_MANUAL_COLORS);
        }
    }

    public String getBorderColors() {
        return formatted(BORDER_COLORS);
    }

    private String formatted(List<String> colors) {
        return "[" + colors.stream().collect(Collectors.joining(",")) + "]";
    }

    private boolean isInAccessibleMode() {
        return Boolean.parseBoolean(
                EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(SERENITY_REPORT_ACCESSIBILITY).orElse("false")
        );
    }
}
