<#macro requirements_results(requirements, title, requirementType, id)>
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
                <th class="test-results-heading" width="75px">Auto.<br/>Tests</th>
                <th class="test-results-heading" width="25px"><i class="fa fa-check-square-o"
                                                                 title="Tests passed (automated)"></i>
                </th>
                <th class="test-results-heading" width="25px"><i class="fa fa-square-o"
                                                                 title="Tests skipped or pending (automated)">
                </th>
                <th class="test-results-heading" width="25px"><i class="fa fa-times-circle"
                                                                 title="Tests failed (automated)"></th>
                <th class="test-results-heading" width="25px"><i
                        class="fa fa-exclamation-triangle"
                        title="Tests failed with an error (automated)"></th>
                <th class="test-results-heading" width="25px">
                    <i class="fa fa-chain-broken fa-lg" title="Tests compromised"></i>
                </th>
                <#if reportOptions.showManualTests>
                    <th class="test-results-heading" width="75px">Manual<br/>Tests</th>
                    <th class="test-results-heading" width="25px"><i class="fa fa-check-square-o"
                                                                     title="Tests passed (manual)"></i>
                    </th>
                    <th class="test-results-heading" width="25px"><i class="fa fa-square-o"
                                                                     title="Tests skipped or pending (manual)">
                    </th>
                    <th class="test-results-heading" width="25px"><i class="fa fa-times-circle"
                                                                     title="Tests failed (manual)"></th>
                    <th class="test-results-heading" width="25px"><i
                            class="fa fa-exclamation-triangle"
                            title="Tests failed with an error (manual)"></th>
                </#if>


                <th width="125px" class="test-results-heading">Working ${requirementType}</th>
            </tr>
            <tbody>

                <#foreach requirementOutcome in requirements.requirementOutcomes>

                    <#assign successCount = requirementOutcome.subrequirements.withResult("success") >
                    <#assign pendingCount = requirementOutcome.subrequirements.withResult("pending") >
                    <#assign ignoredCount = requirementOutcome.subrequirements.withResult("ignored") >
                    <#assign skipCount = requirementOutcome.subrequirements.withResult("skipped") >
                    <#assign failureCount = requirementOutcome.subrequirements.withResult("failure") >
                    <#assign errorCount = requirementOutcome.subrequirements.withResult("error") >
                    <#assign compromisedCount = requirementOutcome.subrequirements.withResult("compromised") >
                    <#assign untestedCount = requirementOutcome.subrequirements.withNoTests() >

                    <#--<#assign successCount = requirementOutcome.testOutcomes.totalTests.withResult("success") >-->
                    <#--<#assign pendingCount = requirementOutcome.testOutcomes.totalTests.withResult("pending") >-->
                    <#--<#assign ignoredCount = requirementOutcome.testOutcomes.totalTests.withResult("ignored") >-->
                    <#--<#assign indeterminateCount = requirementOutcome.testOutcomes.totalTests.withIndeterminateResult() >-->
                    <#--<#assign skipCount = requirementOutcome.testOutcomes.totalTests.withResult("skipped") >-->
                    <#--<#assign failureCount = requirementOutcome.testOutcomes.totalTests.withResult("failure") >-->
                    <#--<#assign errorCount = requirementOutcome.testOutcomes.totalTests.withResult("error") >-->
                    <#--<#assign compromisedCount = requirementOutcome.testOutcomes.totalTests.withResult("compromised") >-->
                    <#--<#assign requirementsWithoutTestsCount = requirementOutcome.testOutcomes.totalTests.withResult("compromised") >-->

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
                                href="${requirementReport}">${requirementOutcome.requirement.displayName}</a></span>

                        <div class="requirementNarrative read-more-text">${formatter.renderDescription(requirementOutcome.requirement.narrative.renderedText)}</div>
                    </td>

                    <#if (requirements.childrenType?has_content) >
                        <td class="bluetext requirementRowCell">${requirementOutcome.requirement.childrenCount}</td>
                    </#if>

                    <td class="greentext highlighted-value requirementRowCell">${totalAutomated}</td>
                    <td class="greentext requirementRowCell">${automatedPassed}</td>
                    <td class="bluetext requirementRowCell">${automatedPending}</td>
                    <td class="redtext requirementRowCell">${automatedFailed}</td>
                    <td class="lightorangetext requirementRowCell">${automatedError}</td>
                    <td class="violettext requirementRowCell">${automatedCompromised}</td>
                    <#if reportOptions.showManualTests>
                        <td class="greentext highlighted-value requirementRowCell">${totalManual}</td>
                        <td class="greentext requirementRowCell">${manualPassed}</td>
                        <td class="bluetext requirementRowCell">${manualPending}</td>
                        <td class="redtext requirementRowCell">${manualFailed}</td>
                        <td class="lightorangetext requirementRowCell">${manualError}</td>
                    </#if>

                    <td width="125px" class="lightgreentext requirementRowCell">
                        <#assign barWidth = 125 />

                        <#assign percentUntested = requirementOutcome.subrequirements.proportion.withNoTests()/>
                        <#assign percentPending = requirementOutcome.subrequirements.proportion.withResult("PENDING")/>
                        <#assign percentIgnored = requirementOutcome.subrequirements.proportion.withSkippedOrIgnored() />
                        <#assign percentError = requirementOutcome.subrequirements.proportion.withResult("ERROR")/>
                        <#assign percentCompromised = requirementOutcome.subrequirements.proportion.withResult("COMPROMISED")/>
                        <#assign percentFailing = requirementOutcome.subrequirements.proportion.withResult("FAILURE")/>
                        <#assign percentPassing = requirementOutcome.subrequirements.proportion.withResult("SUCCESS")/>

                        <#assign passing = requirementOutcome.subrequirements.percentage.withResult("SUCCESS")>
                        <#assign failing = requirementOutcome.subrequirements.percentage.withResult("FAILURE")>
                        <#assign error = requirementOutcome.subrequirements.percentage.withResult("ERROR")>
                        <#assign compromised = requirementOutcome.subrequirements.percentage.withResult("COMPROMISED")>
                        <#assign pending = requirementOutcome.subrequirements.percentage.withResult("PENDING")>
                        <#assign ignored = requirementOutcome.subrequirements.percentage.withSkippedOrIgnored()>
                        <#assign untested = requirementOutcome.subrequirements.percentage.withNoTests()>

                        <#assign passingbar = percentPassing*barWidth>
                        <#assign failingbar = (percentPassing + percentFailing)*barWidth>
                        <#assign errorbar = (percentPassing + percentFailing + percentError)*barWidth>
                        <#assign compromisedbar = (percentPassing + percentFailing + percentError + percentCompromised)*barWidth>
                        <#assign ignoredbar = (percentPassing + percentFailing + percentError + percentCompromised + percentIgnored)*barWidth>
                        <#assign pendingbar = (percentPassing + percentFailing + percentError + percentCompromised + percentIgnored + percentPending)*barWidth>
                        <#assign untestedbar = (percentPassing + percentFailing + percentError + percentCompromised + percentIgnored + percentPending + percentUntested)*barWidth>

                        <#assign tests = inflection.of(requirementOutcome.testOutcomes.total).times("test") >

                        <#assign overviewCaption =
