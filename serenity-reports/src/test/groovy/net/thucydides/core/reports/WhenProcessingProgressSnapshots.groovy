package net.thucydides.core.reports

import net.thucydides.model.reports.history.ProgressSnapshot
import org.joda.time.DateTime
import spock.lang.Specification

class WhenProcessingProgressSnapshots extends Specification {

    def "should format date for javascript graphs"() {
        when:
            DateTime time = new DateTime(2012,06,21,10,15,02);
        ProgressSnapshot snapshot = ProgressSnapshot.forRequirementType("capability")
                                                        .atTime(time)
                                                        .with(5).completed()
                                                        .and(1).failed()
                                                        .and(2).estimated()
                                                        .outOf(10)
                                                        .forBuild("MANUAL");
        then:
            snapshot.buildId == "MANUAL"
            snapshot.requirementType == "capability"
            snapshot.total == 10
            snapshot.completed == 5
            snapshot.estimated == 2
            snapshot.failed == 1
            snapshot.formattedTime == "2012/06/21 10:15:02"
    }

    def "snapshots should be ordered by date"() {
        when:
            DateTime earlier = new DateTime(2012,06,21,10,15,02);
            DateTime later = new DateTime(2012,06,22,10,15,02);
            ProgressSnapshot earlierSnapshot = ProgressSnapshot.forRequirementType("capability")
                    .atTime(earlier)
                    .with(5).completed()
                    .and(1).failed()
                    .outOf(10)
                    .forBuild("MANUAL");

        ProgressSnapshot laterSnapshot = ProgressSnapshot.forRequirementType("capability")
                .atTime(later)
                .with(5).completed()
                .and(1).failed()
                .outOf(10)
                .forBuild("MANUAL");

        then:
            earlierSnapshot < laterSnapshot
            laterSnapshot > earlierSnapshot
    }

}
