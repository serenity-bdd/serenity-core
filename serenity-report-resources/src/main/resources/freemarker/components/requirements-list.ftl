<#macro requirements_results(requirements, title, id)>
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


                <th width="125px" class="test-results-heading">Coverage</th>
            </tr>
            <tbody>

                <#foreach requirementOutcome in requirements.requirementOutcomes>
                    <#assign status_icon = formatter.resultIcon().forResult(requirementOutcome.result) />
                    <#assign status_rank = formatter.resultRank().forResult(requirementOutcome.result) />

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

                    <#assign successCount = requirementOutcome.testOutcomes.totalTests.withResult("success") >
                    <#assign pendingCount = requirementOutcome.testOutcomes.totalTests.withResult("pending") >
                    <#assign ignoredCount = requirementOutcome.testOutcomes.totalTests.withResult("ignored") >
                    <#assign indeterminateCount = requirementOutcome.testOutcomes.totalTests.withIndeterminateResult() >
                    <#assign skipCount = requirementOutcome.testOutcomes.totalTests.withResult("skipped") >
                    <#assign failureCount = requirementOutcome.testOutcomes.totalTests.withResult("failure") >
                    <#assign errorCount = requirementOutcome.testOutcomes.totalTests.withResult("error") >
                    <#assign compromisedCount = requirementOutcome.testOutcomes.totalTests.withResult("compromised") >

                <#--<td class="bluetext requirementRowCell">${requirementOutcome.testOutcomes.total}</td>-->
                <#--<td class="greentext requirementRowCell">${successCount}</td>-->
                <#--<td class="redtext requirementRowCell">${failureCount}</td>-->
                <#--<td class="lightredtext requirementRowCell">${errorCount}</td>-->
                <#--<td class="bluetext requirementRowCell">${indeterminateCount}</td>-->

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
                        <#assign percentPending = requirementOutcome.percent.withResult("PENDING")/>
                        <#assign percentIgnored = requirementOutcome.percent.withResult("IGNORED") + requirementOutcome.percent.withResult("SKIPPED")/>
                        <#assign percentError = requirementOutcome.percent.withResult("ERROR")/>
                        <#assign percentCompromised = requirementOutcome.percent.withResult("COMPROMISED")/>
                        <#assign percentFailing = requirementOutcome.percent.withResult("FAILURE")/>
                        <#assign percentPassing = requirementOutcome.percent.withResult("SUCCESS")/>
                        <#assign percentIndeterminate = requirementOutcome.percent.withIndeterminateResult()/>
                        <#assign passing = requirementOutcome.formattedPercentage.withResult("SUCCESS")>
                        <#assign failing = requirementOutcome.formattedPercentage.withResult("FAILURE")>
                        <#assign error = requirementOutcome.formattedPercentage.withResult("ERROR")>
                        <#assign compromised = requirementOutcome.formattedPercentage.withResult("COMPROMISED")>
                        <#assign pending = requirementOutcome.formattedPercentage.withResult("PENDING")>
                        <#assign ignored = requirementOutcome.formattedPercentage.withSkippedOrIgnored()>
                        <#assign indeterminate = requirementOutcome.formattedPercentage.withIndeterminateResult()>


                        <#assign ignoredbar = (percentPassing + percentFailing + percentError + percentCompromised + percentIgnored)*125>
                        <#assign compromisedbar = (percentPassing + percentFailing + percentError + percentCompromised)*125>
                        <#assign errorbar = (percentPassing + percentFailing + percentError)*125>
                        <#assign failingbar = (percentPassing + percentFailing)*125>
                        <#assign passingbar = percentPassing*125>


                        <#assign tests = inflection.of(requirementOutcome.testOutcomes.total).times("test") >

                        <!--
                        Accessing the ESAA Registration screens

                        Tests implemented: 10
                          - Passing tests: 4
                          - Failing tests: 3

                        Requirements specified:   6
                        Requiments without tests: 2

                        Estimated unimplemented tests: 7
                        -->
                        <#assign overviewCaption =
                        "${requirementOutcome.requirement.displayName}
Tests implemented: ${requirementOutcome.testCount}
  - Passing tests: ${successCount} (${passing} of specified requirements)
  - Failing tests: ${failureCount} (${failing} of specified requirements)
  - Tests with errors: ${errorCount} (${error} of specified requirements)
  - Compromised tests ${compromisedCount} (${compromised} of specified requirements)

Tests not implemented or not executed: ${pendingCount + ignoredCount}
  - Pending tests: ${pendingCount} (${pending} of specified requirements)
  - Ignored or skipped tests: ${ignoredCount} (${ignored} of specified requirements)

Requirements specified:     ${requirementOutcome.flattenedRequirementCount}
Requirements with no tests: ${requirementOutcome.requirementsWithoutTestsCount}


Estimated unimplemented tests: ${requirementOutcome.estimatedUnimplementedTests}
Estimated unimplemented or pending requirements: ${pending}
Estimated ignored or skipped requirements: ${ignored}"
                        >

                        <table>
                            <tr>
                                <td width="40px">
                                    <div class="small">${passing}</div>
                                </td>
                                <td width="125px">
                                    <div class="percentagebar"
                                         title="${overviewCaption}"
                                         style="width: 125px;">
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
