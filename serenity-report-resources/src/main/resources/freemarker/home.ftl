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


    <#assign manualTests = testOutcomes.count("manual")>
    <#assign automatedTests = testOutcomes.count("automated")>
    <#assign totalTests = testOutcomes.count("automated")>

    <#assign testResultData = resultCounts.byTypeFor("success","pending","ignored","skipped","failure","error","compromised") >
    <#assign testLabels = resultCounts.percentageLabelsByTypeFor("success","pending","ignored","skipped","failure","error","compromised") >
    <#assign graphType="automated-and-manual-results"/>

    <#assign successfulManualTests = (manualTests.withResult("SUCCESS") > 0)>
    <#assign pendingManualTests = (manualTests.withResult("PENDING") > 0)>
    <#assign ignoredManualTests = (manualTests.withResult("IGNORED") > 0)>
    <#assign failingManualTests = (manualTests.withResult("FAILURE") > 0)>

    <script class="code" type="text/javascript">$(document).ready(function () {

            $('.scenario-result-table').DataTable({
                "order": [[0, "asc",], [3, "asc",]],
                "pageLength": 10,
                "language": {
                    searchPlaceholder: "Filter",
                    search: ""
                },
                columnDefs: [
                    { type: 'time-elapsed-dhms', targets: 4 }
                ]
            });

            // Results table
            $('#test-results-table').DataTable({
                "order": [[0, "asc",], [3, "asc",]],
                "pageLength": 10,
                "lengthMenu": [[10, 25, 50, 100, 200, -1], [10, 25, 50, 100, 200, "All"]]
            });

            $('#evidence-table').DataTable({
                <#if evidence?size <= 10 >
                searching: false,
                paging: false,
                info: false
                </#if>
            });

            $(".feature-coverage-table").DataTable({
                searching: false,
                paging: false,
                info: false
            });

            $(".feature-coverage-table-with-pagination").DataTable({
                order: [[0, "asc",]],
                pageLength: 10,
                language: {
                    searchPlaceholder: "Filter",
                    search: ""
                }
            });
        });
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
            <#assign pageTitle = "<i class='fa fa-tags'></i> " + inflection.of(currentTagType!"").asATitle() + ': ' +  tagInflector.ofTag(currentTagType!"", testOutcomes.label).toFinalView() >
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
                    <#assign breadcrumbTitle = formatter.renderTitle(inflection.of(breadcrumb.shortName).asATitle()) >
                    <#assign breadcrumbType = inflection.of(breadcrumb.type).asATitle() >
                    > <a href="${breadcrumbReport}" title="${breadcrumbTitle} (breadcrumbType)">
                    <#--${formatter.htmlCompatible(breadcrumbTitle)}-->
                    ${formatter.htmlCompatibleStoryTitle(breadcrumbTitle)}
                </a>
                </#list>
            <#else>
                <#if currentTagType?has_content>
                    > ${inflection.of(currentTagType!"").asATitle()} ${formatter.renderTitle(titleContext)}
                </#if>
            </#if>
            <#if testOutcomes.label?has_content>
            <#--> ${formatter.truncatedHtmlCompatible(inflection.of(testOutcomes.label).asATitle(),60)}-->
                > <span class="truncate-60">
