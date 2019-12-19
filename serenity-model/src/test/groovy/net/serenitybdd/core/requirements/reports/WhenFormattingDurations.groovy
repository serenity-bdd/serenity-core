package net.serenitybdd.core.requirements.reports


import net.thucydides.core.requirements.reports.CompoundDuration
import spock.lang.Specification
import spock.lang.Unroll

class WhenFormattingDurations extends Specification {

    @Unroll
    def "durations should be formatted in a human-readable way"() {

        expect:
        CompoundDuration.of(durationInMilliseconds) == formattedDuration
        where:
        durationInMilliseconds | formattedDuration
        250                    | "250ms"
        2000                   | "2s"
        2500                   | "2s 500ms"
        60000                  | "1m"
        62000                  | "1m 2s"
        62500                  | "1m 2s 500ms"
        3600000                | "1h"
        3660000                | "1h 1m"
        3661000                | "1h 1m 1s"
        3661100                | "1h 1m 1s 100ms"
        86400000                | "1d"
        90000000                | "1d 1h"
        90060000                | "1d 1h 1m"
        90061000                | "1d 1h 1m 1s"
        90061100                | "1d 1h 1m 1s 100ms"

    }

}
