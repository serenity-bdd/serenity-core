<#macro list_tags(weighted, context)>
<table class="tags-summary-table">
    <tr>
        <td width="300px"><h3>${tagsTitle}</h3></td>
        <td width="90px" class="tag-count-header">% Passed</td>
        <td width="130px" class="test-count">&nbsp;</td>
        <td class="tag-count-header">Test count</td>
    </tr>
</table>
    <#foreach tagType in tagTypes>
        <#assign tagTypeTitle = inflection.of(tagType).inPluralForm().asATitle() >
        <#assign outcomesForType = testOutcomes.withTagType(tagType) >
        <#assign tags = testOutcomes.getTagsOfTypeExcluding(tagType, testOutcomes.label) >
        <#if tags?has_content >
        <table class="test-summary-table">
            <tr>
                <td colspan="3">
                    <div class="tagTypeTitle">${tagTypeTitle}</div>
                </td>
            </tr>
            <#foreach tag in tags>
                <#assign tagTitle = formatter.htmlCompatible(tagInflector.ofTag(tagType, tag.shortName).toFinalView()) >
                <#assign tagLabel = inflection.of(tag.name).asATitle() >
                <#assign tagReport = reportName.inContext(currentTag.completeName).forRequirementOrTag(tag) >
                <#assign outcomesForTag = testOutcomes.withTag(tag) >
                <#assign count = outcomesForTag.total>
                <#assign testCountLabel = inflection.of(count).times("test").inPluralForm() >
                <tr>
                    <td class="bluetext tag-title">
                        <span class="${outcomesForTag.result}-text truncated-tag-title">
                            <#if testOutcomes.label == tag.name>
                                <a href="${tagReport}" title="${tagLabel}" class="currentTag">${tagTitle}</a>
                            <#else>
                                <a href="${tagReport}" title="${tagTitle}">${tagTitle}</a>
                            </#if>
                        </span>
                    </td>
                    <td width="220px" class="table-figure">
                        <#if weighted == "true">
                            <#assign percentPending = outcomesForTag.percentSteps.withResult("pending")/>
                            <#assign percentIgnored = outcomesForTag.percentSteps.withResult("ignored") + outcomesForTag.percentSteps.withResult("skipped")/>
                            <#assign percentError = outcomesForTag.percentSteps.withResult("error")/>
                            <#assign percentCompromised = outcomesForTag.percentSteps.withResult("compromised")/>
                            <#assign percentFailing = outcomesForTag.percentSteps.withResult("failure")/>
                            <#assign percentPassing = outcomesForTag.percentSteps.withResult("success")/>

                            <#assign passing = outcomesForTag.formattedPercentageSteps.withResult("success")>
                            <#assign ignored = outcomesForTag.formattedPercentageSteps.withSkippedOrIgnored()/>
                            <#assign failing = outcomesForTag.formattedPercentageSteps.withResult("failure")>
                            <#assign error = outcomesForTag.formattedPercentageSteps.withResult("error")>
                            <#assign compromised = outcomesForTag.formattedPercentageSteps.withResult("compromised")>
                            <#assign pending = outcomesForTag.formattedPercentageSteps.withResult("pending")>
                        <#else>
                            <#assign percentPending = outcomesForTag.proportion.withResult("pending")/>
                            <#assign percentError = outcomesForTag.proportion.withResult("error")/>
                            <#assign percentCompromised = outcomesForTag.proportion.withResult("compromised")/>
                            <#assign percentIgnored = outcomesForTag.proportion.withResult("ignored") + outcomesForTag.proportion.withResult("skipped")/>
                            <#assign percentFailing = outcomesForTag.proportion.withResult("failure")/>
                            <#assign percentPassing = outcomesForTag.proportion.withResult("success")/>

                            <#assign passing = outcomesForTag.formattedPercentage.withResult("success")>
                            <#assign failing = outcomesForTag.formattedPercentage.withResult("failure")>
                            <#assign error = outcomesForTag.formattedPercentage.withResult("error")>
                            <#assign compromised = outcomesForTag.formattedPercentage.withResult("compromised")>
                            <#assign pending = outcomesForTag.formattedPercentage.withResult("pending")>
                            <#assign ignored = outcomesForTag.formattedPercentage.withResult("ignored") + outcomesForTag.formattedPercentage.withResult("skipped") >
                        </#if>

                        <#assign ignoredbar =     (percentPassing + percentFailing + percentError + percentCompromised + percentIgnored)*150>
                        <#assign compromisedbar = (percentPassing + percentFailing + percentError + percentCompromised)*150>
                        <#assign errorbar =       (percentPassing + percentFailing + percentError)*150>
                        <#assign failingbar =     (percentPassing + percentFailing)*150>
                        <#assign passingbar =     (percentPassing)*150>

                        <#assign successCount = outcomesForTag.totalTests.withResult("success") >
                        <#assign pendingCount = outcomesForTag.totalTests.withResult("pending") >
                        <#assign failureCount = outcomesForTag.totalTests.withResult("failure") >
                        <#assign errorCount = outcomesForTag.totalTests.withResult("error") >
                        <#assign compromisedCount = outcomesForTag.totalTests.withResult("compromised") >
                        <#assign ignoredCount = outcomesForTag.totalTests.withResult("ignored") + outcomesForTag.totalTests.withResult("skipped")>

                        <#assign successStepCount = outcomesForTag.havingResult("success").stepCount >
                        <#assign pendingStepCount = outcomesForTag.havingResult("pending").stepCount >
                        <#assign failureStepCount = outcomesForTag.havingResult("failure").stepCount >
                        <#assign errorStepCount = outcomesForTag.havingResult("error").stepCount >
                        <#assign compromisedStepCount = outcomesForTag.havingResult("compromised").stepCount >
                        <#assign ignoredStepCount = outcomesForTag.havingResult("ignored").stepCount + outcomesForTag.havingResult("skipped").stepCount >

                        <#assign pendingCaption = "${pendingCount} out of ${outcomesForTag.total} tests (${pendingStepCount} steps) pending">
                        <#assign passingCaption = "${successCount} out of ${outcomesForTag.total} tests (${successStepCount} steps) passing">
                        <#assign failingCaption = "${failureCount} out of ${outcomesForTag.total} tests (${failureStepCount} steps) failing">
                        <#assign errorCaption = "${errorCount} out of ${outcomesForTag.total} tests (${errorStepCount} steps) broken">
                        <#assign compromisedCaption = "${compromisedCount} out of ${outcomesForTag.total} tests (${compromisedStepCount} steps) compromised">
                        <#assign ignoredCaption = "${ignoredCount} out of ${outcomesForTag.total} tests (${ignoredStepCount} steps) skipped or ignored">

                        <table>
                            <tr>
                                <td class="related-tag-percentage"><span title="${passingCaption}">${passing}</span></td>
                                <td width="150px">
                                    <a href="${tagReport}">
                                        <div class="pendingbar"
                                             title="${pendingCaption}"
                                             style="width: 150px;">
                                            <div class="ignoredbar"
                                                 style="width: ${ignoredbar?string("0")}px;"
                                                 title="${ignoredCaption}">
                                                <div class="compromisedbar"
                                                     style="width: ${compromisedbar?string("0")}px;"
                                                     title="${compromisedCaption}">
                                                    <div class="errorbar"
                                                         style="width: ${errorbar?string("0")}px;"
                                                         title="${errorCaption}">
                                                        <div class="failingbar"
                                                             style="width: ${failingbar?string("0")}px;"
                                                             title="${failingCaption}">
                                                            <div class="passingbar"
                                                                 style="width: ${passingbar?string("0")}px;"
                                                                 title="${passingCaption}">
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </a>
                                </td>
                                <td class="related-tag-count"><span class="result-test-count" title="${outcomesForTag.total} ${testCountLabel}">${count}</span></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </#foreach>
        </table>
        </#if>
    </#foreach>
</#macro>