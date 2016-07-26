<#macro feature_coverage(requirementOutcome, requirementType, barWidth)>

    <#assign percentPending = requirementOutcome.subrequirements.proportion.withResult("PENDING")/>
    <#assign percentIgnored = requirementOutcome.subrequirements.proportion.withSkippedOrIgnored() />
    <#assign percentError = requirementOutcome.subrequirements.proportion.withResult("ERROR")/>
    <#assign percentCompromised = requirementOutcome.subrequirements.proportion.withResult("COMPROMISED")/>
    <#assign percentFailing = requirementOutcome.subrequirements.proportion.withResult("FAILURE")/>
    <#assign percentPassing = requirementOutcome.subrequirements.proportion.withResult("SUCCESS")/>
    <#assign percentUntested = requirementOutcome.subrequirements.proportion.withNoTests()/>

    <#assign successCount = requirementOutcome.subrequirements.withResult("success") >
    <#assign pendingCount = requirementOutcome.subrequirements.withResult("pending") >
    <#assign ignoredCount = requirementOutcome.subrequirements.withResult("ignored") >
    <#assign skipCount = requirementOutcome.subrequirements.withResult("skipped") >
    <#assign failureCount = requirementOutcome.subrequirements.withResult("failure") >
    <#assign errorCount = requirementOutcome.subrequirements.withResult("error") >
    <#assign compromisedCount = requirementOutcome.subrequirements.withResult("compromised") >
    <#assign untestedCount = requirementOutcome.subrequirements.withNoTests() >

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
    "${requirementOutcome.requirement.displayName}:
  - Passing ${requirementType}: ${successCount} (${passing}
  - ${requirementType} Failing: ${failureCount} (${failing}
  - ${requirementType} Failing with errors: ${errorCount}
  - Compromised ${requirementType}: ${compromisedCount}
  - Pending ${requirementType}: ${pendingCount}
  - Ignored or skipped ${requirementType}: ${ignoredCount}
  - Untested ${requirementType}: ${untestedCount} (${untested}"
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