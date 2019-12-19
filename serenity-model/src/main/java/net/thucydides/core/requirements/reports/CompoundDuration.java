package net.thucydides.core.requirements.reports;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Map;
import java.util.TreeMap;

public class CompoundDuration {

    private static final String MILLISECONDS_FORMAT = "SSS'ms'";
    private static final String SECONDS_FORMAT = "s's' SSS'ms'";
    private static final String MINUTES_FORMAT = "m'm' s's' SSS'ms'";
    private static final String HOURS_FORMAT = "H'h' m'm' s's' SSS'ms'";
    private static final String DAYS_FORMAT = "d'd' H'h' m'm' s's' SSS'ms'";

    private static final Map<Long, String> FORMATS_PER_DURATION_THRESHOLD = new TreeMap<>();
    static {
        FORMATS_PER_DURATION_THRESHOLD.put(1000L, MILLISECONDS_FORMAT);
        FORMATS_PER_DURATION_THRESHOLD.put(60L * 1000, SECONDS_FORMAT);
        FORMATS_PER_DURATION_THRESHOLD.put(60L * 60 * 1000, MINUTES_FORMAT);
        FORMATS_PER_DURATION_THRESHOLD.put(24L * 60 * 60 * 1000, HOURS_FORMAT);
    }
    public static String of(long durationInMilliseconds) {
        String format =
                FORMATS_PER_DURATION_THRESHOLD.entrySet()
                .stream()
                .filter(entry -> durationInMilliseconds < entry.getKey())
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse(DAYS_FORMAT);

        return trimZeroValuesFrom(DurationFormatUtils.formatDuration(durationInMilliseconds,format));
    }

    private static String trimZeroValuesFrom(String formatDuration) {
        return formatDuration.replaceAll(" 000ms$", "")
                .replaceAll(" 0s$", "")
                .replaceAll(" 0m$", "")
                .replaceAll(" 0h$", "")
                .replaceAll(" 0m 0s$", "")
                .replaceAll(" 0h 0m 0s$", "");
    }
}