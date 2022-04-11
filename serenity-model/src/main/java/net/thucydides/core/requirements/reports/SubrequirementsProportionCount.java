package net.thucydides.core.requirements.reports;

public class SubrequirementsProportionCount {

    private final SubrequirementsCount subrequirementsCount;

    public SubrequirementsProportionCount(SubrequirementsCount subrequirementsCount) {
        this.subrequirementsCount = subrequirementsCount;
    }

    public double withResult(String resultValue) {
        long totalNumberOfRequirements = subrequirementsCount.getTotal();
        long requirementsWithExpectedResult = subrequirementsCount.withResult(resultValue);

        if (totalNumberOfRequirements == 0)  { return 0; }

        return ((double) requirementsWithExpectedResult) / ((double) totalNumberOfRequirements);
    }

    public double withSkippedOrIgnored() {
        long totalNumberOfRequirements = subrequirementsCount.getTotal();
        long requirementsWithExpectedResult = subrequirementsCount.withResult("skipped") + subrequirementsCount.withResult("ignored");

        if (totalNumberOfRequirements == 0)  { return 0; }

        return ((double) requirementsWithExpectedResult) / ((double) totalNumberOfRequirements);
    }

    public double withNoTests() {
        long totalNumberOfRequirements = subrequirementsCount.getTotal();
        long requirementsWithNoTests = subrequirementsCount.withNoTests();

        if (totalNumberOfRequirements == 0)  { return 0; }

        return ((double) requirementsWithNoTests) / ((double) totalNumberOfRequirements);
    }

}
