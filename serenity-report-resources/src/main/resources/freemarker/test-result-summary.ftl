
<#assign totalCount = testOutcomes.totalTests.total >
<#assign successCount = testOutcomes.totalTests.withResult("success") >
<#assign pendingCount = testOutcomes.totalTests.withResult("pending") >
<#assign ignoredOrSkippedCount = testOutcomes.totalTests.withResult("ignored") + testOutcomes.totalTests.withResult("skipped")>
<#assign failureCount = testOutcomes.totalTests.withResult("failure") >
<#assign errorCount = testOutcomes.totalTests.withResult("error") >
<#assign compromisedCount = testOutcomes.totalTests.withResult("compromised") >
<#assign failureOrErrorCount = testOutcomes.totalTests.withFailureOrError() >

<#assign autoTotalCount = testOutcomes.count("AUTOMATED").total >
<#assign autoSuccessCount = testOutcomes.count("AUTOMATED").withResult("success") >
<#assign autoPendingCount = testOutcomes.count("AUTOMATED").withResult("pending") >
<#assign autoIgnoredOrSkippedCount = testOutcomes.count("AUTOMATED").withResult("ignored") + testOutcomes.count("AUTOMATED").withResult("skipped") >
<#assign autoFailureOrErrorCount = testOutcomes.count("AUTOMATED").withFailureOrError() >

<#if (autoTotalCount > 0)>
    <#assign autoPercentageSuccessCount = autoSuccessCount / autoTotalCount >
    <#assign autoPercentagePendingCount = autoPendingCount / autoTotalCount  >
    <#assign autoPercentageIgnoredOrSkippedCount = autoIgnoredOrSkippedCount / autoTotalCount  >
    <#assign autoPercentageFailureOrErrorCount = autoFailureOrErrorCount / autoTotalCount  >
<#else>
    <#assign autoPercentageSuccessCount = 0.0 >
    <#assign autoPercentagePendingCount = 0.0 >
    <#assign autoPercentageIgnoredOrSkippedCount = 0.0 >
    <#assign autoPercentageFailureOrErrorCount = 0.0 >
</#if>

<#assign manualTotalCount = testOutcomes.count("MANUAL").total >
<#assign manualSuccessCount = testOutcomes.count("MANUAL").withResult("success") >
<#assign manualPendingCount = testOutcomes.count("MANUAL").withResult("pending") >
<#assign manualIgnoredCount = testOutcomes.count("MANUAL").withResult("ignored") >
<#assign manualFailureOrErrorCount = testOutcomes.count("MANUAL").withFailureOrError() >

<#if (manualTotalCount > 0)>
    <#assign manualPercentageSuccessCount = manualSuccessCount / manualTotalCount >
    <#assign manualPercentagePendingCount = manualPendingCount / manualTotalCount  >
    <#assign manualPercentageIgnoredCount = manualIgnoredCount / manualTotalCount  >
    <#assign manualPercentageFailureOrErrorCount = manualFailureOrErrorCount / manualTotalCount  >
<#else>
    <#assign manualPercentageSuccessCount = 0.0 >
    <#assign manualPercentagePendingCount = 0.0 >
    <#assign manualPercentageIgnoredCount = 0.0 >
    <#assign manualPercentageFailureOrErrorCount = 0.0 >
</#if>

<#if (totalCount > 0)>
    <#assign percentageSuccessCount = successCount / totalCount >
    <#assign percentagePendingCount = pendingCount / totalCount  >
    <#assign percentageIgnoredOrSkippedCount = ignoredOrSkippedCount / totalCount  >
    <#assign percentageFailureOrErrorCount = (manualFailureOrErrorCount + autoFailureOrErrorCount) / totalCount  >
<#else>
    <#assign percentageSuccessCount = 0.0 >
    <#assign percentagePendingCount = 0.0 >
    <#assign percentageIgnoredOrSkippedCount = 0.0 >
    <#assign percentageFailureOrErrorCount = 0.0 >
</#if>

<div>
    <h4>Test Result Summary</h4>
    <table class="summary-table">
        <head>
            <tr>
                <th>Test Type</th>
                <th>Total</th>
                <th>Pass&nbsp;<i class="icon-check"/> </th>
                <th>Fail&nbsp;<i class="icon-thumbs-down"/></th>
                <th>Pending&nbsp;<i class="icon-calendar"/></th>
                <th>Ignore/Skip&nbsp;<i class="icon-ban-circle"/></th>
            </tr>
        </head>
        <body>
        <tr>
            <td class="summary-leading-column">Automated</td>
            <td>${autoTotalCount}</td>
            <td>${autoSuccessCount} (${autoPercentageSuccessCount?string.percent})</td>
            <td>${autoFailureOrErrorCount} (${autoPercentageFailureOrErrorCount?string.percent})</td>
            <td>${autoPendingCount} (${autoPercentagePendingCount?string.percent})</td>
            <td>${autoIgnoredOrSkippedCount} (${autoPercentageIgnoredOrSkippedCount?string.percent})</td>
        </tr>
        <tr>
            <td class="summary-leading-column">Manual</td>
            <td>${manualTotalCount}</td>
            <td>${manualSuccessCount} (${manualPercentageSuccessCount?string.percent})</td>
            <td>${manualFailureOrErrorCount} (${manualPercentageFailureOrErrorCount?string.percent})</td>
            <td>${manualPendingCount} (${manualPercentagePendingCount?string.percent})</td>
            <td>${manualIgnoredCount} (${manualPercentageIgnoredCount?string.percent})</td>
        </tr>
        <tr>
            <td class="summary-leading-column">Total</td>
            <td>${totalCount}</td>
            <td>${successCount} (${percentageSuccessCount?string.percent})</td>
            <td>${failureOrErrorCount} (${percentageFailureOrErrorCount?string.percent})</td>
            <td>${pendingCount} (${percentagePendingCount?string.percent})</td>
            <td>${ignoredOrSkippedCount} (${percentageIgnoredOrSkippedCount?string.percent})</td>
        </tr>
        <tr>
            <td class="summary-leading-column">Total Duration</td>
            <#assign durationInSeconds = testOutcomes.duration / 1000>
            <#if (durationInSeconds > 60)>
                <td colspan="5">${(durationInSeconds / 60)?int} minutes ${(durationInSeconds % 60)?round} seconds</td>
            <#else>
                <td colspan="5">${durationInSeconds?round} seconds</td>
            </#if>
        </tr>
        </body>
    </table>
</div>