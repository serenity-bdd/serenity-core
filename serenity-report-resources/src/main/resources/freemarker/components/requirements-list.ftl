<#macro requirements_results(requirements, title, requirementType, id)>

<#include "feature-coverage.ftl">
<#include "test-coverage.ftl">

<#if requirements.requirementOutcomes?size gt 10>
<script>
    $(document).ready(function () {

        $('#${id}').DataTable({

            "order": [],
            "language": {
                searchPlaceholder: "Filter",
                search: ""
            }
        });
    });
</script>
</#if>
<h4 class="requireent-type-heading">${requirementType}</h4>

<table class="scenario-result table" id="${id}">
    <thead>
        <tr>
            <th class="requirement-name-column" width="60%">${title}</th>
            <th>ID</th>
            <th>Automated Tests</th>
            <#if reportOptions.showManualTests>
                <th>Manual Tests</th>
            </#if>
            <th>Results</th>
            <th>Coverage</th>
        </tr>
    </thead>
    <tbody>
    <#foreach requirementOutcome in requirements.requirementOutcomes>
        <#assign totalAutomated = requirementOutcome.tests.count("AUTOMATED").withAnyResult()/>
        <#assign automatedPassedPercentage = requirementOutcome.tests.getFormattedPercentage("AUTOMATED").withResult("SUCCESS")/>
        <#assign automatedFailedPercentage = requirementOutcome.tests.getFormattedPercentage("AUTOMATED").withFailureOrError()/>
        <#assign automatedPendingPercentage = requirementOutcome.tests.getFormattedPercentage("AUTOMATED").withIndeterminateResult()/>
        <#assign automatedPassed = requirementOutcome.tests.count("AUTOMATED").withResult("SUCCESS")/>
        <#assign automatedPending = requirementOutcome.tests.count("AUTOMATED").withIndeterminateResult()/>
        <#assign automatedFailed = requirementOutcome.tests.count("AUTOMATED").withResult("FAILURE")/>
        <#assign automatedError = requirementOutcome.tests.count("AUTOMATED").withResult("ERROR")/>
        <#assign automatedCompromised = requirementOutcome.tests.count("AUTOMATED").withResult("COMPROMISED")/>
        <#assign totalManual = requirementOutcome.tests.count("MANUAL").withAnyResult()/>

        <#assign manualPassedPercentage = requirementOutcome.tests.getFormattedPercentage("MANUAL").withResult("SUCCESS")/>
        <#assign manualFailedPercentage = requirementOutcome.tests.getFormattedPercentage("MANUAL").withFailureOrError()/>
        <#assign manualPending = requirementOutcome.tests.count("MANUAL").withIndeterminateResult()/>
        <#assign manualPendingPercentage = requirementOutcome.tests.getFormattedPercentage("MANUAL").withIndeterminateResult()/>
        <#assign manualPassed = requirementOutcome.tests.count("MANUAL").withResult("SUCCESS")/>
        <#assign manualFailed = requirementOutcome.tests.count("MANUAL").withResult("FAILURE")/>
        <#assign manualError = requirementOutcome.tests.count("MANUAL").withResult("ERROR")/>


        <#assign status_icon = formatter.resultIcon().forResult(requirementOutcome.testOutcomes.result) />
        <#assign status_rank = formatter.resultRank().forResult(requirementOutcome.testOutcomes.result) />
        <#assign background_bar_style = 'percentagebar'>
        <#if (totalAutomated + totalManual == 0) >
            <#assign status_icon = formatter.resultIcon().forResult('PENDING') />
            <#assign status_rank = formatter.resultRank().forResult('PENDING') />
            <#assign background_bar_style = 'darkpercentagebar'>
        </#if>

        <#assign requirementReport = reportName.forRequirement(requirementOutcome.requirement) >

        <tr>


            <td> <#-- NAME -->
                <a href="${requirementReport}">${formatter.htmlCompatibleStoryTitle(requirementOutcome.requirement.displayName)}</a></span>
            </td>
            <td> <#-- ID -->
                ${requirementOutcome.cardNumberWithLinks}
            </td>
            <td>${totalAutomated}</td>
            <#if reportOptions.showManualTests>
            <td>${totalManual}</td>
            </#if>
            <td>
                <span class="status_icon">${status_icon}</span>
                <span style="display:none">${status_rank}</span>
            </td>

            <td width="165px">
                <@test_coverage requirementOutcome=requirementOutcome barWidth=125 />
            </td>

        </tr>
    </#foreach>
    </tbody>
</table>


</#macro>
