package net.serenitybdd.screenplay.playwright.interactions;

import com.microsoft.playwright.Clock;
import com.microsoft.playwright.Page;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Control the browser's clock for testing time-dependent features.
 *
 * <p>This interaction uses Playwright's Clock API to manipulate Date, setTimeout,
 * setInterval, and other time-related functions in the browser.</p>
 *
 * <p>Sample usage:</p>
 * <pre>
 *     // Install fake timers and freeze at a specific time
 *     actor.attemptsTo(ControlClock.freezeAt(LocalDateTime.of(2024, 12, 25, 10, 30)));
 *
 *     // Install fake timers at specific time (allows time to pass)
 *     actor.attemptsTo(ControlClock.installAt(LocalDateTime.of(2024, 1, 1, 0, 0)));
 *
 *     // Advance time by duration
 *     actor.attemptsTo(ControlClock.fastForward(5000));  // 5 seconds
 *     actor.attemptsTo(ControlClock.fastForwardMinutes(30));
 *     actor.attemptsTo(ControlClock.fastForwardHours(2));
 *
 *     // Pause and resume
 *     actor.attemptsTo(ControlClock.pause());
 *     actor.attemptsTo(ControlClock.resume());
 *
 *     // Set system time (without installing fake timers)
 *     actor.attemptsTo(ControlClock.setSystemTimeTo(LocalDateTime.of(2024, 6, 15, 14, 0)));
 * </pre>
 *
 * @see <a href="https://playwright.dev/java/docs/clock">Playwright Clock API</a>
 */
public class ControlClock {

    private ControlClock() {
        // Factory class - prevent instantiation
    }

    /**
     * Install fake timers and freeze time at the specified instant.
     * Time will not advance until explicitly moved forward.
     *
     * @param dateTime The time to freeze at
     */
    public static Performable freezeAt(LocalDateTime dateTime) {
        return new FreezeClockAt(dateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    /**
     * Install fake timers and freeze time at the specified timestamp.
     *
     * @param epochMillis The Unix timestamp in milliseconds
     */
    public static Performable freezeAt(long epochMillis) {
        return new FreezeClockAt(epochMillis);
    }

    /**
     * Install fake timers and freeze time at the specified date.
     *
     * @param date The date to freeze at
     */
    public static Performable freezeAt(Date date) {
        return new FreezeClockAt(date.getTime());
    }

    /**
     * Install fake timers starting at the specified time.
     * Time will advance normally from this point.
     *
     * @param dateTime The initial time
     */
    public static Performable installAt(LocalDateTime dateTime) {
        return new InstallClockAt(dateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    /**
     * Install fake timers starting at the specified timestamp.
     *
     * @param epochMillis The Unix timestamp in milliseconds
     */
    public static Performable installAt(long epochMillis) {
        return new InstallClockAt(epochMillis);
    }

    /**
     * Advance time by the specified number of milliseconds.
     *
     * @param milliseconds Time to advance in milliseconds
     */
    public static Performable fastForward(long milliseconds) {
        return new FastForwardClock(milliseconds);
    }

    /**
     * Advance time by the specified number of seconds.
     *
     * @param seconds Time to advance in seconds
     */
    public static Performable fastForwardSeconds(long seconds) {
        return new FastForwardClock(seconds * 1000);
    }

    /**
     * Advance time by the specified number of minutes.
     *
     * @param minutes Time to advance in minutes
     */
    public static Performable fastForwardMinutes(long minutes) {
        return new FastForwardClock(minutes * 60 * 1000);
    }

    /**
     * Advance time by the specified number of hours.
     *
     * @param hours Time to advance in hours
     */
    public static Performable fastForwardHours(long hours) {
        return new FastForwardClock(hours * 60 * 60 * 1000);
    }

    /**
     * Pause the clock. Time will not advance until resumed.
     */
    public static Performable pause() {
        return new PauseClock();
    }

    /**
     * Resume the clock after pausing.
     */
    public static Performable resume() {
        return new ResumeClock();
    }

    /**
     * Set the system time without installing fake timers.
     * This only affects Date.now() and new Date(), not setTimeout/setInterval.
     *
     * @param dateTime The time to set
     */
    public static Performable setSystemTimeTo(LocalDateTime dateTime) {
        return new SetSystemTime(dateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    /**
     * Set the system time without installing fake timers.
     *
     * @param epochMillis The Unix timestamp in milliseconds
     */
    public static Performable setSystemTimeTo(long epochMillis) {
        return new SetSystemTime(epochMillis);
    }
}

class FreezeClockAt implements Performable {
    private final long epochMillis;

    FreezeClockAt(long epochMillis) {
        this.epochMillis = epochMillis;
    }

    @Override
    @Step("{0} freezes clock at {1}")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.clock().setFixedTime(epochMillis);
    }
}

class InstallClockAt implements Performable {
    private final long epochMillis;

    InstallClockAt(long epochMillis) {
        this.epochMillis = epochMillis;
    }

    @Override
    @Step("{0} installs clock at {1}")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.clock().install(new Clock.InstallOptions().setTime(epochMillis));
    }
}

class FastForwardClock implements Performable {
    private final long milliseconds;

    FastForwardClock(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    @Step("{0} advances clock by {1}ms")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.clock().fastForward(milliseconds);
    }
}

class PauseClock implements Performable {
    @Override
    @Step("{0} pauses the clock")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.clock().pauseAt(System.currentTimeMillis());
    }
}

class ResumeClock implements Performable {
    @Override
    @Step("{0} resumes the clock")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.clock().resume();
    }
}

class SetSystemTime implements Performable {
    private final long epochMillis;

    SetSystemTime(long epochMillis) {
        this.epochMillis = epochMillis;
    }

    @Override
    @Step("{0} sets system time to {1}")
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.clock().setSystemTime(epochMillis);
    }
}
