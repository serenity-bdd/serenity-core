package net.thucydides.core.reports.html;

import net.thucydides.core.model.ReportData;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;

import java.util.ArrayList;
import java.util.List;

public class EvidenceData {

    private final String scenario;
    private final String title;
    private final String detailsLink;

    public EvidenceData(String scenario, String title, String detailsLink) {
        this.scenario = scenario;
        this.title = title;
        this.detailsLink = detailsLink;
    }


    public String getScenario() {
        return scenario;
    }

    public String getTitle() {
        return title;
    }

    public String getDetailsLink() {
        return detailsLink;
    }

    public static List<EvidenceData> from(List<? extends TestOutcome> testOutcomes) {

        List<EvidenceData> evidence = new ArrayList<>();

        for(TestOutcome testOutcome : testOutcomes) {
            for(ReportData reportData : testOutcome.getEvidence()) {
                evidence.add(new EvidenceData(testOutcome.getQualified().withContext().getTitle(),
                                              reportData.getTitle(),
                                              detailsLinkFor(testOutcome, reportData)));
            }
        }
        return evidence;
    }

    private static String detailsLinkFor(TestOutcome testOutcome, ReportData reportData) {
        if (reportData.getPath() != null) {
            return "<a role='button' target='_blank' class='btn btn-success btn-sm' href='"
                    + reportData.getPath()
                    + "'><i class='fas fa-download'></i>&nbsp;Download Evidence</a>";
        } else {
            String report = ReportNamer.forReportType(ReportType.HTML).getNormalizedReportNameFor(testOutcome);
            return "<a role='button' class='btn btn-success btn-sm' href='"
                    + report + "#" + reportData.getId()
                    + "'><i class=\"fas fa-external-link-alt\"></i>&nbsp;Details</a>";
        }
    }


}
