package net.serenitybdd.screenplay
import org.hamcrest.Matchers
import spock.lang.Specification
import spock.lang.Unroll

import static net.serenitybdd.screenplay.EventualConsequence.eventually
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.lessThan

class WhenConsequenceOutcomesAreReported extends Specification {

    @Unroll
    def "consequences should be rendered in readable form for '#consequence'"() {
        expect:
        consequence.toString() == renderedConsequence
        where:
        consequence                                        | renderedConsequence
        seeThat(TheCost.of(10), equalTo(10))               | "Then the cost should be <10>"
        seeThat(TheCost.of(10), Matchers.is(lessThan(11))) | "Then the cost should be a value less than <11>"
        eventually(seeThat(TheCost.of(10), equalTo(10)))   | "Then the cost should be <10>"
    }

}