"${requirementOutcome.requirement.displayName}
${requirementType} completed: ${passing}
  - Passing ${requirementType}: ${successCount} (${passing})

${requirementType} incomplete or broken: ${failing + error + compromised}
  - ${requirementType} Failing: ${failureCount} (${failing})
  - ${requirementType} Failing with errors: ${errorCount} (${error})
  - Compromised ${requirementType}: ${compromisedCount} (${compromised})

${requirementType} not implemented or not executed: ${pendingCount + ignoredCount + untestedCount}
  - Pending ${requirementType}: ${pendingCount} (${pending})
  - Ignored or skipped ${requirementType}: ${ignoredCount} (${ignored})
  - Untested ${requirementType}: ${untestedCount} (${untested})"
                        >

                        <table>
                            <tr>
                                <td width="40px">
                                    <div class="small">${passing}</div>
                                </td>
                                <td width="125px">
                                    <#--<div class="untestedbar"-->
                                         <#--title="${overviewCaption}"-->
                                         <#--style="width: 125px;">-->
                                        <#--<div class="pendingbar"-->
                                             <#--title="${overviewCaption}"-->
                                             <#--style="width: ${pendingbar?string("0")}px;">-->
                                            <#--<div class="ignoredbar"-->
                                                 <#--style="width: ${ignoredbar?string("0")}px;"-->
                                                 <#--title="${overviewCaption}">-->
                                                <#--<div class="compromisedbar"-->
                                                     <#--style="width: ${compromisedbar?string("0")}px;"-->
                                                     <#--title="${overviewCaption}">-->
                                                    <#--<div class="errorbar"-->
                                                         <#--style="width: ${errorbar?string("0")}px;"-->
                                                         <#--title="${overviewCaption}">-->
                                                        <#--<div class="failingbar"-->
                                                             <#--style="width: ${failingbar?string("0")}px;"-->
                                                             <#--title="${overviewCaption}">-->
                                                            <#--<div class="passingbar"-->
                                                                 <#--style="width: ${passingbar?string("0")}px;"-->
                                                                 <#--title="${overviewCaption}">-->
                                                            <#--</div>-->
                                                        <#--</div>-->
                                                    <#--</div>-->
                                                <#--</div>-->
                                            <#--</div>-->
                                        <#--</div>-->
                                    <#--</div>-->
                                        <div class="percentagebar"
                                             title="${overviewCaption}"
                                             style="width: 125px;">
                                            <div class="pendingbar"
                                                 title="${overviewCaption}"
                                                 style="width: ${pendingbar?string("0")}px;">
                                                <div class="ignoredbar"
                                                     style="width: ${ignoredbar?string("0")}px;"
                                                     title="${overviewCaption}">
                                                    <div class="compromisedbar"
                                                         style="width: ${compromisedbar?string("0")}px;"
                                                         title="${overviewCaption}">
                                                        <div class="errorbar"
                                                             style="width: ${errorbar?string("0")}px;"
                                                             title="${overviewCaption}">
                                                            <div class="failingbar"
                                                                 style="width: ${failingbar?string("0")}px;"
                                                                 title="${overviewCaption}">
                                                                <div class="passingbar"
                                                                     style="width: ${passingbar?string("0")}px;"
                                                                     title="${overviewCaption}">
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                </td>
                            </tr>
                        </table>
                    </td>

                </tr>
                </#foreach>
            </tbody>
        </table>
    </div>
</div>

</#macro>
