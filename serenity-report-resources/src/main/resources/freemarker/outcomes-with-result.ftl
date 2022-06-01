<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Serenity Reports</title>

    <#include "libraries/favicon.ftl">

    <#include "libraries/common.ftl">
    <#include "libraries/jquery-ui.ftl">
    <#include "libraries/datatables.ftl">

    <#include "components/tag-list.ftl">
    <#include "components/test-outcomes.ftl">
    <#include "components/result-chart.ftl">
    <#include "components/result-summary.ftl">
    <#include "components/duration-chart.ftl">
    <#include "components/functional-coverage-chart.ftl">
    <#include "components/tag_cloud.ftl">

<#assign manualTests = testOutcomes.count("manual")>
<#assign automatedTests = testOutcomes.count("automated")>
<#assign totalTests = testOutcomes.count("automated")>

<#assign testResultData = resultCounts.byTypeFor("success","pending","ignored","skipped","aborted","failure","error","compromised") >
<#assign testLabels = resultCounts.percentageLabelsByTypeFor("success","pending","ignored","skipped","aborted","failure","error","compromised") >
<#assign graphType="automated-and-manual-results"/>

<#assign successfulManualTests = (manualTests.withResult("SUCCESS") > 0)>
<#assign pendingManualTests = (manualTests.withResult("PENDING") > 0)>
<#assign ignoredManualTests = (manualTests.withResult("IGNORED") > 0)>
<#assign abortedManualTests = (manualTests.withResult("ABORTED") > 0)>
<#assign failingManualTests = (manualTests.withResult("FAILURE") > 0)>

    <script class="code" type="text/javascript">$(document).ready(function () {


        $('.scenario-result-table').DataTable({

            "order": [[0, "asc",], [3, "asc",]],
            "pageLength": 25,
            "language": {
                searchPlaceholder: "Filter",
                search: ""
            }
        });

        // Results table
        $('#test-results-table').DataTable({
            "order": [[0, "asc",], [3, "asc",]],
            "pageLength": 100,
            "lengthMenu": [[25, 50, 100, 200, -1], [25, 50, 100, 200, "All"]]
        });

    })
    ;
    </script>
</head>

<body class="results-page">
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

<div class="middlecontent">

<#assign tagsTitle = 'Related Tags' >
<#if (testOutcomes.label == '')>
    <#assign resultsContext = ''>
    <#assign pageTitle = 'Test Results: All Tests' >
<#else>
    <#assign resultsContext = '> ' + testOutcomes.label>

    <#if (currentTagType! != '')>
        <#assign pageTitle = "<i class='bi bi-tags'></i> " + inflection.of(currentTagType!"").asATitle() + ': ' +  inflection.of(testOutcomes.label).asATitle() >
    <#else>
        <#assign pageTitle = inflection.of(testOutcomes.label).asATitle() >
    </#if>
</#if>
    <div id="contenttop">
    <#--<div class="leftbg"></div>-->
        <div class="middlebg">
        <span class="breadcrumbs"><a href="index.html">Home</a>
        <#if (parentTag?has_content && parentTag.name! != '')>
            <#assign titleContext = " (with " + inflection.of(parentTag.type!"").asATitle() + " " + inflection.of(parentTag.name!"").asATitle() + ")" >
        <#else>
            <#assign titleContext = "" >
        </#if>
        <#if (breadcrumbs?has_content)>
            <#list breadcrumbs as breadcrumb>
                <#assign breadcrumbReport = absoluteReportName.forRequirementOrTag(breadcrumb) />
                <#assign breadcrumbTitle = inflection.of(breadcrumb.displayName).asATitle() >
                <#assign breadcrumbType = inflection.of(breadcrumb.type).asATitle() >
                > <a href="${breadcrumbReport}" title="${breadcrumbTitle} (breadcrumbType)">
                    <#--${formatter.htmlCompatible(breadcrumbTitle)}-->
                        ${formatter.htmlCompatibleStoryTitle(breadcrumbTitle)}
                </a>
            </#list>
        <#else>
            <#if currentTagType?has_content>
                > ${inflection.of(currentTagType!"").asATitle()} ${titleContext}
            </#if>
        </#if>
            <#if testOutcomes.label?has_content>
            <#--> ${formatter.truncatedHtmlCompatible(inflection.of(testOutcomes.label).asATitle(),60)}-->
                > <span
                    class="truncate-60">${formatter.htmlCompatibleStoryTitle(inflection.of(testOutcomes.label).asATitle())}</span>
            </#if>
        </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>
    <!--/* starts second table*/-->
