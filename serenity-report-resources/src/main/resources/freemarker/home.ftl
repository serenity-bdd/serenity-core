<!DOCTYPE html>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Thucydides Reports</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" href="font-awesome/css/font-awesome.min.css">
    <!--[if IE 7]>
    <link rel="stylesheet" href="font-awesome/css/font-awesome-ie7.min.css">
    <![endif]-->
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/link.css"/>
    <link type="text/css" media="screen" href="css/screen.css" rel="Stylesheet"/>

    <link rel="stylesheet" type="text/css" href="jqplot/jquery.jqplot.min.css"/>


    <!--[if IE]>
    <script language="javascript" type="text/javascript" src="jit/Extras/excanvas.js"></script><![endif]-->

    <script type="text/javascript" src="scripts/jquery.js"></script>
    <script type="text/javascript" src="datatables/media/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="jqplot/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="jqplot/plugins/jqplot.pieRenderer.min.js"></script>

    <link type="text/css" href="jqueryui/css/start/jquery-ui-1.8.18.custom.css" rel="Stylesheet"/>
    <script type="text/javascript" src="jqueryui/js/jquery-ui-1.8.18.custom.min.js"></script>


<#assign successfulManualTests = (testOutcomes.count("manual").withResult("SUCCESS") > 0)>
<#assign pendingManualTests = (testOutcomes.count("manual").withResult("PENDING") > 0)>
<#assign ignoredManualTests = (testOutcomes.count("manual").withResult("IGNORED") > 0)>
<#assign failingManualTests = (testOutcomes.count("manual").withResult("FAILURE") > 0)>

    <script class="code" type="text/javascript">$(document).ready(function () {
        var test_results_plot = $.jqplot('test_results_pie_chart', [
            [
                ['Passing', ${testOutcomes.proportionOf("automated").withResult("success")}],
                <#if (successfulManualTests)>['Passing (manual)', ${testOutcomes.proportionOf("manual").withResult("success")}],</#if>
                ['Pending', ${testOutcomes.proportionOf("automated").withResult("pending")}],
                <#if (pendingManualTests)>['Pending (manual)', ${testOutcomes.proportionOf("manual").withResult("pending")}],</#if>
                ['Ignored', ${testOutcomes.proportionOf("automated").withResult("ignored")}],
                <#if (pendingManualTests)>['Ignored (manual)', ${testOutcomes.proportionOf("manual").withResult("ignored")}],</#if>
                ['Failing', ${testOutcomes.proportionOf("automated").withResult("failure")}],
                <#if (failingManualTests)>['Failing (manual)', ${testOutcomes.proportionOf("manual").withResult("failure")}],</#if>
                ['Errors',  ${testOutcomes.proportionOf("automated").withResult("error")}]
            ]
        ], {

            gridPadding: {top: 0, bottom: 38, left: 0, right: 0},
            seriesColors: ['#30cb23',
                <#if (successfulManualTests)>'#009818',</#if>
                '#a2f2f2',
                <#if (pendingManualTests)>'#8bb1df',</#if>
                '#eeeadd',
                <#if (ignoredManualTests)>'#d3d3d3',</#if>
                '#f8001f',
                <#if (failingManualTests)>'#a20019',</#if>
                '#fc6e1f'],
            seriesDefaults: {
                renderer: $.jqplot.PieRenderer,
                trendline: { show: false },
                rendererOptions: { padding: 8, showDataLabels: true }
            },
            legend: {
                show: true,
                placement: 'outside',
                rendererOptions: {
                    numberRows: 2
                },
                location: 's',
                marginTop: '15px'
            },
            series: [
                {label: '${testOutcomes.count("automated").withResult("success")} / ${testOutcomes.total} tests passed' },
            <#if (successfulManualTests)>
                {label: '${testOutcomes.count("manual").withResult("success")} / ${testOutcomes.total} manual tests passed' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("pending")} / ${testOutcomes.total} tests pending'},
            <#if (pendingManualTests)>
                {label: '${testOutcomes.count("manual").withResult("pending")} / ${testOutcomes.total} manual tests pending' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("ignored")} / ${testOutcomes.total} tests not executed'},
            <#if (ignoredManualTests)>
                {label: '${testOutcomes.count("manual").withResult("ignored")} / ${testOutcomes.total} manual tests not executed' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("failure")} / ${testOutcomes.total} tests failed'},
            <#if (failingManualTests)>
                {label: '${testOutcomes.count("manual").withResult("failure")} / ${testOutcomes.total} manual tests failed' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("error")} / ${testOutcomes.total} errors'}
            ]
        });

        var weighted_test_results_plot = $.jqplot('weighted_test_results_pie_chart', [
            [
                ['Passing', ${testOutcomes.proportionalStepsOf("automated").withResult("success")}],
                <#if (successfulManualTests)>['Passing (manual)', ${testOutcomes.proportionalStepsOf("manual").withResult("success")}],</#if>
                ['Pending', ${testOutcomes.proportionalStepsOf("automated").withResult("pending")}],
                <#if (pendingManualTests)>['Pending (manual)', ${testOutcomes.proportionalStepsOf("manual").withResult("pending")}],</#if>
                ['Ignored', ${testOutcomes.proportionalStepsOf("automated").withResult("ignored")}],
                <#if (ignoredManualTests)>['Pending (manual)', ${testOutcomes.proportionalStepsOf("manual").withResult("ignored")}],</#if>
                ['Failing', ${testOutcomes.proportionalStepsOf("automated").withResult("failure")}],
                <#if (failingManualTests)>['Failing (manual)', ${testOutcomes.proportionalStepsOf("manual").withResult("failure")}],</#if>
                ['Errors', ${testOutcomes.proportionalStepsOf("automated").withResult("error")}]
            ]
        ], {

            gridPadding: {top: 0, bottom: 38, left: 0, right: 0},
            seriesColors: ['#30cb23',
                <#if (successfulManualTests)>'#28a818',</#if>
                '#a2f2f2',
                <#if (pendingManualTests)>'#8be1df',</#if>
                '#eeeadd',
                <#if (ignoredManualTests)>'#d3d3d3',</#if>
                '#f8001f',
                <#if (failingManualTests)>'#e20019',</#if>
                '#fc6e1f'],

            seriesDefaults: {
                renderer: $.jqplot.PieRenderer,
                trendline: { show: false },
                rendererOptions: { padding: 8, showDataLabels: true }
            },
            legend: {
                show: true,
                placement: 'outside',
                rendererOptions: {
                    numberRows: 2
                },
                location: 's',
                marginTop: '15px'
            },
            series: [
                {label: '${testOutcomes.count("automated").withResult("success")} / ${testOutcomes.total} tests passed (${testOutcomes.decimalPercentageSteps("automated").withResult("success")}% of all test steps)' },
            <#if (successfulManualTests)>
                {label: '${testOutcomes.count("manual").withResult("success")} / ${testOutcomes.total} manual tests passed (${testOutcomes.decimalPercentageSteps("manual").withResult("success")}% of all test steps)' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("pending")} / ${testOutcomes.total} tests pending'},
            <#if (pendingManualTests)>
                {label: '${testOutcomes.count("manual").withResult("pending")} / ${testOutcomes.total} manual tests pending' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("ignored")} / ${testOutcomes.total} tests not executed'},
            <#if (ignoredManualTests)>
                {label: '${testOutcomes.count("manual").withResult("ignored")} / ${testOutcomes.total} manual tests not executed' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("failure")} / ${testOutcomes.total} tests failed (${testOutcomes.decimalPercentageSteps("automated").withResult("failure")}% of all test steps)'},
            <#if (failingManualTests)>
                {label: '${testOutcomes.count("manual").withResult("failure")} / ${testOutcomes.total} manual tests failed (${testOutcomes.decimalPercentageSteps("manual").withResult("failure")}% of all test steps)' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("error")} / ${testOutcomes.total} errors (${testOutcomes.decimalPercentageSteps("automated").withResult("error")}% of all test steps)'}
            ]
        });

        // Results table
        $('#test-results-table').dataTable({
            "aaSorting": [
                [ 1, "asc" ]
            ],
            "bJQueryUI": true,
            "iDisplayLength": 25
        });

        // Pie charts
        $('#test-results-tabs').tabs()

        $('#toggleNormalPieChart').click(function () {
            $("#test_results_pie_chart").toggle();
        });

        $('#toggleWeightedPieChart').click(function () {
            $("#weighted_test_results_pie_chart").toggle();
        });

    <#if !reportOptions.displayPiechart>
        $("#test_results_pie_chart").hide();
        $("#weighted_test_results_pie_chart").hide();
    </#if>


    })
    ;
    </script>
