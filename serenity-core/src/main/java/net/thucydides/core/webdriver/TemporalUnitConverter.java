package net.thucydides.core.webdriver;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TemporalUnitConverter {

    private static final Map<TimeUnit, TemporalUnit> TIME_TO_TEMPORAL = new HashMap<>();
    static {
        TIME_TO_TEMPORAL.put(TimeUnit.MILLISECONDS, ChronoUnit.MILLIS);
        TIME_TO_TEMPORAL.put(TimeUnit.SECONDS, ChronoUnit.SECONDS);
        TIME_TO_TEMPORAL.put(TimeUnit.HOURS, ChronoUnit.HOURS);
        TIME_TO_TEMPORAL.put(TimeUnit.MINUTES, ChronoUnit.MINUTES);
        TIME_TO_TEMPORAL.put(TimeUnit.DAYS, ChronoUnit.DAYS);
        TIME_TO_TEMPORAL.put(TimeUnit.MICROSECONDS, ChronoUnit.MICROS);
        TIME_TO_TEMPORAL.put(TimeUnit.NANOSECONDS, ChronoUnit.NANOS);
    }

    public static TemporalUnit fromTimeUnit(TimeUnit timeUnit) {
        return TIME_TO_TEMPORAL.get(timeUnit);
    }

}