<#include "menu.ftl">
<@main_menu selected="home" />
    <div class="clr"></div>
    <div id="beforetable"></div>
    <div id="results-dashboard">
        <div class="middlb">
            <div class="table">

                <h2>${pageTitle}</h2>
                <table class='overview'>
                    <tr>
                        <td width="375px" valign="top">
                            <div class="test-count-summary">
                                <div>
                                    <#assign scenarioLabel = inflection.of(testOutcomes.totalTestScenarios).times("scenario").inPluralForm().toString() >

                                ${testOutcomes.totalMatchingScenarios} ${testOutcomes.resultTypeLabel}
                                    across ${testOutcomes.totalTestScenarios} ${scenarioLabel}

                                    <#if (csvReport! != '')> |
                                        <a href="${csvReport}" title="Download CSV"> <i class="bi bi-cloud-arrow-down" title="Download CSV"></i></a>
                                    </#if>

                                    <#if testOutcomes.resultFilterName != 'SUCCESS'>
                                    <p class="report-info"><i class="bi bi-info-circle"></i> Note that results include
                                        data-driven scenarios containing ${testOutcomes.resultTypeLabel} ,
                                        which may also contain results other than ${testOutcomes.resultTypeLabel} .</p>
                                    </#if>

            <#assign successReport = reportName.withPrefix(currentTag).forTestResult("success") >
            <#assign brokenReport = reportName.withPrefix(currentTag).forTestResult("broken") >
            <#assign failureReport = reportName.withPrefix(currentTag).forTestResult("failure") >
            <#assign errorReport = reportName.withPrefix(currentTag).forTestResult("error") >
            <#assign compromisedReport = reportName.withPrefix(currentTag).forTestResult("compromised") >
            <#assign pendingReport = reportName.withPrefix(currentTag).forTestResult("pending") >
            <#assign skippedReport = reportName.withPrefix(currentTag).forTestResult("skipped") >
            <#assign abortedReport = reportName.withPrefix(currentTag).forTestResult("aborted") >
            <#assign ignoredReport = reportName.withPrefix(currentTag).forTestResult("ignored") >

            <#assign totalCount   = testOutcomes.totalScenarios.total >
            <#assign successCount = testOutcomes.totalScenarios.withResult("success") >
            <#assign pendingCount = testOutcomes.totalScenarios.withResult("pending") >
            <#assign ignoredCount = testOutcomes.totalScenarios.withResult("ignored") >
            <#assign skippedCount = testOutcomes.totalScenarios.withResult("skipped") >
            <#assign abortedCount = testOutcomes.totalScenarios.withResult("aborted") >
            <#assign failureCount = testOutcomes.totalScenarios.withResult("failure") >
            <#assign errorCount   = testOutcomes.totalScenarios.withResult("error") >
            <#assign brokenCount  = failureCount + errorCount >
            <#assign compromisedCount = testOutcomes.totalScenarios.withResult("compromised") >
            <#assign badTestCount  = failureCount + errorCount + compromisedCount>

                <#if testOutcomes.haveFlags()>
                    <span class="test-count"> |
                        <#list testOutcomes.flags as flag>
                            <#assign flagTitle = inflection.of(flag.message).inPluralForm().asATitle() >
                            <#assign flagTag = "flag_${inflection.of(flag.message).asATitle()}" >
                            <#assign flagReport = reportName.forTag(flagTag) >
                            <#assign flagCount = testOutcomes.flagCountFor(flag)>
                            <i class="bi bi-${flag.symbol} flag-color" alt="${flag.message}"
                               title="${flag.message}"></i> <a href="${flagReport}">${flagTitle}</a> (${flagCount})
                        </#list>
                    </span>
                </#if>

                                </div>
                            </div>

                            <div>
                                <ul class="nav nav-tabs">
                                    <li class="active">
                                        <a data-toggle="tab" href="#summary"><i class="bi bi-house-door"></i> Summary</a>
                                    </li>
                                    <li>
                                        <a data-toggle="tab" href="#tests"><i class="bi bi-speedometer"></i> Test
                                            Results</a>
                                    </li>
                                </ul>

                                <div class="card border">
                                    <div class="tab-content" id="pills-tabContent">
                                        <div id="summary" class="tab-pane fade in active">
                                            <div class="container-fluid">
                                                <div class="row">
                                                    <div class="col-sm-4">
                                                        <div>
                                                            <h3><i class="bi bi-speedometer2"></i> Key Statistics</h3>
                                                            <div>
                                                                <table class="table table-striped table-hover">
                                                                    <tbody>
                                                                    <tr scope="row">
                                                                        <td>
                                                                            <i class="bi bi-card-checklist"></i> Number of Scenarios
                                                                        </td>
                                                                        <td>${testCount}</td>
                                                                    </tr>
                                                                    <tr scope="row">
                                                                        <td>
                                                                            <i class="bi bi-caret-right"></i> Number of Test Cases
                                                                        </td>
                                                                        <td>${scenarioCount}</td>
                                                                    </tr>
                                                                    <tr scope="row">
                                                                        <td>
                                                                            <i class="bi bi-flag-fill"></i> Tests Started
                                                                        </td>
                                                                        <td>${startTimestamp}</td>
                                                                    </tr>
                                                                    <tr scope="row">
                                                                        <td>
                                                                            <i class="bi bi-stop-circle"></i> Tests Finished
                                                                        </td>
                                                                        <td>${endTimestamp}</td>
                                                                    </tr>
                                                                    <tr scope="row">
                                                                        <td>
                                                                            <i class="bi bi-stopwatch"></i> Total Duration
                                                                        </td>
                                                                        <td>${totalClockDuration}</td>
                                                                    </tr>
                                                                    <tr scope="row">
                                                                        <td>
                                                                            <i class="bi bi-trophy"></i> Fastest Test
                                                                        </td>
                                                                        <td>${minTestDuration}</td>
                                                                    </tr>
                                                                    <tr scope="row">
                                                                        <td>
                                                                            <i class="bi bi-skip-start"></i> Slowest Test
                                                                        </td>
                                                                        <td>${maxTestDuration}</td>
                                                                    </tr>
                                                                    <tr scope="row">
                                                                        <td>
                                                                            <i class="bi bi-stopwatch"></i> Average Execution Time
                                                                        </td>
                                                                        <td>${averageTestDuration}</td>
                                                                    </tr>
                                                                    <tr scope="row">
                                                                        <td>
                                                                            <i class="bi bi-stopwatch-fill"></i> Total Execution Time
                                                                        </td>
                                                                        <td>${totalTestDuration}</td>
                                                                    </tr>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="col-sm-4">
                                                        <h4><i class="bi bi-check-square"></i> Test Outcomes</h4>
                                                        <!-- Severity bar chart -->
                                                        <div class="chart-container" style="position: relative; width:30vw">
                                                            <canvas id="severityChart" width="300" height="300"></canvas>
                                                        </div>
                                                    </div>

                                                    <div class="col-sm-4">
                                                        <h4><i class="bi bi-graph-up"></i> Test Performance</h4>

                                                        <!-- Duration bar chart -->
                                                        <div class="chart-container" style="position: relative; width:30vw">
                                                            <canvas id="durationChart" width="300" height="300"></canvas>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="container-fluid">
                                                <div class="row">
                                                    <#if coverage?has_content>
                                                        <#assign featureType = inflection.of(coverage[0].tagType).inPluralForm().asATitle()/>
                                                        <div class="col-sm-12">
                                                            <!-- High level coverage bar chart -->
                                                            <div class="chart-container" style="position: relative; width:60vw">
                                                                <h3><i class="bi bi-reception-3"></i> Functional Coverage Overview</h3>
                                                                <h4>${featureType}</h4>
                                                                <canvas id="coverageChart"></canvas>
                                                            </div>
                                                        </div>
                                                    </#if>
                                                </div>
                                                <#if tagResults?has_content >
                                                    <div class="row">
                                                        <div class="col-sm-12">
                                                            <h3>Tags</h3>

                                                            <#list tagResults as tagResultGroup >
                                                                <div class="card">
                                                                    <div class="card-body">
                                                                        <#if tagResultGroup.tagType?has_content>
                                                                            <h5 class="card-title">${inflection.of(tagResultGroup.tagType).asATitle()}</h5>
                                                                        </#if>
                                                                        <div>
                                                                            <#list tagResultGroup.tagResults as tagResult >
                                                                                <a href="${tagResult.report}">
                                                                                    <span class="badge" style="background-color:${tagResult.color}; margin:1em;padding:4px;">
                                                                                        <i class="bi bi-tag"></i> ${tagInflector.ofTag(tagResult.tag.type, tagResult.tag.name).toFinalView()}&nbsp;&nbsp;&nbsp;${tagResult.count}
                                                                                    </span>
                                                                                </a>
                                                                            </#list>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </#list>
                                                        </div>
                                                    </div>
                                                </#if>
                                            </div>

                                            <div class="container-fluid">
                                                <#if coverage?has_content>
                                                    <div class="row">
                                                        <div class="col-sm-11">
                                                            <h3>Functional Coverage Details</h3>

                                                            <#list coverage as tagCoverageByType>
                                                                <#if tagCoverageByType.tagCoverage?has_content>
                                                                    <#if tagCoverageByType.tagCoverage?size <= 10>
                                                                        <#assign coverageTableClass="feature-coverage-table">
                                                                    <#else>
                                                                        <#assign coverageTableClass="feature-coverage-table-with-pagination">
                                                                    </#if>

                                                                    <#assign sectionTitle = inflection.of(tagCoverageByType.tagType).inPluralForm().asATitle() >
                                                                    <h4>${inflection.of(tagCoverageByType.tagType).inPluralForm().asATitle()}</h4>

                                                                    <table class="table ${coverageTableClass}" id="${tagCoverageByType.tagType}">
                                                                        <thead>
                                                                        <tr>
                                                                            <th>${formatter.humanReadableFormOf(tagCoverageByType.tagType)}</th>
                                                                            <th style="width:1em;">Test&nbsp;Cases</th>
                                                                            <th style="width:1em;">Scenarios</th>
                                                                            <th style="width:1em;">%&nbsp;Pass</th>
                                                                            <th style="width:1em;">Result</th>
                                                                            <th>Coverage</th>
                                                                        </tr>
                                                                        </thead>
                                                                        <#assign tagCoverageEntries = tagCoverageByType.tagCoverage />
                                                                        <tbody>
                                                                        <#list tagCoverageEntries as tagCoverage>
                                                                            <#if (!hideEmptyRequirements || tagCoverage.testCount != 0)>
                                                                                <tr>
                                                                                    <td>
                                                                                        <#if (!tagCoverageByType.featureNamesAreUnique && tagCoverage.parentName?has_content) >
                                                                                            <#assign displayedTagName = tagCoverage.parentName + " > " + tagCoverage.tagName />
                                                                                        <#else>
                                                                                            <#assign displayedTagName = tagCoverage.tagName />
                                                                                        </#if>
                                                                                        <#if tagCoverage.testCount = 0>
                                                                                            ${displayedTagName}
                                                                                        <#else>
                                                                                            <a href="${tagCoverage.report}" > ${displayedTagName}</a>
                                                                                        </#if>
                                                                                    </td>
                                                                                    <td>${tagCoverage.testCount}</td>
                                                                                    <td>${tagCoverage.scenarioCount}</td>
                                                                                    <td>${tagCoverage.successRate}</td>
                                                                                    <td>
                                                                                        <#if tagCoverage.testCount = 0>
                                                                                            <i class="bi bi-hourglass-top pending-icon"></i>
                                                                                        <#else>
                                                                                            ${tagCoverage.resultIcon}
                                                                                        </#if>
                                                                                    </td>
                                                                                    <td>
                                                                                        <div class="progress">
                                                                                            <#list tagCoverage.coverageSegments as coverageSegment>
                                                                                                <div class="progress-bar"
                                                                                                     role="progressbar"
                                                                                                     style="width: ${coverageSegment.percentage}%; background-color: ${coverageSegment.color}"
                                                                                                     aria-valuenow="${coverageSegment.count}"
                                                                                                     title="${coverageSegment.title}"
                                                                                                     aria-valuemin="0"
                                                                                                     aria-valuemax="100">
                                                                                                </div>
                                                                                            </#list>
                                                                                        </div>
                                                                                    </td>
                                                                                </tr>
                                                                            </#if>
                                                                        </#list>
                                                                        </tbody>
                                                                    </table>
                                                                </#if>
                                                            </#list>
                                                        </div>
                                                    </div>
                                                </#if>

                                                <#if badTestCount != 0>
                                                    <div class="row">
                                                        <div class="col-lg-6 col-md-6 col-sm-12">
                                                            <h3>Test Failure Overview</h3>
                                                        </div>
                                                    </div>
                                                    <div class="row">
                                                        <div class="col-lg-6 col-md-6 col-sm-12">
                                                            <h4>Most Frequent Failures</h4>
                                                            <table class="table" style="width:40vw;">
                                                                <tbody>
                                                                <#list frequentFailures as frequentFailure>
                                                                    <tr>
                                                                        <td class="${frequentFailure.resultClass}-color top-list-title">
                                                                            <a href="${frequentFailure.report}">${frequentFailure.resultIcon} ${frequentFailure.name}</a>
                                                                        </td>
                                                                        <td><span
                                                                                    class="badge failure-badge">${frequentFailure.count}</span>
                                                                        </td>
                                                                    </tr>
                                                                </#list>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                        <div class="col-lg-6 col-md-6 col-sm-12">
                                                            <h4>Most Unstable Features</h4>
                                                            <table class="table" style="width:40vw;">
                                                                <tbody>
                                                                <#list unstableFeatures as unstableFeature>
                                                                    <tr>
                                                                        <td class="failure-color top-list-title">
                                                                            <a href="${unstableFeature.report}">${unstableFeature.name}</a>
                                                                        </td>
                                                                        <td>
                                                                            <span class="badge failure-badge">${unstableFeature.failurePercentage}%</span>
                                                                        </td>
                                                                    </tr>
                                                                </#list>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </#if>
                                            </div>
                                            <div class="container-fluid">
                                                <@tag_cloud />
                                            </div>
                                        </div>
                                        <div id="tests" class="tab-pane fade in">
                                            <div class="container-fluid">
                                                <div class="row">
                                                    <div class="col-sm-12">
                                                        <h3><i class="bi bi-gear"></i> Automated Tests</h3>

                                            <#if (automatedTestCases?has_content)>
                                            <table class="scenario-result-table table" id="scenario-results">
                                                <thead>
                                                <tr>
                                                    <th>${leafRequirementType}</th>
                                                    <th class="test-name-column">Scenario</th>
                                                    <th>Steps</th>
                                                    <th>Started</th>
                                                    <th>Duration</th>
                                                    <th>Result</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <#list automatedTestCases as scenario>
                                                    <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />
                                                <tr class="scenario-result ${scenario.result}">
                                                    <td>
                                                        <#if scenario.parentName?has_content>
                                                            <a href="${scenario.parentReport}">${scenario.parentName}</a>
                                                        </#if>
                                                    </td>
                                                    <td>
                                                        <a href="${scenario.scenarioReport}">${scenario.title}</a>
                                                               <#if scenario.hasExamples() >
                                                                   (${scenario.numberOfExamples})
                                                               </#if>
                                                    </td>
                                                    <td>${scenario.stepCount}</td>
                                                    <td>${scenario.formattedStartTime}</td>
                                                    <td>${scenario.formattedDuration}</td>
                                                    <td>${outcome_icon} <span
                                                            style="display:none">${scenario.result}</span></td>
                                                </tr>
                                                </#list>
                                                </tbody>
                                            </table>
                                            <#else>
                                            No automated tests were executed
                                            </#if>

                                                    </div>
                                                </div>

                                                <div class="row">
                                                    <div class="col-sm-12">
                                                        <h3><i class="bi bi-hand-index-thumb"></i> Manual Tests</h3>

                                            <#if (manualTestCases?has_content)>
                                            <table class="scenario-result-table table" id="manual-scenario-results">
                                                <thead>
                                                <tr>
                                                    <th>${leafRequirementType}</th>
                                                    <th class="test-name-column" style="width:60em;">Scenario</th>
                                                    <th>Steps</th>
                                                    <th>Result</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <#list manualTestCases as scenario>
                                                    <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />
                                                <tr>
                                                    <td>
                                                        <#if scenario.parentName?has_content>
                                                            <a href="${scenario.parentReport}">${scenario.parentName}</a>
                                                        </#if>
                                                    </td>
                                                    <td>
                                                        <a href="${scenario.scenarioReport}">${scenario.title}</a>
                                                        <#if scenario.hasExamples() >
                                                           (${scenario.numberOfExamples})
                                                        </#if>
                                                    </td>
                                                    <td>${scenario.stepCount}</td>
                                                    <td>${outcome_icon} <span
                                                            style="display:none">${scenario.result}</span></td>
                                                </tr>
                                                </#list>
                                                </tbody>
                                            </table>
                                            <#else>
                                                No manual tests were recorded
                                            </#if>

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
            </div>
        </div>
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

<!-- Chart data -->
<@result_chart id='resultChart' />
<@result_summary id='severityChart' />
<@duration_chart id='durationChart' />

<#if coverage?has_content>
    <@coverage_chart id='coverageChart' feature=coverage[0]  />
</#if>

</body>
</html>
