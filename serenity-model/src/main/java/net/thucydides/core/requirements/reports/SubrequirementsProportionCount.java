package net.thucydides.core.requirements.reports;

public class SubrequirementsProportionCount {

    private final SubrequirementsCount subrequirementsCount;

    public SubrequirementsProportionCount(SubrequirementsCount subrequirementsCount) {
        this.subrequirementsCount = subrequirementsCount;
    }

    public double withResult(String resultValue) {
        int totalNumberOfRequirements = subrequirementsCount.getTotal();
        int requirementsWithExpectedResult = subrequirementsCount.withResult(resultValue);

        if (totalNumberOfRequirements == 0)  { return 0; }

        return ((double) requirementsWithExpectedResult) / ((double) totalNumberOfRequirements);
    }

    public double withSkippedOrIgnored() {
        int totalNumberOfRequirements = subrequirementsCount.getTotal();
        int requirementsWithExpectedResult = subrequirementsCount.withResult("skipped") + subrequirementsCount.withResult("ignored");

        if (totalNumberOfRequirements == 0)  { return 0; }

        return ((double) requirementsWithExpectedResult) / ((double) totalNumberOfRequirements);
    }

    public double withNoTests() {
        int totalNumberOfRequirements = subrequirementsCount.getTotal();
        int requirementsWithNoTests = subrequirementsCount.withNoTests();

        if (totalNumberOfRequirements == 0)  { return 0; }

        return ((double) requirementsWithNoTests) / ((double) totalNumberOfRequirements);
    }

}