<#--                    ${formatter.htmlCompatibleStoryTitle(formatter.renderTitle(inflection.of(testOutcomes.label).asATitle()))}-->
                    ${formatter.htmlCompatibleStoryTitle(formatter.renderHeaders(inflection.of(testOutcomes.label).asATitle()))}
            </span>
            </#if>
        </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>
    <!--/* starts second table*/-->
    <#if (customFields?has_content) && (customFields?size > 0) >
        <div>
            <table class="table environment">
                <tr>
                    <#list customFields as customField>
                        <th class="custom-title">
                            ${customField}
                        </th>
                    </#list>
                </tr>
                <tr>
                    <#list customFieldValues as customFieldValue>
                        <td class="custom-value">
                            ${customFieldValue}
                        </td>
                    </#list>
                </tr>
            </table>
        </div>
    </#if>
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
                                <div class="test-count-title">
                                    ${testOutcomes.total} test scenarios <#if (testOutcomes.hasDataDrivenTests())>
                                        (including ${testOutcomes.totalDataRows} rows of test data)</#if>
                                    <#if (csvReport! != '')> |
                                        <a href="${csvReport}" title="Download CSV"> <i class="fa fa-download"
                                                                                        title="Download CSV"></i></a>
                                    </#if>
                                    <#assign successReport = reportName.withPrefix(currentTag).forTestResult("success") >
                                    <#assign brokenReport = reportName.withPrefix(currentTag).forTestResult("broken") >
                                    <#assign failureReport = reportName.withPrefix(currentTag).forTestResult("failure") >
                                    <#assign errorReport = reportName.withPrefix(currentTag).forTestResult("error") >
                                    <#assign compromisedReport = reportName.withPrefix(currentTag).forTestResult("compromised") >
                                    <#assign pendingReport = reportName.withPrefix(currentTag).forTestResult("pending") >
                                    <#assign skippedReport = reportName.withPrefix(currentTag).forTestResult("skipped") >
                                    <#assign ignoredReport = reportName.withPrefix(currentTag).forTestResult("ignored") >

                                    <#assign totalCount   = testOutcomes.totalScenarios.total >
                                    <#assign successCount = testOutcomes.totalScenarios.withResult("success") >
                                    <#assign pendingCount = testOutcomes.totalScenarios.withResult("pending") >
                                    <#assign ignoredCount = testOutcomes.totalScenarios.withResult("ignored") >
                                    <#assign skippedCount = testOutcomes.totalScenarios.withResult("skipped") >
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
                            <i class="fa fa fa-${flag.symbol} flag-color" alt="${flag.message}"
                               title="${flag.message}"></i> <a href="${flagReport}">${flagTitle}</a> (${flagCount})
                        </#list>
                    </span>
                                    </#if>

                                </div>
                            </div>

                            <div>
                                <ul class="nav nav-tabs">
                                    <li class="active">
                                        <a data-toggle="tab" href="#summary"><i class="fas fa-home"></i> Summary</a>
                                    </li>
                                    <li>
                                        <a data-toggle="tab" href="#tests"><i class="fas fa-tachometer-alt"></i> Test
                                            Results</a>
                                    </li>
                                    <#if evidence?has_content>
                                        <li>
                                            <a data-toggle="tab" href="#evidence"><i class="far fa-file"></i>
                                                Evidence</a>
                                        </li>
                                    </#if>
                                </ul>


                                <div class="card border">
                                    <div class="tab-content" id="pills-tabContent">
                                        <div id="summary" class="tab-pane fade in active">
                                            <div class="container-fluid">
                                                <div class="row">
                                                    <div class="col-sm-4">
                                                        <#if testOutcomes.total != 0>
                                                            <div style="width:300px;"
                                                                 class="chart-container ${graphType}">
                                                                <div class="ct-chart ct-square"></div>
                                                            </div>
                                                            <script>

                                                                var labels = ${testLabels};
                                                                // Our series array that contains series objects or in this case series data arrays

                                                                var series = ${testResultData};

                                                                // As options we currently only set a static size of 300x200 px. We can also omit this and use aspect ratio containers
                                                                // as you saw in the previous example
                                                                var options = {
                                                                    width: 350,
                                                                    height: 300
                                                                };


                                                                new Chartist.Pie('.ct-chart', {
                                                                    series: series,
                                                                    labels: labels
                                                                }, {
                                                                    plugins: [Chartist.plugins.tooltip()],
                                                                    donut: true,
                                                                    donutWidth: 60,
                                                                    donutSolid: true,
                                                                    startAngle: 270,
                                                                    showLabel: true
                                                                }, options);


                                                                $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
                                                                    new Chartist.Pie('.ct-chart', {
                                                                        series: series,
                                                                        labels: labels
                                                                    }, {
                                                                        plugins: [Chartist.plugins.tooltip()],
                                                                        donut: true,
                                                                        donutWidth: 60,
                                                                        donutSolid: true,
                                                                        startAngle: 270,
                                                                        showLabel: true
                                                                    }, options);
                                                                });


                                                            </script>
                                                        </#if>
                                                    </div>
                                                    <div class="col-sm-8">

                                                        <table class="table">
                                                            <thead>
                                                            <tr>
                                                                <th scope="col">Scenario Results (including rows of test
                                                                    data)
                                                                </th>
                                                                <th scope="col" colspan="2" class="automated-stats">
                                                                    Automated
                                                                </th>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <th scope="col" colspan="2" class="manual-stats">
                                                                        Manual
                                                                    </th>
                                                                    <th scope="col" colspan="2" class="total-stats">
                                                                        Total
                                                                    </th>
                                                                </#if>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("success") != 0)>
                                                                    <td class="aggregate-result-count">
                                                                        <a href="${successReport}"><i
                                                                                    class='fa fa-check-circle-o success-icon'></i>&nbsp;Passing</a>
                                                                    </td>
                                                                <#else>
                                                                    <td class="aggregate-result-count"><i
                                                                                class='fa fa-check-circle-o success-icon'></i>&nbsp;Passing
                                                                    </td>
                                                                </#if>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("success")}</td>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("success")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <td class="manual-stats">${resultCounts.getManualTestCount("success")}</td>
                                                                    <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("success")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestCount("success")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("success")}</td>
                                                                </#if>
                                                            </tr>
                                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("pending") != 0)>
                                                                    <td class="aggregate-result-count">
                                                                        <a href="${pendingReport}"><i
                                                                                    class='fa fa-stop-circle-o pending-icon'></i>&nbsp;Pending</a>
                                                                    </td>
                                                                <#else>
                                                                    <td class="aggregate-result-count"><i
                                                                                class='fa fa-stop-circle-o pending-icon'></i>&nbsp;Pending
                                                                    </td>
                                                                </#if>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("pending")}</td>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("pending")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <td class="manual-stats">${resultCounts.getManualTestCount("pending")}</td>
                                                                    <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("pending")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestCount("pending")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("pending")}</td>
                                                                </#if>
                                                            </tr>
                                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("ignored") != 0)>
                                                                    <td class="aggregate-result-count">
                                                                        <a href="${ignoredReport}"><i
                                                                                    class='fa fa-ban ignored-icon'></i>&nbsp;Ignored</a>
                                                                    </td>
                                                                <#else>
                                                                    <td class="aggregate-result-count"><i
                                                                                class='fa fa-ban ignored-icon'></i>&nbsp;Ignored
                                                                    </td>
                                                                </#if>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("ignored")}</td>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("ignored")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <td class="manual-stats">${resultCounts.getManualTestCount("ignored")}</td>
                                                                    <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("ignored")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestCount("ignored")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("ignored")}</td>
                                                                </#if>
                                                            </tr>
                                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("skipped") != 0)>
                                                                    <td class="aggregate-result-count">
                                                                        <a href="${skippedReport}"><i
                                                                                    class='fa fa-fast-forward skip-icon'></i>&nbsp;Skipped</a>
                                                                    </td>
                                                                <#else>
                                                                    <td class="aggregate-result-count"><i
                                                                                class='fa fa-fast-forward skip-icon'></i>&nbsp;Skipped
                                                                    </td>
                                                                </#if>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("skipped")}</td>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("skipped")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <td class="manual-stats">${resultCounts.getManualTestCount("skipped")}</td>
                                                                    <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("skipped")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestCount("skipped")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("skipped")}</td>
                                                                </#if>
                                                            </tr>
                                                            <tr>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <#if (resultCounts.getOverallTestsCount("failure","error","compromised") != 0)>
                                                                        <td colspan="7"><a
                                                                                    href="${relativeLink}${brokenReport}"><i
                                                                                        class='fa fa-times failure-icon'></i>&nbsp;<em>Unsuccessful</em></a>
                                                                        </td>
                                                                    <#else>
                                                                        <td colspan="7"><i
                                                                                    class='fa fa-times failure-icon'></i>&nbsp;<em>Unsuccessful</em></a>
                                                                        </td>
                                                                    </#if>
                                                                <#else>
                                                                    <#if (resultCounts.getOverallTestsCount("failure","error","compromised") != 0)>
                                                                        <td colspan="3"><a
                                                                                    href="${relativeLink}${brokenReport}"><i
                                                                                        class='fa fa-times failure-icon'></i>&nbsp;<em>Unsuccessful</em></a>
                                                                        </td>
                                                                    <#else>
                                                                        <td colspan="3"><i
                                                                                    class='fa fa-times failure-icon'></i>&nbsp;<em>Unsuccessful</em></a>
                                                                        </td>
                                                                    </#if>
                                                                </#if>
                                                            </tr>
                                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("failure") != 0)>
                                                                    <td class="aggregate-result-count indented-error-category">
                                                                        <a href="${failureReport}"><i
                                                                                    class='fa fa-times-circle failure-icon'></i>&nbsp;Failed</a>
                                                                    </td>
                                                                <#else>
                                                                    <td class="aggregate-result-count indented-error-category">
                                                                        <i
                                                                                class='fa fa-times-circle failure-icon'></i>&nbsp;Failed
                                                                    </td>
                                                                </#if>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("failure")}</td>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("failure")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <td class="manual-stats">${resultCounts.getManualTestCount("failure")}</td>
                                                                    <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("failure")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestCount("failure")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("failure")}</td>
                                                                </#if>
                                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("error") != 0)>
                                                                    <td class="aggregate-result-count indented-error-category">
                                                                        <a href="${errorReport}"><i
                                                                                    class='fa fa-exclamation-triangle error-icon'></i>&nbsp;Broken</a>
                                                                    </td>
                                                                <#else>
                                                                    <td class="aggregate-result-count indented-error-category">
                                                                        <i
                                                                                class='fa fa-exclamation-triangle error-icon'></i>&nbsp;Broken
                                                                    </td>
                                                                </#if>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("error")}</td>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("error")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <td class="manual-stats">${resultCounts.getManualTestCount("error")}</td>
                                                                    <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("error")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestCount("error")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("error")}</td>
                                                                </#if>
                                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("compromised") != 0)>
                                                                    <td class="aggregate-result-count indented-error-category">
                                                                        <a href="${compromisedReport}"><i
                                                                                    class='fa fa-chain-broken compromised-icon'></i>&nbsp;Compromised</a>
                                                                    </td>
                                                                <#else>
                                                                    <td class="aggregate-result-count indented-error-category">
                                                                        <i
                                                                                class='fa fa-chain-broken compromised-icon'></i>&nbsp;Compromised
                                                                    </td>
                                                                </#if>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("compromised")}</td>
                                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("compromised")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <td class="manual-stats">${resultCounts.getManualTestCount("compromised")}</td>
                                                                    <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("compromised")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestCount("compromised")}</td>
                                                                    <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("compromised")}</td>
                                                                </#if>
                                                            </tr>
                                                            <tr class="summary-stats">
                                                                <td class="aggregate-result-count">Total</td>
                                                                <td class="automated-stats">${resultCounts.getTotalAutomatedTestCount()}</td>
                                                                <td class="automated-stats"></td>
                                                                <#if resultCounts.hasManualTests() >
                                                                    <td class="manual-stats">${resultCounts.getTotalManualTestCount()}</td>
                                                                    <td class="manual-stats"></td>
                                                                    <td class="total-stats">${resultCounts.getTotalOverallTestCount()}</td>
                                                                    <td class="total-stats"></td>
                                                                </#if>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>

                                                <div class="row">
                                                    <div class="col-sm-12">
                                                        <table class="table">
                                                            <thead>
                                                            <tr>
                                                                <th>Execution Time</th>
                                                                <th>Clock Time</th>
                                                                <th>Fastest Test</th>
                                                                <th>Slowest Test</th>
                                                                <th>Average Execution Time</th>
                                                            </tr>
                                                            </thead>
                                                            <tbody>
                                                            <tr>
                                                                <td>${totalTestDuration}</td>
                                                                <td>${totalClockDuration}</td>
                                                                <td>${minTestDuration}</td>
                                                                <td>${maxTestDuration}</td>
                                                                <td>${averageTestDuration}</td>
                                                            </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                                <#if coverage?has_content>
                                                    <div class="row">
                                                        <div class="col-sm-12">
                                                            <h3>Functional Coverage Overview</h3>

                                                            <#list coverage as tagCoverageByType>
                                                                <#if tagCoverageByType.tagCoverage?has_content>
                                                                    <#if tagCoverageByType.tagCoverage?size <= 10>
                                                                        <#assign coverageTableClass="feature-coverage-table">
                                                                    <#else>
                                                                        <#assign coverageTableClass="feature-coverage-table-with-pagination">
                                                                    </#if>

                                                                    <#assign sectionTitle = inflection.of(tagCoverageByType.tagType).inPluralForm().asATitle() >
                                                                    <h4>${sectionTitle}</h4>

                                                                    <table class="table ${coverageTableClass}"
                                                                           id="${tagCoverageByType.tagType}">
                                                                        <thead>
                                                                        <tr>
                                                                            <th>${formatter.humanReadableFormOf(tagCoverageByType.tagType)}</th>
                                                                            <th style="width:7.5em;">Scenarios</th>
                                                                            <th style="width:7.5em;">% Pass</th>
                                                                            <th style="width:7.5em;">Result</th>
                                                                            <th>Coverage</th>
                                                                        </tr>
                                                                        </thead>
                                                                        <#assign tagCoverageEntries = tagCoverageByType.tagCoverage />
                                                                        <tbody>
                                                                        <#list tagCoverageEntries as tagCoverage>
                                                                            <tr>
                                                                                <td>
                                                                                    <#if tagCoverage.testCount = 0>
                                                                                        ${tagCoverage.tagName}
                                                                                    <#else>
                                                                                        <a href="${tagCoverage.report}">${tagCoverage.tagName}</a>
                                                                                    </#if>
                                                                                </td>
                                                                                <td>${tagCoverage.testCount}</td>
                                                                                <td>${tagCoverage.successRate}</td>
                                                                                <td>
                                                                                    <#if tagCoverage.testCount = 0>
                                                                                        <i class="fa fa-stop-circle-o pending-icon"></i>
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
                                                        <div class="col-sm-6">
                                                            <h3>Test Failure Overview</h3>
                                                        </div>
                                                    </div>
                                                    <div class="row">
                                                        <div class="col-sm-6">
                                                            <h4>Most Frequent Failures</h4>
                                                            <table class="table">
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
                                                        <div class="col-sm-6">
                                                            <h4>Most Unstable Features</h4>
                                                            <table class="table">
                                                                <tbody>
                                                                <#list unstableFeatures as unstableFeature>
                                                                    <tr>
                                                                        <td class="failure-color top-list-title"><a
                                                                                    href="${unstableFeature.report}">${unstableFeature.name}</a>
                                                                        </td>
                                                                        <td><span
                                                                                    class="badge failure-badge">${unstableFeature.failurePercentage}%</span>
                                                                        </td>
                                                                    </tr>
                                                                </#list>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </#if>

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
                                                                        <span class="badge"
                                                                              style="background-color:${tagResult.color}; margin:1em;padding:4px;"><i
                                                                                    class="fa fa-tag"></i> ${tagInflector.ofTag(tagResult.tag.type, tagResult.tag.name).toFinalView()}&nbsp;&nbsp;&nbsp;${tagResult.count}</span>
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
                                        </div>
                                        <div id="tests" class="tab-pane fade">
                                            <div class="container-fluid">
                                                <div class="row">
                                                    <div class="col-sm-12">
                                                        <h3><i class="fas fa-cogs"></i> Automated Tests</h3>

                                                        <#if (automatedTestCases?has_content)>
                                                            <table class="scenario-result-table table"
                                                                   id="scenario-results">
                                                                <thead>
                                                                <tr>
                                                                    <th>${leafRequirementType}</th>
                                                                    <th class="test-name-column">Scenario</th>
                                                                    <th>Steps</th>
                                                                    <th>Start Time</th>
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
                                                                                    style="display:none">${scenario.result}</span>
                                                                        </td>
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
                                                        <h3><i class="fas fa-edit"></i> Manual Tests</h3>

                                                        <#if (manualTestCases?has_content)>
                                                            <table class="scenario-result-table table"
                                                                   id="manual-scenario-results">
                                                                <thead>
                                                                <tr>
                                                                    <th>${leafRequirementType}</th>
                                                                    <th class="test-name-column" style="width:60em;">
                                                                        Scenario
                                                                    </th>
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
                                                                                    style="display:none">${scenario.result}</span>
                                                                        </td>
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

                                        <#if evidence?has_content>
                                            <div id="evidence" class="tab-pane fade">
                                                <div class="container-fluid">
                                                    <div class="row">
                                                        <div class="col-sm-12">
                                                            <h3><i class="far fa-file"></i> Evidence</h3>
                                                            <table id="evidence-table" class="table table-bordered">
                                                                <thead>
                                                                <tr>
                                                                    <th>Scenario</th>
                                                                    <th>Title</th>
                                                                    <th>Details</th>
                                                                </tr>
                                                                </thead>
                                                                <tbody>
                                                                <#list evidence as evidenceRecord>
                                                                    <tr>
                                                                        <td>${evidenceRecord.scenario}</td>
                                                                        <td>${evidenceRecord.title}</td>
                                                                        <td>${evidenceRecord.detailsLink}</td>
                                                                    </tr>
                                                                </#list>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </#if>
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


</body>
</html>
