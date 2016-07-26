<#macro test_coverage(requirementOutcome, barWidth)>

    <#assign totalCount = requirementOutcome.testOutcomes.totalTests.total >
    <#assign successCount = requirementOutcome.testOutcomes.totalTests.withResult("success") >
    <#assign pendingCount = requirementOutcome.testOutcomes.totalTests.withResult("pending") >
    <#assign ignoredCount = requirementOutcome.testOutcomes.totalTests.withResult("ignored") >
    <#assign indeterminateCount = requirementOutcome.testOutcomes.totalTests.withIndeterminateResult() >
    <#assign skipCount = requirementOutcome.testOutcomes.totalTests.withResult("skipped") >
    <#assign failureCount = requirementOutcome.testOutcomes.totalTests.withResult("failure") >
    <#assign errorCount = requirementOutcome.testOutcomes.totalTests.withResult("error") >
    <#assign compromisedCount = requirementOutcome.testOutcomes.totalTests.withResult("compromised") >
    <#assign requirementsWithoutTestsCount = requirementOutcome.testOutcomes.totalTests.withResult("compromised") >

    <#assign percentPending = requirementOutcome.testOutcomes.proportion.withResult("PENDING")/>
    <#assign percentIgnored = requirementOutcome.testOutcomes.proportion.withResult("IGNORED")
                              + requirementOutcome.testOutcomes.proportion.withResult("SKIPPED")/>
    <#assign percentError = requirementOutcome.testOutcomes.proportion.withResult("ERROR")/>
    <#assign percentCompromised = requirementOutcome.testOutcomes.proportion.withResult("COMPROMISED")/>
    <#assign percentFailing = requirementOutcome.testOutcomes.proportion.withResult("FAILURE")/>
    <#assign percentPassing = requirementOutcome.testOutcomes.proportion.withResult("SUCCESS")/>
    <#assign percentIndeterminate = requirementOutcome.testOutcomes.proportion.withIndeterminateResult()/>

    <#assign passing = requirementOutcome.testOutcomes.formatted.percentTests().withResult("SUCCESS")>
    <#assign failing = requirementOutcome.testOutcomes.formatted.percentTests().withResult("FAILURE")>
    <#assign error = requirementOutcome.testOutcomes.formatted.percentTests().withResult("ERROR")>
    <#assign compromised = requirementOutcome.testOutcomes.formatted.percentTests().withResult("COMPROMISED")>
    <#assign pending = requirementOutcome.testOutcomes.formatted.percentTests().withResult("PENDING")>
    <#assign ignored = requirementOutcome.testOutcomes.formatted.percentTests().withSkippedOrIgnored()>
    <#assign indeterminate = requirementOutcome.testOutcomes.formatted.percentTests().withIndeterminateResult()>

    <#assign pendingbar = (percentPassing + percentFailing + percentError + percentCompromised + percentIgnored + percentPending)*125>
    <#assign ignoredbar = (percentPassing + percentFailing + percentError + percentCompromised + percentIgnored)*125>
    <#assign compromisedbar = (percentPassing + percentFailing + percentError + percentCompromised)*125>
    <#assign errorbar = (percentPassing + percentFailing + percentError)*125>
    <#assign failingbar = (percentPassing + percentFailing)*125>
    <#assign passingbar = percentPassing*125>


    <#assign tests = inflection.of(requirementOutcome.testOutcomes.total).times("test") >

    <#assign overviewCaption =
"  - Total tests: ${totalCount}
  - Passing tests: ${successCount} (${passing})
  - Failing tests: ${failureCount} (${failing})
  - Tests with errors: ${errorCount} (${error})
  - Compromised tests ${compromisedCount} (${compromised})
  - Pending tests: ${pendingCount} (${pending})
  - Ignored or skipped tests: ${ignoredCount} (${ignored})"
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
</#macro>