package net.thucydides.model.requirements.reports;

import net.thucydides.model.reports.TestOutcomes;
import net.thucydides.model.requirements.model.Requirement;

/**
 * Created by john on 22/07/2016.
 */
public class PercentageRequirementCounter {

    private final Requirement requirement;
    private final TestOutcomes testOutcomes;

    public PercentageRequirementCounter(Requirement requirement, TestOutcomes testOutcomes) {

        this.requirement = requirement;
        this.testOutcomes = testOutcomes;
    }

    /*
    <#list requirements.types as requirementType>
    <#assign successfulRequirements= requirements.requirementsOfType(requirementType).completedRequirementsCount >
    <#assign pendingRequirements = requirements.requirementsOfType(requirementType).pendingRequirementsCount>
    <#assign ignoredRequirements = requirements.requirementsOfType(requirementType).ignoredRequirementsCount >
    <#assign failingRequirements = requirements.requirementsOfType(requirementType).failingRequirementsCount >
    <#assign errorRequirements = requirements.requirementsOfType(requirementType).errorRequirementsCount  >
    <#assign compromisedRequirements = requirements.requirementsOfType(requirementType).compromisedRequirementsCount  >
    <#assign untesteddRequirements = requirements.requirementsOfType(requirementType).requirementsWithoutTestsCount >
<#else>
    <#assign successfulRequirements= testOutcomes.totalTests.withResult("success") >
    <#assign pendingRequirements = testOutcomes.totalTests.withResult("pending") >
    <#assign ignoredRequirements = testOutcomes.totalTests.withResult("ignored") + testOutcomes.totalTests.withResult("skipped")>
    <#assign failingRequirements = testOutcomes.totalTests.withResult("failure") >
    <#assign errorRequirements = testOutcomes.totalTests.withResult("error") >
    <#assign compromisedRequirements = testOutcomes.totalTests.withResult("compromised") >
    <#assign untesteddRequirements = 0 >
</#list>

     */
    public double getWithNoTests() {

        return 0.0;
    }
}
