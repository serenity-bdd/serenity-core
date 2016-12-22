<#macro requirements_results(requirements, title, requirementType, id)>

<#include "feature-coverage.ftl">
<#include "test-coverage.ftl">

<div id="req_list_tests" class="table">
    <div class="test-results">
        <table id="${id}">
            <thead>
            <tr>
                <th width="30" class="test-results-heading">&nbsp;</th>
                <th width="65" class="test-results-heading">ID</th>
                <th width="525" class="test-results-heading">${title}</th>
                <#if (requirements.childrenType?has_content) >
                    <#assign childrenTitle = inflection.of(requirements.childrenType).inPluralForm().asATitle()>
                    <th width="65" class="test-results-heading">${childrenTitle}</th>
                </#if>
                <th class="test-results-heading" width="75px">Automated<br/>Tests</th>
                <#if reportOptions.showManualTests>
                    <th class="test-results-heading" width="75px">Manual<br/>Tests</th>
                </#if>

                <th width="125px" class="test-results-heading">Test Results</th>

                <#if (requirements.childrenType?has_content) >
                <th width="125px" class="test-results-heading">Working ${childrenTitle}</th>
                </#if>
            </tr>
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

                <tr class="test-${requirementOutcome.testOutcomes.result} requirementRow">
                    <td class="requirementRowCell">
                        <span class="status_icon">${status_icon}</span>
                        <span style="display:none">${status_rank}</span>
                    </td>
                    <td class="cardNumber requirementRowCell">${requirementOutcome.cardNumberWithLinks}</td>

                    <#assign requirementReport = reportName.forRequirement(requirementOutcome.requirement) >
                    <td class="${requirementOutcome.testOutcomes.result}-text requirementRowCell">
                        <a href="javaScript:void(0)" class="read-more-link">
                        <#--<i class="fa fa-plus-square-o" class="read-more-link"></i>-->
                            <img src="images/plus.png" height="16"/></a>
                        <span class="requirementName"><a
                                href="${requirementReport}">${formatter.htmlCompatibleListEntry(requirementOutcome.requirement.displayName)}</a></span>

                        <div class="requirementNarrative read-more-text">${formatter.renderDescription(requirementOutcome.requirement.narrative.renderedText)}</div>
                    </td>

                    <#if (requirements.childrenType?has_content) >
                        <td class="bluetext requirementRowCell">${requirementOutcome.requirement.childrenCount}</td>
                    </#if>

                    <td class="greentext highlighted-value requirementRowCell">${totalAutomated}</td>
<!--
                    <td class="greentext requirementRowCell">${automatedPassed}</td>
                    <td class="bluetext requirementRowCell">${automatedPending}</td>
                    <td class="redtext requirementRowCell">${automatedFailed}</td>
                    <td class="lightorangetext requirementRowCell">${automatedError}</td>
                    <td class="violettext requirementRowCell">${automatedCompromised}</td>
-->
                    <#if reportOptions.showManualTests>
                        <td class="greentext highlighted-value requirementRowCell">${totalManual}</td>
<!--
                        <td class="greentext requirementRowCell">${manualPassed}</td>
                        <td class="bluetext requirementRowCell">${manualPending}</td>
                        <td class="redtext requirementRowCell">${manualFailed}</td>
                        <td class="lightorangetext requirementRowCell">${manualError}</td>
                        -->
                    </#if>

                    <td width="125px" class="lightgreentext requirementRowCell">
                        <@test_coverage requirementOutcome=requirementOutcome barWidth=125 />
                    </td>

                    <#if (requirements.childrenType?has_content) >
                    <td width="125px" class="lightgreentext requirementRowCell">
                        <@feature_coverage requirementOutcome=requirementOutcome requirementType=childrenTitle barWidth=125 />
                    </td>
                    </#if>
                </tr>
                </#foreach>
            </tbody>
        </table>
    </div>
</div>

</#macro>
