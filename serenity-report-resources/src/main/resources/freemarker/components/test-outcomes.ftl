<#macro test_results(testOutcomes, title, id)>
<table>
    <tr>
        <td>
            <div><h3>${title}</h3></div>
            <div id="test_list_tests" class="table">
                <div class="test-results">
                    <table id="${id}">
                        <thead>
                        <tr>
                            <th width="50" class="test-results-heading">&nbsp;</th>
                            <th width="%" class="test-results-heading">Tests</th>
                            <th width="70" class="test-results-heading">Steps</th>

                            <#if reportOptions.showStepDetails>
                                <th width="65" class="test-results-heading">Fail</th>
                                <th width="65" class="test-results-heading">Error</th>
                                <th width="65" class="test-results-heading">Comp</th>
                                <th width="65" class="test-results-heading">Abort</th>
                                <th width="65" class="test-results-heading">Pend</th>
                                <th width="65" class="test-results-heading">Ignore</th>
                                <th width="65" class="test-results-heading">Skip</th>
                            </#if>
                            <th width="100" class="test-results-heading">Started</th>
                            <th width="100" class="test-results-heading">Took<br>(secs)</th>
                        </tr>
                        </thead>
                        <tbody>
                            <#assign testResultSet = testOutcomes.tests >
                            <#foreach testOutcome in testResultSet>
                                <#assign test_outcome_icon = formatter.resultIcon().forResult(testOutcome.result) />

                                <#assign exampleCount = "" />
                                <#if testOutcome.dataDriven>
                                    <#assign exampleCount>&nbsp;(${testOutcome.dataTable.size}  examples)</#assign>
                                </#if>

                            <tr class="test-${testOutcome.result}">
                                <td><span class="summary-icon">${test_outcome_icon}</span>
                                    <#if (testOutcome.manual)><i class="bi bi-person manual" title="Manual test"></i></#if>
                                    <#list testOutcome.flags as flag>
                                        <i class="bi bi-${flag.symbol} flag-color" alt="${flag.message}" title="${flag.message}"></i>
                                    </#list>
                                    <span style="display:none">${testOutcome.result}</span></td>
                                <td class="${testOutcome.result}-text">
                                    <div class="ellipsis">
                                        <#assign testOutcomeTitle = testOutcome.unqualified.withContext().title >
                                        <#assign testOutcomeTitleTip = testOutcome.unqualified.title >

                                        <a href="${relativeLink}${testOutcome.reportName}.html" class="ellipsis"
                                           title="${formatter.htmlAttributeCompatible(testOutcomeTitleTip)} ${formatter.htmlAttributeCompatible(testOutcome.conciseErrorMessage, 40)}">
                                            ${formatter.htmlCompatibleTestTitle(testOutcomeTitle)}${exampleCount}
                                            <#if (!testOutcome.titleWithIssues)>
                                                <span class="related-issue-title">${testOutcome.formattedIssues}</span>
                                            </#if>
                                        </a>
                                    </div>
                                </td>

                                <td class="${testOutcome.result}-text">${testOutcome.nestedStepCount}</td>

                                <#if reportOptions.showStepDetails>
                                    <td class="redtext">${testOutcome.failureCount}</td>
                                    <td class="redtext">${testOutcome.errorCount}</td>
                                    <td class="redtext">${testOutcome.compromisedCount}</td>
                                    <td class="bluetext">${testOutcome.abortedCount}</td>
                                    <td class="bluetext">${testOutcome.pendingCount}</td>
                                    <td class="bluetext">${testOutcome.skippedCount}</td>
                                    <td class="bluetext">${testOutcome.ignoredCount}</td>
                                </#if>

                                <td class="${testOutcome.result}-text" data-order="${testOutcome.timestamp}">${testOutcome.startedAt}</td>

                                <td class="${testOutcome.result}-text">${testOutcome.durationInSeconds}</td>
                            </tr>
                            </#foreach>
                        </tbody>
                    </table>
                </div>
            </div>
        </td>
    </tr>
</table>
</div>
</#macro>
