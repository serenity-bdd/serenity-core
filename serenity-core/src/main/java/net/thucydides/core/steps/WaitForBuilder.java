package net.thucydides.core.steps;

import net.serenitybdd.model.time.SystemClock;

public class WaitForBuilder<T> {
        private final int duration;
        private final T parent;
        private final SystemClock clock;

        private static final int MILLISECONDS_IN_A_SECOND = 1000;
        private static final int MILLISECONDS_IN_A_MINUTE = 1000 * 60;
        private static final int MILLISECONDS_IN_AN_HOUR = 1000 * 60 * 60;

        public WaitForBuilder(int duration, T parent, SystemClock clock) {
            this.clock = clock;
            this.duration = duration; 
            this.parent = parent;
        }

        public T millisecond() {
            clock.pauseFor(duration);
            return parent;
        }

        public T milliseconds() {
            clock.pauseFor(duration);
            return parent;
        }

        public T second() {
            clock.pauseFor((long) duration * MILLISECONDS_IN_A_SECOND);
            return parent;
        }

        public T seconds() {
            clock.pauseFor((long) duration * MILLISECONDS_IN_A_SECOND);
            return parent;
        }

        public T minute() {
            clock.pauseFor((long) duration * MILLISECONDS_IN_A_MINUTE);
            return parent;
        }

        public T minutes() {
            clock.pauseFor((long) duration * MILLISECONDS_IN_A_MINUTE);
            return parent;
        }

        public T hour() {
            clock.pauseFor((long) duration * MILLISECONDS_IN_AN_HOUR);
            return parent;
        }

        public T hours() {
            clock.pauseFor((long) duration * MILLISECONDS_IN_AN_HOUR);
            return parent;
        }
    }