</head>

<body>
<div id="topheader">
    <div id="topbanner">
        <div id="logo"><a href="index.html"><img src="images/logo.jpg" border="0"/></a></div>
        <div id="projectname-banner" style="float:right">
            <span class="projectname">${reportOptions.projectName}</span>
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
        <#assign pageTitle = inflection.of(currentTagType!"").asATitle() + ': ' +  inflection.of(testOutcomes.label).asATitle() >
    <#else>
        <#assign pageTitle = inflection.of(testOutcomes.label).asATitle() >
    </#if>
</#if>
<div id="contenttop">
<#--<div class="leftbg"></div>-->
    <div class="middlebg">
        <span class="bluetext"><a href="index.html" class="bluetext">Home</a> ${resultsContext}</span>
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
                <span class="test-count-title">${testOutcomes.total}
                    test scenarios <#if (testOutcomes.hasDataDrivenTests())>(including ${testOutcomes.totalDataRows}
                    rows of test data)</#if></span>
                <div>
            <#assign successReport = reportName.withPrefix(currentTag).forTestResult("success") >
            <#assign failureReport = reportName.withPrefix(currentTag).forTestResult("failure") >
            <#assign errorReport = reportName.withPrefix(currentTag).forTestResult("error") >
            <#assign pendingReport = reportName.withPrefix(currentTag).forTestResult("pending") >
            <#assign skippedReport = reportName.withPrefix(currentTag).forTestResult("skipped") >
            <#assign ignoredReport = reportName.withPrefix(currentTag).forTestResult("ignored") >

            <#assign totalCount = testOutcomes.totalTests.total >
            <#assign successCount = testOutcomes.totalTests.withResult("success") >
            <#assign pendingCount = testOutcomes.totalTests.withResult("pending") >
            <#assign ignoredCount = testOutcomes.totalTests.withResult("ignored") >
            <#assign skippedCount = testOutcomes.totalTests.withResult("skipped") >
            <#assign failureCount = testOutcomes.totalTests.withResult("failure") >
            <#assign errorCount = testOutcomes.totalTests.withResult("error") >
            <#assign failureOrErrorCount = testOutcomes.totalTests.withFailureOrError() >

                <span class="test-count">
                    ${successCount}
                    <#if (successCount > 0 && report.shouldDisplayResultLink)>
                        <a href="${relativeLink}${successReport}">passed</a>
                    <#else>passed</#if>,
                </span>
                <span class="test-count">
                    ${pendingCount}
                    <#if (pendingCount > 0 && report.shouldDisplayResultLink)>
                        <a href="${relativeLink}${pendingReport}">pending</a>
                    <#else>pending</#if>,
                </span>
                <span class="test-count">
                    ${failureCount}
                    <#if (failureCount > 0 && report.shouldDisplayResultLink)>
                        <a href="${relativeLink}${failureReport}">failed</a>
                    <#else>failed</#if>,
                </span>
                <span class="test-count">
                    ${errorCount}
                    <#if (errorCount > 0 && report.shouldDisplayResultLink)>
                        <a href="${relativeLink}${errorReport}">with errors</a>
                    <#else>errors</#if>,
                </span>
                <span class="test-count">
                    ${ignoredCount}
                    <#if (ignoredCount > 0 && report.shouldDisplayResultLink)>
                        <a href="${relativeLink}${ignoredReport}">ignored</a>
                    <#else>ignored</#if>,
                </span>
                <span class="test-count">
                    ${skippedCount}
                    <#if (skippedCount > 0 && report.shouldDisplayResultLink)>
                        <a href="${relativeLink}${skippedReport}">skipped</a>
                    <#else>skipped</#if>
                </span>
                <#if (csvReport! != '')>
                    <a href="${csvReport}">[CSV]</a>
                </#if>
                </div>
            </div>

            <div id="test-results-tabs">
                <ul>
                    <li><a href="#test-results-tabs-1">Test Count</a></li>
                    <li><a href="#test-results-tabs-2">Weighted Tests</a></li>
                </ul>
                <div id="test-results-tabs-1">
                    <table>
                        <tr>
                            <td colspan="2">
                                <span class="caption">Total number of tests that pass, fail, or are pending.</span>
                                <span class="togglePieChart" id="toggleNormalPieChart"><a href="#">Show/Hide Pie
                                    Chart</a></span>
                            </td>
                        </tr>
                        <tr>
                            <td style="vertical-align: text-top;">
                                <div id="test_results_pie_chart"></div>
                            </td>
                            <td class="related-tags-section">
                                <div>
                                <#include "test-result-summary.ftl"/>
                                </div>
                                <div>
                                <#if reportOptions.showRelatedTags>
                                    <@list_tags weighted="false"/>
                                </#if>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
                <div id="test-results-tabs-2">
                    <table>
                        <tr>
                            <td colspan="2">
                                <span class="caption">Test results weighted by test size in steps (average steps per test: ${testOutcomes.averageTestSize}) .</span>
                                <span class="togglePieChart" id="toggleWeightedPieChart"><a href="#">Show/Hide Pie
                                    Chart</a></span>
                            </td>
                        </tr>
                        <tr>
                            <td style="vertical-align: text-top;">
                                <div id="weighted_test_results_pie_chart"></div>
                            </td>
                            <td class="related-tags-section">
                                <div>
                                <#include "test-result-summary.ftl"/>
                                </div>
                                <div>
                                <#if reportOptions.showRelatedTags>
                                   <@list_tags weighted="true"/>
                                </#if>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </td>

    </tr>
