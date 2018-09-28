<#macro test_coverage(requirementOutcome, barWidth)>

    <#assign totalCount = requirementOutcome.testOutcomes.totalTests.total >
    <#assign successCount = requirementOutcome.testOutcomes.totalTests.withResult("success") >
    <#assign pendingCount = requirementOutcome.testOutcomes.totalTests.withResult("pending") >
    <#assign ignoredCount = requirementOutcome.testOutcomes.totalTests.withResult("ignored") >
    <#assign indeterminateCount = requirementOutcome.testOutcomes.totalTests.withIndeterminateResult() >
    <#assign skippedCount = requirementOutcome.testOutcomes.totalTests.withResult("skipped") >
    <#assign failureCount = requirementOutcome.testOutcomes.totalTests.withResult("failure") >
    <#assign errorCount = requirementOutcome.testOutcomes.totalTests.withResult("error") >
    <#assign compromisedCount = requirementOutcome.testOutcomes.totalTests.withResult("compromised") >
    <#assign requirementsWithoutTestsCount = requirementOutcome.testOutcomes.totalTests.withResult("compromised") >

    <#assign passing = requirementOutcome.testOutcomes.formatted.percentTests().withResult("SUCCESS")>
    <#assign failure = requirementOutcome.testOutcomes.formatted.percentTests().withResult("FAILURE")>
    <#assign error = requirementOutcome.testOutcomes.formatted.percentTests().withResult("ERROR")>
    <#assign compromised = requirementOutcome.testOutcomes.formatted.percentTests().withResult("COMPROMISED")>
    <#assign pending = requirementOutcome.testOutcomes.formatted.percentTests().withResult("PENDING")>
    <#assign ignored = requirementOutcome.testOutcomes.formatted.percentTests().withResult("IGNORED")>
    <#assign skipped = requirementOutcome.testOutcomes.formatted.percentTests().withResult("SKIPPED")>
    <#assign indeterminate = requirementOutcome.testOutcomes.formatted.percentTests().withIndeterminateResult()>

    <#assign tests = inflection.of(requirementOutcome.testOutcomes.total).times("test") >

    <#assign overviewCaption =
"  - Total tests: ${totalCount}
  - Passing tests: ${successCount} (${passing})
  - Failing tests: ${failureCount} (${failure})
  - Tests with errors: ${errorCount} (${error})
  - Compromised tests ${compromisedCount} (${compromised})
  - Pending tests: ${pendingCount} (${pending})
  - Ignored or skipped tests: ${ignoredCount} (${ignored})"
    >

<table>
    <tr>
        <td width="40px">
            <div class="small" style="text-align:right;">${passing}</div>
        </td>
        <td style="min-width:12.5em;">
            <div class="progress">
                <div class="progress-bar" role="progressbar"
                     style="width: ${passing}; background-color:${backgroundColor.forResult('SUCCESS')};"
                     aria-valuenow="${successCount}"
                     aria-valuemin="0"
                     aria-valuemax="${totalCount}"
                     title="${overviewCaption}">
                </div>
                <div class="progress-bar" role="progressbar"
                     style="width: ${pending}; background-color:${backgroundColor.forResult('PENDING')};"
                     aria-valuenow="${pendingCount}"
                     aria-valuemin="0"
                     aria-valuemax="${totalCount}"
                     title="${overviewCaption}">
                </div>
                <div class="progress-bar" role="progressbar"
                     style="width: ${ignored}; background-color:${backgroundColor.forResult('IGNORED')};"
                     aria-valuenow="${ignoredCount}"
                     aria-valuemin="0"
                     aria-valuemax="${totalCount}"
                     title="${overviewCaption}">
                </div>
                <div class="progress-bar" role="progressbar"
                     style="width: ${skipped}; background-color:${backgroundColor.forResult('SKIPPED')};"
                     aria-valuenow="${skippedCount}"
                     aria-valuemin="0"
                     aria-valuemax="${totalCount}"
                     title="${overviewCaption}">
                </div>
                <div class="progress-bar" role="progressbar"
                     style="width: ${failure}; background-color:${backgroundColor.forResult('FAILURE')};"
                     aria-valuenow="${failureCount}"
                     aria-valuemin="0"
                     aria-valuemax="${totalCount}"
                     title="${overviewCaption}">
                </div>
                <div class="progress-bar" role="progressbar"
                     style="width: ${error}; background-color:${backgroundColor.forResult('ERROR')};"
                     aria-valuenow="${errorCount}"
                     aria-valuemin="0"
                     aria-valuemax="${totalCount}"
                     title="${overviewCaption}">
                </div>
                <div class="progress-bar" role="progressbar"
                     style="width: ${compromised}; background-color:${backgroundColor.forResult('COMPROMISED')};"
                     aria-valuenow="${compromisedCount}"
                     aria-valuemin="0"
                     aria-valuemax="${totalCount}"
                     title="${overviewCaption}">
                </div>
            </div>
        </td>
    </tr>
</table>
</#macro>