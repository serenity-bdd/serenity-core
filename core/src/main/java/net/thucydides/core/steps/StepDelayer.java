package net.thucydides.core.steps;

import net.thucydides.core.pages.SystemClock;

public class StepDelayer {

    private final SystemClock clock;

    public StepDelayer(SystemClock clock) {
        this.clock = clock;
    }

    public WaitForBuilder waitFor(int duration) {
        return new WaitForBuilder(duration);
    }

    public class WaitForBuilder {
        private final int duration;
        private static final int MILLISECONDS_IN_A_SECOND = 1000;
        private static final int MILLISECONDS_IN_A_MINUTE = 1000 * 60;
        private static final int MILLISECONDS_IN_AN_HOUR = 1000 * 60 * 60;

        public WaitForBuilder(int duration) {
            this.duration = duration;
        }

        public void millisecond() {
            clock.pauseFor(duration);
        }

        public void milliseconds() {
            clock.pauseFor(duration);
        }

        public void second() {
            clock.pauseFor(duration * MILLISECONDS_IN_A_SECOND);
        }

        public void seconds() {
            clock.pauseFor(duration * MILLISECONDS_IN_A_SECOND);
        }

        public void minute() {
            clock.pauseFor(duration * MILLISECONDS_IN_A_MINUTE);
        }

        public void minutes() {
            clock.pauseFor(duration * MILLISECONDS_IN_A_MINUTE);
        }

        public void hour() {
            clock.pauseFor(duration * MILLISECONDS_IN_AN_HOUR);
        }

        public void hours() {
            clock.pauseFor(duration * MILLISECONDS_IN_AN_HOUR);
        }
    }
}
