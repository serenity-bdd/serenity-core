<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>${testOutcome.unqualified.title}</title>
    <#include "libraries/favicon.ftl">

    <#include "libraries/common.ftl">
    <#include "libraries/jquery-ui.ftl">
    <#include "libraries/datatables.ftl">
    <#include "libraries/imgpreview.ftl">
    <#include "components/report-data.ftl">

</head>

<body class="results-page">

<#include "components/stacktrace.ftl">

<div id="topheader">
    <div id="topbanner">
        <div id="logo"><a href="index.html"><img src="images/serenity-logo.png" border="0"/></a></div>
        <div id="projectname-banner" style="float:right">
            <span class="projectname">
                <span class="projecttitle">${reportOptions.projectName}</span>
                <span class="projectsubtitle">${reportOptions.projectSubTitle}</span>
            </span>
        </div>
    </div>
</div>

<#-- HEADER -->
<div class="middlecontent">
    <div id="contenttop">
        <div class="middlebg">
        <span class="breadcrumbs">
            <a href="index.html" class="breadcrumbs">Home</a>

        <#list breadcrumbs as breadcrumb>
            <#assign breadcrumbReport = absoluteReportName.forRequirement(breadcrumb) />
            <#assign breadcrumbTitle = inflection.of(breadcrumb.shortName).asATitle() >
            > <a href="${breadcrumbReport}">${formatter.htmlCompatibleStoryTitle(breadcrumbTitle)}</a>
        </#list>
        > ${formatter.htmlCompatibleTestTitle(formatter.renderTitle(testOutcome.title))}
        </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
    <#include "menu.ftl">
    <@main_menu selected="home" />
    <div class="clr"></div>

    <#if testOutcome.result == "FAILURE"><#assign outcome_text = "failing-color">
    <#elseif testOutcome.result == "ERROR"><#assign outcome_text = "error-color">
    <#elseif testOutcome.result == "SUCCESS"><#assign outcome_text = "success-color">
    <#elseif testOutcome.result == "PENDING"><#assign outcome_text = "pending-color">
    <#elseif testOutcome.result == "COMPROMISED"><#assign outcome_text = "compromised-color">
    <#else><#assign outcome_text = "ignore-color">
    </#if>

    <#assign title_outcome_icon =  formatter.resultIcon().inLarge().forResult(testOutcome.result) />

    <#-- TEST TITLE -->
    <div id="contentbody">
        <div class="titlebar">
            <div class="story-title">
                <table class="outcome-header">
                    <tr>
                        <td>
                            <#if (parentRequirement?? && parentRequirement.isPresent())>
                                <div>
                                    <#assign parentTitle = formatter.renderDescription(inflection.of(parentRequirement.get().name).asATitle()) >
                                    <#assign parentType = inflection.of(parentRequirement.get().type).asATitle() >
                                    <#if (parentRequirement.get().cardNumber?has_content) >
                                        <#assign issueNumber = "[" + reportFormatter.addLinks(parentRequirement.get().cardNumber) + "]" >
                                    <#else>
                                        <#assign issueNumber = "">
                                    </#if>
                                    <h3 class="discreet-story-header">
                                        <i class="fa fa-2x fa-comments-o"></i>
                                        <span class="story-header-title">${parentTitle} ${issueNumber}</span>
                                    </h3>

                                    <#-- <div class="discreet-requirement-narrative-title">-->
                                    <#-- ${formatter.renderDescription(parentRequirement.get().narrative.renderedTextWithoutTables)}-->
                                    <#-- </div>-->
                                </div>
                            <#elseif (featureOrStory?? && featureOrStory.isPresent())>
                                <div>
                                    <#assign parentTitle = inflection.of(featureOrStory.get().name).asATitle() >
                                    <#assign parentType = inflection.of(featureOrStory.get().type).asATitle() >
                                    <h3 class="discreet-story-header">
                                        <i class="fa fa-2x fa-comments-o"></i>
                                        <span class="story-header-title">${parentTitle}</span>
                                    </h3>
                                    <#if showDetailedStoryDescription!false>
                                        <div class="discreet-requirement-narrative-title">
                                            ${formatter.renderDescription(featureOrStory.get().narrativeSummary)}
                                        </div>
                                    </#if>
                                </div>
                            </#if>

                            <#if (testOutcome.backgroundTitle?has_content)>
                                <div class="requirementNarrative">Background: ${testOutcome.backgroundTitle}
                                    <#if (testOutcome.backgroundTitle?has_content)>
                                        <p>${testOutcome.backgroundDescription}</p>
                                    </#if>
                                </div>
                            </#if>
                        </td>
                        <td valign="top">
                            <#list filteredTags as tag>
                                <#assign tagReport = absoluteReportName.forRequirementOrTag(tag) />
                                <#assign tagTitle = tagInflector.ofTag(tag.type, tag.shortName).toFinalView() >
                                <p class="tag">
                                    <#assign tagStyle = styling.tagStyleFor(tag) >
                                    <span class="badge tag-badge" style="${tagStyle}">
                                    <i class="fa fa-tag"></i>&nbsp;<a class="tagLink" style="${tagStyle}"
                                                                      href="${tagReport}">${formatter.htmlCompatible(tagTitle)}
                                    (${tag.type})</a>
                                </span>
                                </p>
                            </#list>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="story-title">
                <table class="outcome-header">
                    <tr>
                        <td colspan="2" class="test-title-bar">
                            <span class="outcome-icon">${title_outcome_icon}</span>
                            <span class="test-case-title">
                                <#assign testOutcomeTitle = testOutcome.unqualified.titleWithLinks >
                                <span class="${outcome_text!ignore_color}">
                                    ${formatter.htmlCompatibleTestTitle(formatter.renderTitle(testOutcomeTitle))}
                                    <#if (!testOutcome.titleWithIssues)>
                                        <span class="related-issue-title">${testOutcome.formattedIssues}</span>
                                    </#if>
                                </span>
                            </span>
                            <#if (testOutcome.manual)>

                                <#if (testOutcome.lastTested?? && testOutcome.manualTestingUpToDate)>
                                <#-- Last tested and up to date-->
                                    <div class="manual-test-result">
                                        <span class="badge badge-pill badge-info">
                                            <i class="fas fa-user-check"></i> Last tested version: ${testOutcome.lastTested}
                                        </span>
                                        <#if (testOutcome.manualTestEvidence?has_content)>
                                            <br/>
                                            <#list testOutcome.renderedManualTestEvidence as manualEvidence>
                                                <a target="_blank" href="${manualEvidence.link}">
                                                <span class="badge badge-pill badge-primary">
                                                    <i class="fas fa-external-link-alt"></i> ${manualEvidence.label}
                                                </span>
                                                </a>
                                            </#list>
                                        </#if>
                                    </div>

                                <#elseif (testOutcome.lastTested??)>
                                <#-- Last tested out of date-->
                                    <div class="manual-test-result">
                                        <span class="badge badge-pill badge-info">
                                            <i class="fas fa-user-clock"></i> Awaiting new manual test. Last tested version: ${testOutcome.lastTested}</span>
                                        <#if (testOutcome.manualTestEvidence?has_content)>
                                            <br/>
                                            <#list testOutcome.renderedManualTestEvidence as manualEvidence>
                                                <a target="_blank" href="${manualEvidence.link}}">
                                                    <span class="badge badge-pill badge-primary">
                                                        <i class="fas fa-external-link-alt"></i> Test Evidence for previous test
                                                    </span>
                                                </a>
                                            </#list>
                                        </#if>
                                    </div>
                                <#else>
                                <#-- No last tested version specified -->
                                    <i class="fa fa-user manual" alt="Manual test" title="Manual test"></i>
                                </#if>
                            </#if>

                            <#list testOutcome.flags as flag>
                                <i class="fa fa-2x fa-${flag.symbol} flag-color" alt="${flag.message}"
                                   title="${flag.message}"></i>
                            </#list>
                            <#if (testOutcome.descriptionText.isPresent() && testOutcome.descriptionText.get()?has_content)>
                                <div class="discreet-requirement-narrative-title">
                                    <br/>
                                    ${formatter.renderDescription(testOutcome.descriptionText.get())}
                                </div>
                            </#if>
                        </td>
                        <#if (testOutcome.externalLink)??>
                            <td valign="top">
                                <a href="${testOutcome.externalLink.url}" class="tag"
                                   title="${testOutcome.externalLink.type}">
                                    <i class="fa fa-video-camera fa-2x"></i>
                                </a>
                            </td>
                        </#if>
                    </tr>
                </table>
            </div>

            <#if (testOutcome.actors?has_content)>
                <#assign castSize = testOutcome.actors?size>
                <#assign cellWidth = 100 / castSize>

                <!-- CAST MEMBERS -->
                <div>&nbsp;</div>
                <div class="story-title">
                    <table class="outcome-header">
                        <tr>
                            <td>
                                <div>
                                    <h3 class="discreet-story-header">
                                        <i class="fa fa-2x fa-users"></i>
                                        <span class="story-header-title">
                                            <a class="btn btn-primary" data-toggle="collapse" href="#castDetails"
                                               role="button" aria-expanded="true" aria-controls="castDetails">Cast</a>
                                        </span>
                                    </h3>

                                    <div class="cast-member collapse" id="castDetails">
                                        <table class="cast">
                                            <tr>
                                                <#list testOutcome.actors as castMember>
                                                    <td width="${cellWidth}%">
                                                        <h4>
                                                            <i class="fa fa-2x fa-user-o"></i>&nbsp;<span>${castMember.name}</span>
                                                            <h4>
                                                                <p>${formatter.renderDescription(castMember.description!"")}</p>
                                                                <ul>
                                                                    <#if (castMember.hasFacts())>
                                                                        <li><strong>${castMember.name} has:</strong>
                                                                            <ul>
                                                                                <#list castMember.getHas() as fact>
                                                                                    <li>${fact}</li>
                                                                                </#list>
                                                                            </ul>
                                                                        </li>
                                                                    </#if>
                                                                    <#if (castMember.hasAbilities())>
                                                                        <li><strong>${castMember.name} can:</strong>
                                                                            <ul>
                                                                                <#list castMember.can as ability>
                                                                                    <li>${ability}</li>
                                                                                </#list>
                                                                            </ul>
                                                                        </li>
                                                                    </#if>
                                                                </ul>
                                                    </td>
                                                </#list>
                                            </tr>
                                        </table>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
                <!-- END CAST MEMBERS -->
            </#if>


        </div>
    </div>

    <div class="clr"></div>

    <#if (testOutcome.isDataDriven() && testOutcome.dataDrivenSampleScenario?has_content)>
        <div class="story-title">
            <h3 class="story-header">Scenario Outline</h3>

            <div class="scenario">${formatter.formatWithFields(testOutcome.dataDrivenSampleScenario)}</div>

        </div>
    </#if>

    <div id="beforetable"></div>

    <#if (testOutcome.isDataDriven())>

        <#list testOutcome.dataTable.dataSets as dataSet >
            <h3 class="story-header">Examples:<#if dataSet.name??>&nbsp;${dataSet.name}</#if></h3>
            <#if dataSet.description??>
                <div class="requirementNarrative">${dataSet.description}</div>
            </#if>

            <#if dataSet.tags??>

                <p class="example-tag">
                    <#list dataSet.tags as exampleTag>
                        <#assign exampleTagReport = absoluteReportName.forRequirementOrTag(exampleTag) />
                        <#assign exampleTagTitle = inflection.of(exampleTag.shortName).asATitle() >
                        <#assign tagStyle = styling.tagStyleFor(tag) >
                        <span class="badge tag-badge" style="${tagStyle}">
                    <i class="fa fa-tag"></i>&nbsp;<a class="tagLink" style="${tagStyle} style="${tagStyle}"
                                                      href="${exampleTagReport}">${formatter.htmlCompatible(exampleTagTitle)}
                    (${exampleTag.type})</a>
                </span>
                    </#list>
                </p>
            </#if>

            <div class="example-table test-report">
                <table class="table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <#list testOutcome.dataTable.headers as header>
                            <th>${inflection.of(header).asATitle()}</th>
                        </#list>
                    </tr>
                    </thead>
                    <tbody>
                    <#assign rowIndex = dataSet.startRow >
                    <#list dataSet.rows as row>
                        <tr>
                            <td class="test-${row.result}"><a href="#${rowIndex}">${rowIndex + 1}</a></td>
                            <#list row.values as value>
                                <#if testOutcome.manual>
                                    <#assign roeResult = testOutcome.result/>
                                <#else>
                                    <#assign roeResult = row.result/>
                                </#if>
                                <td class="test-${roeResult}"><a
                                            href="#${rowIndex}">${formatter.plainHtmlCompatible(value)}</a>
                                </td>
                            </#list>
                        </tr>
                        <#assign rowIndex = rowIndex + 1 >
                    </#list>
                    </tbody>
                </table>
            </div>
        </#list>
    </#if>

    <div id="tablecontents">
        <div>
            <table class="step-table">
                <#-- TABLE HEADER -->
                <tr class="step-titles">
                    <th width="65">
                        <#if (testOutcome.manual)>
                            <i class="fa fa-user fa-2x" title="Manual test"></i>
                        </#if>
                    </th>
                    <th class="step-description-column greentext"><#if (testOutcome.manual)>Manual </#if>
                        Steps
                    </th>

                    <#if testOutcome.hasScreenshots()>
                        <th width="150" class="greentext">Screenshots</th>
                    </#if>
                    <th width="130" class="greentext">Outcome</th>
                    <th width="100" class="greentext"><i title="Duration" class="far fa-clock"></i></th>
                </tr>
                <tr class="step-table-separator">
                    <td colspan="5"></td>
                </tr>

                <#-- STEPS BREAKDOWN-->
                <#assign level = 1>
                <#assign screenshotCount = 0>
                <#macro write_step(step, step_number)>
                    <@step_details step=step step_number=step_number level=level/>
                    <#if step.isAGroup()>
                        <tr>
                            <td colspan="5">
                                <table id="stepSection${step_number}" class="step-table-nested" style="display:none;">

                                    <#assign level = level + 1>
                                    <#list step.children as nestedStep>
                                        <#if step.isAGroup() >
                                            <@write_step step=nestedStep step_number=step_number + "-" + nestedStep_index/>
                                        </#if>
                                    </#list>
                                    <#assign level = level-1>
                                    <#assign screenshotCount = screenshotCount + step.screenshotCount>

                                </table>
                            </td>
                        </tr>
                    </#if>
                </#macro>

                <#macro restQueryData(restQuery, number) >
                    <span>
                        <button type="button" class="btn btn-success btn-sm" data-toggle="collapse"
                                data-target="#restModal-${number}">
                            REST Query
                        </button>
                    </span>
                    <!-- Modal -->
                    <div class="rest-query-details">
                        <div class="collapse multi-collapse" id="restModal-${number}">
                            <div class="card">
                                <div class="card-body">
                                    <h4>Response</h4>
                                    <#if restQuery.statusCode?has_content>
                                        <p>Status code: ${restQuery.statusCode}</p>
                                    </#if>
                                    <#if restQuery.contentType?has_content>
                                        <p>Content Type: ${restQuery.contentType}</p>
                                    </#if>
                                    <#if restQuery.requestHeaders?has_content>
                                        <h4>Request Headers</h4>
                                        <pre>${(formatter.renderHeaders(restQuery.requestHeaders))!}</pre>
                                    </#if>
                                    <#if restQuery.content?has_content>
                                        <h4>Content Body</h4>
                                        <pre>${(formatter.renderText(restQuery.content))!}</pre>
                                    </#if>
                                    <#if restQuery.requestCookies?has_content>
                                        <h4>Request Cookies</h4>
                                        <pre>${(formatter.renderText(restQuery.requestCookies))!}</pre>
                                    </#if>
                                    <#if restQuery.responseHeaders?has_content>
                                        <h4>Response Headers</h4>
                                        <pre>${(formatter.renderHeaders(restQuery.responseHeaders))!}</pre>
                                    </#if>
                                    <h4>Response Body</h4>
                                    <#if restQuery.responseHeaders?has_content>
                                        <pre>${formatter.renderText(restQuery.responseBody)}</pre>
                                    </#if>
                                    <#if restQuery.responseCookies?has_content && (!(restQuery.requestCookies?has_content) || restQuery.responseCookies!=restQuery.requestCookies)>
                                        <h4>Response Cookies</h4>
                                        <pre>${(formatter.renderText(restQuery.responseCookies))!}</pre>
                                    </#if>
                                </div>
                            </div>
                        </div>
                    </div>
                </#macro>

                <#macro step_details(step, step_number, level)>
                    <#assign step_outcome_icon = formatter.resultIcon().forResult(step.result) />
                    <#assign step_outcome_style = formatter.resultIcon().colorFor(step.result) />
                    <#assign step_icon_size = 20>
                    <#if (level>1)>
                        <#if step.isAGroup()>
                            <#assign step_class_root = "nested">
                        <#else>
                            <#assign step_class_root = "nested-group">
                        </#if>
                    <#else>
                        <#assign step_class_root = "top-level">
                    </#if>
                    <#assign step_indent = level*20>
                    <#if step.isAGroup()>
                        <#assign showAccordion = true/>
                    <#else>
                        <#assign showAccordion = false/>
                    </#if>
                    <tr class="test-${step.result}">

                        <#-- ICON -->
                        <td width="${step_indent + 30}" class="step-icon">
                            <#if step_number?has_content><a name="${step_number}"></a></#if>
                            <#if showAccordion>
                                <a href="javaScript:void(0)" onClick="toggleDiv('stepSection${step_number}')"
                                   style="display:block">
                                    <#--${step_outcome_icon}-->
                                    <i class="fa fa-plus-square-o imgstepSection${step_number} ${step_outcome_style}"
                                       style="margin-left: ${step_indent}px; float:left; padding-right:5px"></i>
                                    <#--<img src="images/plus.png" width="24" class="imgstepSection${step_number}"-->
                                    <#--style="margin-left: 20px; float:left;  padding-right:5px"/>-->
                                </a>
                            <#else>
                                <span style="margin-left: ${step_indent}px;"
                                      class="${step_class_root}-icon">${step_outcome_icon}
                                </span>
                            <#--<img style="margin-left: ${step_indent}px; margin-right: 5px;"-->
                            <#--src="images/${step_outcome_icon}" class="${step_class_root}-icon"/>-->
                            </#if>
                        </td>

                        <#-- DESCRIPTION -->
                        <td class="step-description-column">
                            <div class="step-description">
                                <#if showAccordion>
                                <a href="javaScript:void(0)" onClick="toggleDiv('stepSection${step_number}')"
                                   style="display:block;">
                                    </#if>
                                    <span class="${step_class_root}-step">
                                            <#if step.hasRestQuery()>
                                                ${formatter.restQuery(step.description)}
                                            <#else>
                                                ${formatter.formatWithFields(step.description)}
                                            </#if>
                                        </span>
                                    <#if showAccordion>
                                </a>
                                </#if>
                                <span class="evidence">
                                    <#if step.hasRestQuery()>
                                        <span class="piece-of-evidence">
                                            <#assign restDataNumber = "REST-${step.number}">
                                            <@restQueryData restQuery=step.restQuery number=restDataNumber />
                                        </span>
                                    </#if>
                                    <#if step.hasData()>
                                        <#list step.reportData as recordedData>
                                            <#assign stepIndex=recordedData?index>
                                            <#assign restDataNumber = "EVIDENCE-${step.number}-${stepIndex}">
                                            <span class="piece-of-evidence">
                                                <@reportData reportData=recordedData number=restDataNumber />
                                            </span>
                                        </#list>
                                    </#if>
                                </span>
                                <#if (step.externalLink)??>
                                    <a href="${step.externalLink.url}" class="tag" title="${step.externalLink.type}">
                                        <i class="fa fa-video-camera fa-1x"></i>
                                    </a>
                                </#if>
                            </div>
                        </td>

                        <#-- SCREENSHOTS -->
                        <#if testOutcome.hasScreenshots()>
                            <td width="160" class="${step.result}-text">
                                <#if step.hasMultipleScreenshots() >
                                    <a href="${relativeLink!}${testOutcome.screenshotReportName}.html#screenshots?screenshot=${screenshotCount}">
                                        <img src="${step.earliestScreenshot.filename}"
                                             href="${step.earliestScreenshot.filename}"
                                             class="screenshot"
                                             width="48" height="48"/>
                                    </a>
                                    <i class="fas fa-arrow-right"></i>
                                </#if>

                                <#if step.latestScreenshot?has_content>
                                    <#assign actualScreenshotCount = screenshotCount + step.actualScreenshotCount />
                                    <a href="${relativeLink!}${testOutcome.screenshotReportName}.html#screenshots?screenshot=${actualScreenshotCount}">
                                        <img src="${step.latestScreenshot.filename}"
                                             href="${step.latestScreenshot.filename}"
                                             class="screenshot"
                                             width="48" height="48"/>
                                    </a>
                                    <#if step.hasChildren()>
                                        <#assign screenshotCount = screenshotCount + 1 />
                                    <#else>
                                        <#assign screenshotCount = screenshotCount + step.screenshotCount />
                                    </#if>
                                </#if>
                            </td>
                        </#if>
                        <#-- OUTCOME & TIME -->
                        <td width="130"><span class="${step_class_root}-step">${step.result}</span></td>
                        <td width="100"><span class="${step_class_root}-step">${step.durationInSeconds}s</span></td>
                    </tr>
                    <#if (step.errorMessage?has_content) && !step.hasNestedErrors()>
                        <tr class="test-${step.result}">
                            <td width="40">&nbsp</td>
                            <#if step.errorMessage?has_content>
                                <#assign errorMessageTitle = step.errorMessage?html>
                            <#else>
                                <#assign errorMessageTitle = "">
                            </#if>
                            <#if testOutcome.hasScreenshots()>
                            <td width="%" colspan="4" class="error-message-cell">
                                <#else>
                            <td width="%" colspan="3" class="error-message-cell">
                                </#if>

                                <#assign formattedErrorMessageTitle = formatter.htmlAttributeCompatible(errorMessageTitle, true) />

                                <#if step.nestedException?has_content>
                                    <@stacktrace title=formattedErrorMessageTitle cause=step.nestedException id=step.number />
                                <#else>
                                    <div class="error-message"
                                         title='${formatter.htmlAttributeCompatible(errorMessageTitle)}'>
                                        <pre>${formatter.htmlAttributeCompatible(errorMessageTitle,244)!''}</pre>
                                    </div>
                                </#if>
                            </td>
                        </tr>
                    </#if>
                </#macro>

                <#-- Test step results -->
                <#list testOutcome.testSteps as step>
                    <@write_step step=step step_number=step_index />
                </#list>
                <#if testOutcome.hasNonStepFailure()>
                    <#assign step_outcome_icon = formatter.resultIcon().forResult(testOutcome.result) />
                    <tr class="test-${testOutcome.result}">
                        <td width="40">${step_outcome_icon}</td>
                        <#if testOutcome.hasScreenshots()>
                        <td width="%" colspan="2">
                            <#else>
                        <td width="%" colspan="1">
                            </#if>
                            <#if testOutcome.errorMessage?has_content>
                                <span class="top-level-step">${testOutcome.errorMessage}</span>
                            <#else>
                                <span class="top-level-step">An error occurred outside of step execution</span>
                            </#if>
                        </td>
                        <td width="130"><span
                                    class="top-level-step">${formatter.htmlCompatibleStepDescription(testOutcome.result)}</span>
                        </td>
                        <td width="100"><span class="top-level-step">${testOutcome.durationInSeconds}s</span></td>
                    </tr>
                    <tr class="test-${testOutcome.result}">
                        <td width="40">&nbsp</td>
                        <#if testOutcome.hasScreenshots()>
                        <td width="%" colspan="4">
                            <#else>
                        <td width="%" colspan="3">
                            </#if>
                            <#if (testOutcome.errorMessage)??>
                                <#if (testOutcome.nestedTestFailureCause)??>
                                    <#assign formattedErrorMessageTitle = formatter.htmlAttributeCompatible(testOutcome.errorMessage, true) />

                                    <@stacktrace title=formattedErrorMessageTitle cause=testOutcome.nestedTestFailureCause id="overall" />
                                </#if>
                            </#if>
                        </td>
                    </tr>
                </#if>
                <tr class="test-${testOutcome.result}">
                    <#if testOutcome.hasScreenshots()>
                        <td colspan="3"></td>
                    <#else>
                        <td colspan="2"></td>
                    </#if>
                    <td width="130"><span class="top-level-step"><em>${testOutcome.result}</em></span></td>
                    <td width="100"><span class="top-level-step"><em>${testOutcome.durationInSeconds}s</em></span></td>
                </tr>

            </table>
        </div>
    </div>

    <div id="beforefooter"></div>


    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-12">
                <span class="version">Serenity BDD version ${serenityVersionNumber!"SNAPSHOT-BUILD"}</span>
            </div>
        </div>
    </div>

    <script type="text/javascript">
        function toggleDiv(divId) {
            $("#" + divId).toggle();
            var imgsrc = $(".img" + divId).attr('src');
            if (imgsrc == 'images/plus.png') {
                $(".img" + divId).attr("src", function () {
                    return "images/minus.png";
                });

            } else {
                $(".img" + divId).attr("src", function () {
                    return "images/plus.png";
                });
            }
        }
    </script>

    <#--<script type="text/javascript">-->
    <#--$('.example-table table').DataTable({-->
    <#--// "order": [[0, "asc"]],-->
    <#--"pageLength": 50,-->
    <#--"searching": false,-->
    <#--"ordering":  false,-->
    <#--"scrollX": "100%",-->
    <#--"scrollXInner": "100%",-->
    <#--"scrollCollapse": true-->
    <#--});-->
    <#--</script>-->

    <script type="text/javascript">
        //<![CDATA[

        $(document).ready(function () {
            $('img.screenshot').imgPreview({
                imgCSS: {
                    width: '500px'
                },
                distanceFromCursor: {top: 10, left: -200}
            });
        });

        //]]>
    </script>
    <div id="imgPreviewContainer" style="position: absolute; top: 612px; left: 355px; display: none; " class=""><img
                src="" style="display: none; "></div>
    <div id="imgPreviewContainer2" style="position: absolute; top: 925px; left: 320px; display: none; " class="">
        <img style="width: 200px; display: none; " src=""></div>
    <div id="imgPreviewWithStyles" style="position: absolute; top: 1272px; left: 321px; display: none; " class="">
        <img style="height: 200px; opacity: 1; display: none; " src=""></div>
    <div id="imgPreviewWithStyles2" style="display: none; position: absolute; "><img style="height: 200px; "></div>
    <div id="imgPreviewWithStyles3" style="display: none; position: absolute; "><img style="height: 200px; "></div>

</div>
</body>
</html>