</table>
<#--- Test Results -->
<table>
    <tr>
        <td>
            <div><h3>Tests</h3></div>
            <div id="test_list_tests" class="table">
                <div class="test-results">
                    <table id="test-results-table">
                        <thead>
                        <tr>
                            <th width="50" class="test-results-heading">&nbsp;</th>
                            <th width="%" class="test-results-heading">Tests</th>
                            <th width="70" class="test-results-heading">Steps</th>

                        <#if reportOptions.showStepDetails>
                            <th width="65" class="test-results-heading">Fail</th>
                            <th width="65" class="test-results-heading">Error</th>
                            <th width="65" class="test-results-heading">Pend</th>
                            <th width="65" class="test-results-heading">Ignore</th>
                            <th width="65" class="test-results-heading">Skip</th>
                        </#if>

                            <th width="65" class="test-results-heading">Stable</th>
                            <th width="100" class="test-results-heading">Duration<br>(seconds)</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#assign testResultSet = testOutcomes.tests >
                        <#foreach testOutcome in testResultSet>
                            <#if testOutcome.result == "PENDING">
                                <#assign testrun_outcome_icon = "pending.png">
                            <#elseif testOutcome.result == "IGNORED">
                                <#assign testrun_outcome_icon = "ignor.png">
                            <#elseif testOutcome.result == "FAILURE">
                                <#assign testrun_outcome_icon = "fail.png">
                            <#elseif testOutcome.result == "ERROR">
                                <#assign testrun_outcome_icon = "cross.png">
                            <#elseif testOutcome.result == "SUCCESS">
                                <#assign testrun_outcome_icon = "success.png">
                            <#else>
                                <#assign testrun_outcome_icon = "ignor.png">
                            </#if>



                            <#assign stability = testOutcome.recentStability>
                            <#if (testOutcome.recentTestRunCount == testOutcome.recentPendingCount)>
                                <#assign stability_icon = "traffic-in-progress.gif">
                                <#assign stability_rank = 0>
                            <#elseif stability < 0.25>
                                <#assign stability_icon = "traffic-red.gif">
                                <#assign stability_rank = 1>
                            <#elseif stability < 0.5 >
                                <#assign stability_icon = "traffic-orange.gif">
                                <#assign stability_rank = 2>
                            <#elseif stability < 0.5 >
                                <#assign stability_icon = "traffic-yellow.gif">
                                <#assign stability_rank = 3>
                            <#else>
                                <#assign stability_icon = "traffic-green.gif">
                                <#assign stability_rank = 4>                                               x
                            </#if>

                        <tr class="test-${testOutcome.result}">
                            <td><img src="images/${testrun_outcome_icon}" title="${testOutcome.result}"
                                     class="summary-icon"/>
                                <#if (testOutcome.manual)><img src="images/worker.png" title="Manual test"/></#if>
                                <span style="display:none">${testOutcome.result}</span></td>
                            <td class="${testOutcome.result}-text"><a
                                    href="${relativeLink}${testOutcome.reportName}.html" title="${testOutcome.errorMessage}">${testOutcome.unqualified.titleWithLinks} ${testOutcome.formattedIssues}</a>
                            </td>

                            <td class="lightgreentext">${testOutcome.nestedStepCount}</td>

                            <#if reportOptions.showStepDetails>
                                <td class="redtext">${testOutcome.failureCount}</td>
                                <td class="redtext">${testOutcome.errorCount}</td>
                                <td class="bluetext">${testOutcome.pendingCount}</td>
                                <td class="bluetext">${testOutcome.skippedCount}</td>
                                <td class="bluetext">${testOutcome.ignoredCount}</td>
                            </#if>

                            <td class="bluetext">
                                <img src="images/${stability_icon}"
                                     title="Over the last ${testOutcome.recentTestRunCount} tests: ${testOutcome.recentPassCount} passed, ${testOutcome.recentFailCount} failed, ${testOutcome.recentPendingCount} pending"
                                     class="summary-icon"/>
                                <span style="display:none">${stability_rank }</span>
                            </td>
                            <td class="lightgreentext">${testOutcome.durationInSeconds}</td>
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
<#--- Test Results end -->
</div>
</div>
</div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter">
    <span class="version">Thucydides version ${thucydidesVersionNumber} - Build ${buildNumber}</span>
