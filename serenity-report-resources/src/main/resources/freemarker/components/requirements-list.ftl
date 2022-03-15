<#macro requirements_results(requirements, title, requirementType, id)>

    <#include "feature-coverage.ftl">
    <#include "test-coverage.ftl">

<#--    <#if requirements.visibleOutcomes?size gt 10>-->
        <script>
            $(document).ready(function () {

                $('#${id}').DataTable({
                    "order": [[0, "asc",], [3, "asc",]],
                    "pageLength": 10,
                    "language": {
                        searchPlaceholder: "Filter",
                        search: ""
                    },
                });
            });
        </script>
<#--    </#if>-->
    <h3 class="requirement-type-heading">Test Outcomes for ${requirementType} </h3>

    <table class="scenario-result table" id="${id}">
        <thead>
        <tr>
            <th class="requirement-name-column" width="60%">${title}</th>
            <th style="width:1em;">Test&nbsp;Cases</th>
            <th style="width:1em;">Scenarios</th>
            <th style="width:1em;">%&nbsp;Pass</th>
            <th style="width:1em;">Result</th>
            <th>Coverage</th>
        </tr>
        </thead>
        <tbody>
        <#foreach requirementOutcome in requirements.visibleOutcomes>
            <#assign totalAutomated = requirementOutcome.tests.count("AUTOMATED").withAnyResult()/>
            <#assign overallPassedPercentage = requirementOutcome.tests.getFormattedPercentage("ANY").withResult("SUCCESS")/>
            <#assign totalManual = requirementOutcome.tests.count("MANUAL").withAnyResult()/>
            <#assign status_icon = formatter.resultIcon().forResult(requirementOutcome.testOutcomes.result) />
            <#assign status_rank = formatter.resultRank().forResult(requirementOutcome.testOutcomes.result) />
            <#assign background_bar_style = 'percentagebar'>
            <#if (totalAutomated + totalManual == 0) >
                <#assign status_icon = formatter.resultIcon().forResult('PENDING') />
                <#assign status_rank = formatter.resultRank().forResult('PENDING') />
                <#assign background_bar_style = 'darkpercentagebar'>
            </#if>

            <#assign requirementReport = reportName.forRequirement(requirementOutcome.requirement) >
            <#if (duplicateRequirementNamesPresent) >
                <#assign requirementName = formatter.htmlCompatibleStoryTitle(requirementOutcome.requirement.displayNameWithParent)/>
            <#else>
                <#assign requirementName = formatter.htmlCompatibleStoryTitle(requirementOutcome.requirement.displayName)/>
            </#if>
            <tr>
                <td> <a href="${requirementReport}">${requirementName}</a></td>
                <td>${requirementOutcome.testCaseCount}</td>
                <td>${requirementOutcome.scenarioCount}</td>
                <td>${overallPassedPercentage}</td>
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
