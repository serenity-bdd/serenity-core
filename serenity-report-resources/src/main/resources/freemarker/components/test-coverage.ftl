<#macro test_coverage(requirementOutcome, barWidth)>

    <#assign totalCount = requirementOutcome.testOutcomes.totalTests.total >
    <#assign successCount = requirementOutcome.testOutcomes.totalTests.withResult("success") >
    <#assign pendingCount = requirementOutcome.testOutcomes.totalTests.withResult("pending") >
    <#assign ignoredCount = requirementOutcome.testOutcomes.totalTests.withResult("ignored") >
    <#assign abortedCount = requirementOutcome.testOutcomes.totalTests.withResult("aborted") >
    <#assign indeterminateCount = requirementOutcome.testOutcomes.totalTests.withIndeterminateResult() >
    <#assign skippedCount = requirementOutcome.testOutcomes.totalTests.withResult("skipped") >
    <#assign failureCount = requirementOutcome.testOutcomes.totalTests.withResult("failure") >
    <#assign errorCount = requirementOutcome.testOutcomes.totalTests.withResult("error") >
    <#assign compromisedCount = requirementOutcome.testOutcomes.totalTests.withResult("compromised") >
    <#assign requirementsWithoutTestsCount = requirementOutcome.testOutcomes.totalTests.withResult("compromised") >

    <#assign passing = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(6).withResult("SUCCESS")>
    <#assign failure = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(6).withResult("FAILURE")>
    <#assign error = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(6).withResult("ERROR")>
    <#assign compromised = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(6).withResult("COMPROMISED")>
    <#assign pending = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(6).withResult("PENDING")>
    <#assign aborted = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(6).withResult("ABORTED")>
    <#assign ignored = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(6).withResult("IGNORED")>
    <#assign skipped = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(6).withResult("SKIPPED")>
    <#assign indeterminate = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(6).withIndeterminateResult()>

    <#assign passingLabel = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(1).withResult("SUCCESS")>
    <#assign failureLabel = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(1).withResult("FAILURE")>
    <#assign errorLabel = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(1).withResult("ERROR")>
    <#assign compromisedLabel = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(1).withResult("COMPROMISED")>
    <#assign pendingLabel = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(1).withResult("PENDING")>
    <#assign abortedLabel = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(1).withResult("ABORTED")>
    <#assign ignoredLabel = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(1).withResult("IGNORED")>
    <#assign skippedLabel = requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(1).withResult("SKIPPED")>
    <#assign indeterminateLabel= requirementOutcome.testOutcomes.formatted.percentTests().withPrecision(1).withIndeterminateResult()>

    <#assign tests = inflection.of(requirementOutcome.testOutcomes.total).times("test") >

    <#assign overviewCaption =
"  - Total tests: ${totalCount}
  - Passing tests: ${successCount} (${passingLabel})
  - Failing tests: ${failureCount} (${failureLabel})
  - Tests with errors: ${errorCount} (${errorLabel})
  - Compromised tests ${compromisedCount} (${compromisedLabel})
  - Aborted tests: ${abortedCount} (${abortedLabel})
  - Pending tests: ${pendingCount} (${pendingLabel})
  - Ignored or skipped tests: ${ignoredCount} (${ignoredLabel})"
    >

<table>
    <tr>
        <td width="40px" style="padding-top:4px;">
            <div class="small" style="text-align:right;">${passingLabel}</div>
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
                     style="width: ${aborted}; background-color:${backgroundColor.forResult('ABORTED')};"
                     aria-valuenow="${abortedCount}"
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