</div>
<#macro list_tags(weighted)>
<h4>${tagsTitle}</h4>
    <#foreach tagType in tagTypes>
        <#assign tagTypeTitle = inflection.of(tagType).inPluralForm().asATitle() >
        <#assign outcomesForType = testOutcomes.withTagType(tagType) >
        <#assign tags = testOutcomes.getTagsOfTypeExcluding(tagType, testOutcomes.label) >
        <#if tags?has_content >
        <table class="test-summary-table">
            <tr>
                <td colspan="3">
                    <div class="tagTypeTitle">${tagTypeTitle}
                    </div>
                </td>
            </tr>
            <#foreach tag in tags>
                <#assign tagTitle = inflection.of(tag.shortName).asATitle() >
                <#assign tagReport = reportName.forTag(tag) >
                <#assign outcomesForTag = testOutcomes.withTag(tag) >
                <#--outcomesForType.withTag(tag) >-->
                <#--testOutcomes.withTag(tag) outcomesForType.withTag(tagName)-->
                <#if outcomesForTag.result == "FAILURE">
                    <#assign outcome_icon = "fail.png">
                    <#assign outcome_text = "failure-color">
                <#elseif outcomesForTag.result == "ERROR">
                    <#assign outcome_icon = "cross.png">
                    <#assign outcome_text = "error-color">
                <#elseif outcomesForTag.result == "SUCCESS">
                    <#assign outcome_icon = "success.png">
                    <#assign outcome_text = "success-color">
                <#elseif outcomesForTag.result == "PENDING">
                    <#assign outcome_icon = "pending.png">
                    <#assign outcome_text = "pending-color">
                <#else>
                    <#assign outcome_icon = "ignor.png">
                    <#assign outcome_text = "ignore-color">
                </#if>
                <tr>
                    <td class="bluetext" class="tag-title">
                        <span class="${outcomesForTag.result}-text">
                            <#if testOutcomes.label == tag.name>
                                <a href="${tagReport}" class="currentTag">${tagTitle}</a>
                            <#else>
                                <a href="${tagReport}">${tagTitle}</a>
                            </#if>
                        </span>
                    </td>
                    <td width="150px" class="lightgreentext">
                        <#if weighted == "true">
                            <#assign percentPending = outcomesForTag.percentSteps.withResult("pending")/>
                            <#assign percentIgnored = outcomesForTag.percentSteps.withResult("ignored") + outcomesForTag.percentSteps.withResult("skipped")/>
                            <#assign percentError = outcomesForTag.percentSteps.withResult("error")/>
                            <#assign percentFailing = outcomesForTag.percentSteps.withResult("failure")/>
                            <#assign percentPassing = outcomesForTag.percentSteps.withResult("success")/>

                            <#assign passing = outcomesForTag.formattedPercentageSteps.withResult("success")>
                            <#assign ignored = outcomesForTag.formattedPercentageSteps.withSkippedOrIgnored()/>
                            <#assign failing = outcomesForTag.formattedPercentageSteps.withResult("failure")>
                            <#assign error = outcomesForTag.formattedPercentageSteps.withResult("error")>
                            <#assign pending = outcomesForTag.formattedPercentageSteps.withResult("pending")>
                        <#else>
                            <#assign percentPending = outcomesForTag.proportion.withResult("pending")/>
                            <#assign percentError = outcomesForTag.proportion.withResult("error")/>
                            <#assign percentIgnored = outcomesForTag.proportion.withResult("ignored") + outcomesForTag.proportion.withResult("skipped")/>
                            <#assign percentFailing = outcomesForTag.proportion.withResult("failure")/>
                            <#assign percentPassing = outcomesForTag.proportion.withResult("success")/>

                            <#assign passing = outcomesForTag.formattedPercentage.withResult("success")>
                            <#assign failing = outcomesForTag.formattedPercentage.withResult("failure")>
                            <#assign error = outcomesForTag.formattedPercentage.withResult("error")>
                            <#assign pending = outcomesForTag.formattedPercentage.withResult("pending")>
                            <#assign ignored = outcomesForTag.formattedPercentage.withResult("ignored") + outcomesForTag.formattedPercentage.withResult("skipped") >
                        </#if>

                        <#assign ignoredbar = (percentPassing + percentFailing + percentError + percentIgnored)*150>
                        <#assign errorbar = (percentPassing + percentFailing + percentError)*150>
                        <#assign failingbar = (percentPassing + percentFailing)*150>
                        <#assign passingbar = (percentPassing)*150>

                        <#assign successCount = outcomesForTag.totalTests.withResult("success") >
                        <#assign pendingCount = outcomesForTag.totalTests.withResult("pending") >
                        <#assign failureCount = outcomesForTag.totalTests.withResult("failure") >
                        <#assign errorCount = outcomesForTag.totalTests.withResult("error") >
                        <#assign ignoredCount = outcomesForTag.totalTests.withResult("ignored") + outcomesForTag.totalTests.withResult("skipped")>

                        <#assign successStepCount = outcomesForTag.havingResult("success").stepCount >
                        <#assign pendingStepCount = outcomesForTag.havingResult("pending").stepCount >
                        <#assign failureStepCount = outcomesForTag.havingResult("failure").stepCount >
                        <#assign errorStepCount = outcomesForTag.havingResult("error").stepCount >
                        <#assign ignoredStepCount = outcomesForTag.havingResult("ignored").stepCount + outcomesForTag.havingResult("skipped").stepCount >

                        <#assign pendingCaption = "${pendingCount} out of ${outcomesForTag.total} tests (${pendingStepCount} steps) pending">
                        <#assign passingCaption = "${successCount} out of ${outcomesForTag.total} tests (${successStepCount} steps) passing">
                        <#assign failingCaption = "${failureCount} out of ${outcomesForTag.total} tests (${failureStepCount} steps) failing">
                        <#assign errorCaption = "${errorCount} out of ${outcomesForTag.total} tests (${errorStepCount} steps) broken">
                        <#assign ignoredCaption = "${ignoredCount} out of ${outcomesForTag.total} tests (${ignoredStepCount} steps) skipped or ignored">

                        <table>
                            <tr>
                                <td width="50px">${passing}</td>
                                <td width="150px">
                                    <a href="${tagReport}">
                                        <div class="percentagebar"
                                             title="${pendingCaption}"
                                             style="width: 150px;">
                                            <div class="ignoredbar"
                                                 style="width: ${ignoredbar?string("0")}px;"
                                                 title="${ignoredCaption}">
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
                                    </a>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </#foreach>
        </table>
        </#if>
    </#foreach>
</#macro>

</body>
</html>
