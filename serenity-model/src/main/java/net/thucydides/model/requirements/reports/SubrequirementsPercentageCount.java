package net.thucydides.model.requirements.reports;

import net.thucydides.model.domain.NumericalFormatter;

public class SubrequirementsPercentageCount {

    private final SubrequirementsCount subrequirementsCount;

    private final NumericalFormatter formatter = new NumericalFormatter();

    public SubrequirementsPercentageCount(SubrequirementsCount subrequirementsCount) {
        this.subrequirementsCount = subrequirementsCount;
    }

    public String withResult(String resultValue) {
        return formatter.percentage(subrequirementsCount.getProportion().withResult(resultValue),1);
    }

    public String withSkippedOrIgnored() {
        return formatter.percentage(subrequirementsCount.getProportion().withSkippedOrIgnored(), 1);
    }

    public String withNoTests() {
        return formatter.percentage(subrequirementsCount.getProportion().withNoTests(),1);
    }

}
