package net.thucydides.core.requirements.reports;

import net.thucydides.core.model.BadgeBackground;
import net.thucydides.core.model.ContextIcon;
import net.thucydides.core.model.TestOutcome;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportBadges {

    private final static String DETAILS_BADGE = "<a href='%s' class='badge more-details badge-%s'>%s</a>";
    private final static String DETAILS_WITH_CONTEXT_BADGE = "<a href='%s'class='badge more-details' style='background-color:%s;'>%s %s</a>";

    public static List<String> forReport(String report) {
        return Collections.singletonList(String.format(DETAILS_BADGE, report, "Test Results"));
    }

    public static List<String> from(TestOutcome outcome) {
        return from(Collections.singletonList(outcome), outcome.getName());
    }

    public static List<String> from(List<TestOutcome> outcomes, String scenarioName) {


        if (outcomes.size() == 1) {
            return Collections.singletonList(String.format(DETAILS_BADGE,
                                                           outcomes.get(0).getHtmlReport(),
                                                           outcomes.get(0).getResult().name().toLowerCase(),
                                                           "Test Results"));
        }

        return outcomes.stream()
                .filter( outcome -> outcome.getName().equalsIgnoreCase(scenarioName))
                .map(ReportBadges::outcomeBadgeFor)
                .collect(Collectors.toList());
    }

    private static String outcomeBadgeFor(TestOutcome outcome) {
        String contextIcon = ContextIcon.forOutcome(outcome);

        return String.format(DETAILS_WITH_CONTEXT_BADGE,
                outcome.getHtmlReport(),
                BadgeBackground.forOutcome(outcome),
                contextIcon,
                "Test Results");
    }

}
