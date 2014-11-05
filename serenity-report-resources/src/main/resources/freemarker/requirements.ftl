<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">

<#if requirements.parentRequirement.isPresent()>
    <#assign pageTitle = inflection.of(requirements.parentRequirement.get().type).inPluralForm().asATitle() >
<#else>
    <#assign pageTitle = "Requirements" >
</#if>

<#assign requirementTypeTitle = inflection.of(requirements.type).asATitle() >
<#assign requirementsSectionTitle = inflection.of(requirements.type).inPluralForm().asATitle() >
<head>
    <meta charset="UTF-8"/>
    <title>${pageTitle}</title>
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

<#assign successfulManualTests = (requirements.count("manual").withResult("SUCCESS") > 0)>
<#assign pendingManualTests = (requirements.count("manual").withResult("PENDING") > 0)>
<#assign ignoredManualTests = (requirements.count("manual").withResult("IGNORED") > 0)>
<#assign failingManualTests = (requirements.count("manual").withResult("FAILURE") > 0)>

    <script class="code" type="text/javascript">$(document).ready(function () {
        var plot1 = $.jqplot('coverage_pie_chart', [
            [
                ['Passing', ${requirements.proportion.withResult("SUCCESS")}],
            <#if (successfulManualTests == true)>
                ['Passing (manual)', ${requirements.proportionOf("manual").withResult("SUCCESS")}],
            </#if>
                ['Pending', ${requirements.proportion.withResult("PENDING")}],
            <#if (pendingManualTests)>
                ['Pending (manual)', ${requirements.proportionOf("manual").withResult("PENDING")}],
            </#if>
                ['Ignored', ${requirements.proportion.withResult("IGNORED")}],
            <#if (pendingManualTests)>
                ['Ignored (manual)', ${requirements.proportionOf("manual").withResult("IGNORED")}],
            </#if>                ['Failing', ${requirements.proportion.withResult("FAILURE")}],
            <#if (failingManualTests)>
                ['Failing (manual)', ${requirements.proportionOf("manual").withResult("FAILURE")}],
            </#if>
                ['Errors',  ${requirements.proportion.withResult("ERROR")}]
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
                    numberRows: 3
                },
                location: 's',
                marginTop: '15px'
            },
            series: [
                {label: '${requirements.formattedPercentage.withResult("SUCCESS")} requirements tested successfully' },
                {label: '${requirements.formattedPercentage.withIndeterminateResult()} requirements untested'},
                {label: '${requirements.formattedPercentage.withResult("FAILURE")}} requirements failing'},
                {label: '${requirements.formattedPercentage.withResult("ERROR")}} requirements with errors'}
            ]
        });
        // Results table
        $('#req-results-table').dataTable({
            "aaSorting": [
                [ 2, "asc" ]
            ],
            "bJQueryUI": true,
            "iDisplayLength": 25
        });
        $('#test-results-table').dataTable({
            "aaSorting": [
                [ 2, "asc" ]
            ],
            "bJQueryUI": true,
            "iDisplayLength": 25
        });
        $('#examples-table').dataTable({
            "aaSorting": [
                [ 2, "asc" ]
            ],
            "bJQueryUI": true,
            "iDisplayLength": 25
        });
        $("#tabs").tabs();
        $("#test-tabs").tabs();
    })
    ;
    </script>

    <script type="text/javascript">
        $(document).ready(function () {
            $(".read-more-link").click(function () {
                $(this).nextAll("div.read-more-text").toggle();
                var isrc = $(this).find("img").attr('src');
                if (isrc == 'images/plus.png') {
                    $(this).find("img").attr("src", function () {
                        return "images/minus.png";
                    });
                } else {
                    $(this).find("img").attr("src", function () {
                        return "images/plus.png";
                    });
                }
            });
        });
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
<div id="contenttop">
    <div class="middlebg">
    <#if requirements.parentRequirement.isPresent()>
        <#assign breadcrumbs = "Requirements > " + pageTitle >
    <#else>
        <#assign breadcrumbs = "Requirements" >
    </#if>
        <span class="bluetext"><a href="index.html" class="bluetext">Home</a> > ${breadcrumbs} </span>
    </div>
    <div class="rightbg"></div>
</div>

<div class="clr"></div>

<!--/* starts second table*/-->
<#include "menu.ftl">

<#if requirements.parentRequirement.isPresent()>
    <@main_menu selected="${requirements.parentRequirement.get().type}" />
<#else>
    <@main_menu selected="requirements" />
</#if>

<div class="clr"></div>

<div id="beforetable"></div>
<div id="results-dashboard">
<div class="middlb">
<div class="table">
<#if (requirements.parentRequirement.isPresent())>
<div>
    <#assign parentRequirement = requirements.parentRequirement.get() >
    <#assign parentTitle = inflection.of(parentRequirement.name).asATitle() >
    <#assign parentType = inflection.of(parentRequirement.type).asATitle() >
    <#if (parentRequirement.cardNumber?has_content) >
        <#assign issueNumber = "[" + formatter.addLinks(parentRequirement.cardNumber) + "]" >
    <#else>
        <#assign issueNumber = "">
    </#if>
    <h2>${parentType}: ${issueNumber} ${parentTitle}</h2>

    <#if parentRequirement.narrative.renderedText?has_content>
        <div class="requirementNarrativeTitle">
        ${formatter.renderDescription(parentRequirement.narrative.renderedText)}
        </div>
    </#if>

    <#foreach customField in parentRequirement.customFields >
        <#if parentRequirement.getCustomField(customField).present>
            <div>
                <a href="javaScript:void(0)" class="read-more-link"><img src="images/plus.png" height="15"/>
                    <span class="custom-field-title">${customField}</span>
                </a>

                <div class="requirementNarrativeField read-more-text">${parentRequirement.getCustomField(customField).get().renderedText}</div>
            </div>
        </#if>
    </#foreach>
</div>
</#if>

<#if (requirements.totalTestCount > 0 || requirements.flattenedRequirementCount > 0)>
<div id="requirements-summary">
    <div id="coverage_pie_chart" style="margin-top:10px; margin-left:10px; width:250px; height:250px;"></div>
    <div id="coverage_summary">
        <div>
            <h4>Requirements Overview</h4>
            <table class="summary-table">
                <head>
                    <tr>
                        <th>Requirement Type</th>
                        <th>Total</th>
                        <th>Pass&nbsp;<i class="icon-check"/></th>
                        <th>Fail&nbsp;<i class="icon-thumbs-down"/></th>
                        <th>Pending&nbsp;<i class="icon-calendar"/></th>
                        <th>Ignored&nbsp;<i class="icon-ban-circle"/></th>
                        <th>Untested&nbsp;<i class="icon-question"/></th>
                    </tr>
                </head>
                <body>
                    <#foreach requirementType in requirements.types>
                    <tr>
                        <#assign requirementTitle = inflection.of(requirementType).inPluralForm().asATitle() />
                        <td class="summary-leading-column">${requirementTitle}</td>
                        <td>${requirements.requirementsOfType(requirementType).requirementCount}</td>
                        <td>${requirements.requirementsOfType(requirementType).completedRequirementsCount}</td>
                        <td>${requirements.requirementsOfType(requirementType).failingRequirementsCount}</td>
                        <td>${requirements.requirementsOfType(requirementType).pendingRequirementsCount}</td>
                        <td>${requirements.requirementsOfType(requirementType).ignoredRequirementsCount}</td>
                        <td>${requirements.requirementsOfType(requirementType).requirementsWithoutTestsCount}</td>
                    </tr>
                    </#foreach>
                    <tr>
                        <td class="summary-leading-column">Acceptance Criteria (tests)</td>
                        <td>${requirements.testOutcomes.testCount}</td>
                        <td>${requirements.testOutcomes.havingResult("success").testCount}</td>
                        <td>${requirements.testOutcomes.havingResult("failure").testCount}</td>
                        <td>${requirements.testOutcomes.havingResult("pending").testCount}</td>
                        <td>${requirements.testOutcomes.havingResult("ignored").testCount + requirements.testOutcomes.havingResult("skipped").testCount}</td>
                        <td></td>
                    </tr>
                </body>
            </table>
        </div>
        <#include "test-result-summary.ftl"/>

    </div>
</div>
<div class="clr"></div>
</#if>

<#if (requirements.requirementOutcomes?has_content || testOutcomes.total > 0)>
<div id="tabs">
    <#if (requirements.requirementOutcomes?has_content || (requirements.parentRequirement.isPresent() && requirements.parentRequirement.get().hasExamples()))>
    <ul>
        <#if (requirements.requirementOutcomes?has_content)>
            <li><a href="#tabs-1">
            ${requirementsSectionTitle} (${requirements.requirementCount})
            </a></li>
        </#if>
        <#if (requirements.parentRequirement.isPresent() && requirements.parentRequirement.get().hasExamples())>
            <li><a href="#tabs-2">Examples (${requirements.parentRequirement.get().exampleCount})</a></li>
        </#if>
    </ul>
    </#if>
    <#if (requirements.requirementOutcomes?has_content)>
    <div id="tabs-1" class="capabilities-table">
    <#--- Requirements -->
        <div id="req_list_tests" class="table">
            <div class="test-results">
                <table id="req-results-table">
                    <thead>
                    <tr>
                        <th width="30" class="test-results-heading">&nbsp;</th>
                        <th width="65" class="test-results-heading">ID</th>
                        <th width="525" class="test-results-heading">${requirementTypeTitle}</th>
                        <#if (requirements.childrenType?has_content) >
                            <#assign childrenTitle = inflection.of(requirements.childrenType).inPluralForm().asATitle()>
                            <th width="65" class="test-results-heading">${childrenTitle}</th>
                        </#if>
                        <th class="test-results-heading" width="75px">Auto.<br/>Tests</th>
                        <th class="test-results-heading" width="25px"><i class="icon-check icon-large"
                                                                         title="Tests passed (automated)"></i></th>
                        <th class="test-results-heading" width="25px"><i class="icon-ban-circle icon-large"
                                                                         title="Tests skipped or pending (automated)">
                        </th>
                        <th class="test-results-heading" width="25px"><i class="icon-thumbs-down icon-large"
                                                                         title="Tests failed (automated)"></th>
                        <th class="test-results-heading" width="25px"><i class="icon-exclamation-sign icon-large"
                                                                         title="Tests failed with an error (automated)">
                        </th>
                        <#if reportOptions.showManualTests>
                            <th class="test-results-heading" width="75px">Manual<br/>Tests</th>
                            <th class="test-results-heading" width="25px"><i class="icon-check icon-large"
                                                                             title="Tests passed (manual)"></i></th>
                            <th class="test-results-heading" width="25px"><i class="icon-ban-circle icon-large"
                                                                             title="Tests skipped or pending (manual)">
                            </th>
                            <th class="test-results-heading" width="25px"><i class="icon-thumbs-down icon-large"
                                                                             title="Tests failed (manual)"></th>
                            <th class="test-results-heading" width="25px"><i class="icon-exclamation-sign icon-large"
                                                                             title="Tests failed with an error (manual)">
                            </th>
                        </#if>


                        <th width="125px" class="test-results-heading">Coverage</th>
                    </tr>
                    <tbody>

                        <#foreach requirementOutcome in requirements.requirementOutcomes>
                            <#if requirementOutcome.testOutcomes.stepCount == 0 || requirementOutcome.testOutcomes.result == "PENDING" || requirementOutcome.testOutcomes.result == "IGNORED">
                                <#assign status_icon = "traffic-yellow.gif">
                                <#assign status_rank = 0>
                            <#elseif requirementOutcome.testOutcomes.result == "ERROR">
                                <#assign status_icon = "traffic-orange.gif">
                                <#assign status_rank = 1>
                            <#elseif requirementOutcome.testOutcomes.result == "FAILURE">
                                <#assign status_icon = "traffic-red.gif">
                                <#assign status_rank = 2>
                            <#elseif requirementOutcome.testOutcomes.result == "SUCCESS">
                                <#assign status_icon = "traffic-green.gif">
                                <#assign status_rank = 3>
                            </#if>

                        <tr class="test-${requirementOutcome.testOutcomes.result} requirementRow">
                            <td class="requirementRowCell">
                                <img src="images/${status_icon}" class="summary-icon"
                                     title="${requirementOutcome.testOutcomes.result}"/>
                                <span style="display:none">${status_rank}</span>
                            </td>
                            <td class="cardNumber requirementRowCell">${requirementOutcome.cardNumberWithLinks}</td>

                            <#assign requirementReport = reportName.forRequirement(requirementOutcome.requirement) >
                            <td class="${requirementOutcome.testOutcomes.result}-text requirementRowCell">
                                <#if requirementOutcome.requirement.displayName?has_content>
                                    <a href="javaScript:void(0)" class="read-more-link"><img src="images/plus.png"
                                                                                             height="20"/></a>
                                </#if>
                                <span class="requirementName"><a
                                        href="${requirementReport}">${requirementOutcome.requirement.displayName}</a></span>
                                <#if requirementOutcome.requirement.narrative.renderedText?has_content>
                                    <div class="requirementNarrative read-more-text">${formatter.renderDescription(requirementOutcome.requirement.narrative.renderedText)}</div>
                                </#if>
                            </td>

                            <#if (requirements.childrenType?has_content) >
                                <td class="bluetext requirementRowCell">${requirementOutcome.requirement.childrenCount}</td>
                            </#if>

                            <#assign successCount = requirementOutcome.testOutcomes.totalTests.withResult("success") >
                            <#assign pendingCount = requirementOutcome.testOutcomes.totalTests.withResult("pending") >
                            <#assign ignoredCount = requirementOutcome.testOutcomes.totalTests.withResult("ignored") >
                            <#assign indeterminateCount = requirementOutcome.testOutcomes.totalTests.withIndeterminateResult() >
                            <#assign skipCount = requirementOutcome.testOutcomes.totalTests.withResult("skipped") >
                            <#assign failureCount = requirementOutcome.testOutcomes.totalTests.withResult("failure") >
                            <#assign errorCount = requirementOutcome.testOutcomes.totalTests.withResult("error") >

                        <#--<td class="bluetext requirementRowCell">${requirementOutcome.testOutcomes.total}</td>-->
                        <#--<td class="greentext requirementRowCell">${successCount}</td>-->
                        <#--<td class="redtext requirementRowCell">${failureCount}</td>-->
                        <#--<td class="lightredtext requirementRowCell">${errorCount}</td>-->
                        <#--<td class="bluetext requirementRowCell">${indeterminateCount}</td>-->

                            <#assign totalAutomated = requirementOutcome.tests.count("AUTOMATED").withAnyResult()/>
                            <#assign automatedPassedPercentage = requirementOutcome.tests.getFormattedPercentage("AUTOMATED").withResult("SUCCESS")/>
                            <#assign automatedFailedPercentage = requirementOutcome.tests.getFormattedPercentage("AUTOMATED").withFailureOrError()/>
                            <#assign automatedPendingPercentage = requirementOutcome.tests.getFormattedPercentage("AUTOMATED").withResult("PENDING")/>
                            <#assign automatedIgnoredPercentage = requirementOutcome.tests.getFormattedPercentage("AUTOMATED").withResult("IGNORED")/>
                            <#assign automatedPassed = requirementOutcome.tests.count("AUTOMATED").withResult("SUCCESS")/>
                            <#assign automatedPending = requirementOutcome.tests.count("AUTOMATED").withResult("PENDING")/>
                            <#assign automatedIgnored = requirementOutcome.tests.count("AUTOMATED").withResult("IGNORED")/>
                            <#assign automatedFailed = requirementOutcome.tests.count("AUTOMATED").withResult("FAILURE")/>
                            <#assign automatedError = requirementOutcome.tests.count("AUTOMATED").withResult("ERROR")/>
                            <#assign totalManual = requirementOutcome.tests.count("MANUAL").withAnyResult()/>
                            <#assign manualPassedPercentage = requirementOutcome.tests.getFormattedPercentage("MANUAL").withResult("SUCCESS")/>
                            <#assign manualFailedPercentage = requirementOutcome.tests.getFormattedPercentage("MANUAL").withFailureOrError()/>
                            <#assign manualPending = requirementOutcome.tests.count("MANUAL").withResult("PENDING")/>
                            <#assign manualIgnored = requirementOutcome.tests.count("MANUAL").withResult("IGNORED")/>
                            <#assign manualPendingPercentage = requirementOutcome.tests.getFormattedPercentage("MANUAL").withResult("PENDING")/>
                            <#assign manualPassed = requirementOutcome.tests.count("MANUAL").withResult("SUCCESS")/>
                            <#assign manualFailed = requirementOutcome.tests.count("MANUAL").withResult("FAILURE")/>
                            <#assign manualError = requirementOutcome.tests.count("MANUAL").withResult("ERROR")/>

                            <td class="greentext highlighted-value requirementRowCell">${totalAutomated}</td>
                            <td class="greentext requirementRowCell">${automatedPassed}</td>
                            <td class="bluetext requirementRowCell">${automatedPending}</td>
                            <td class="redtext requirementRowCell">${automatedFailed}</td>
                            <td class="lightorangetext requirementRowCell">${automatedError}</td>
                            <#if reportOptions.showManualTests>
                                <td class="greentext highlighted-value requirementRowCell">${totalManual}</td>
                                <td class="greentext requirementRowCell">${manualPassed}</td>
                                <td class="bluetext requirementRowCell">${manualPending}</td>
                                <td class="redtext requirementRowCell">${manualFailed}</td>
                                <td class="lightorangetext requirementRowCell">${manualError}</td>
                            </#if>

                            <td width="125px" class="lightgreentext requirementRowCell">
                                <#assign percentPending = requirementOutcome.percent.withResult("PENDING")/>
                                <#assign percentIgnored = requirementOutcome.percent.withResult("IGNORED") + requirementOutcome.percent.withResult("SKIPPED")/>
                                <#assign percentError = requirementOutcome.percent.withResult("ERROR")/>
                                <#assign percentFailing = requirementOutcome.percent.withResult("FAILURE")/>
                                <#assign percentPassing = requirementOutcome.percent.withResult("SUCCESS")/>
                                <#assign percentIndeterminate = requirementOutcome.percent.withIndeterminateResult()/>
                                <#assign passing = requirementOutcome.formattedPercentage.withResult("SUCCESS")>
                                <#assign failing = requirementOutcome.formattedPercentage.withResult("FAILURE")>
                                <#assign error = requirementOutcome.formattedPercentage.withResult("ERROR")>
                                <#assign pending = requirementOutcome.formattedPercentage.withResult("PENDING")>
                                <#assign ignored = requirementOutcome.formattedPercentage.withSkippedOrIgnored()>
                                <#assign indeterminate = requirementOutcome.formattedPercentage.withIndeterminateResult()>


                                <#assign ignoredbar = (percentPassing + percentFailing + percentError + percentIgnored)*125>
                                <#assign errorbar = (percentPassing + percentFailing + percentError)*125>
                                <#assign failingbar = (percentPassing + percentFailing)*125>
                                <#assign passingbar = percentPassing*125>

                                <#assign tests = inflection.of(requirementOutcome.testOutcomes.total).times("test") >

                                <!--
                                Accessing the ESAA Registration screens

                                Tests implemented: 10
                                  - Passing tests: 4
                                  - Failing tests: 3

                                Requirements specified:   6
                                Requiments without tests: 2

                                Estimated unimplemented tests: 7
                                -->
                                <#assign overviewCaption =
                                "${requirementOutcome.requirement.displayName}
Tests implemented: ${requirementOutcome.testCount}
  - Passing tests: ${successCount} (${passing} of specified requirements)
  - Failing tests: ${failureCount} (${failing} of specified requirements)
  - Tests with errors: ${errorCount} (${error} of specified requirements)

Tests not implemented or not executed: ${pendingCount + ignoredCount}
  - Pending tests: ${pendingCount} (${pending} of specified requirements)
  - Ignored or skipped tests: ${ignoredCount} (${ignored} of specified requirements)

Requirements specified:     ${requirementOutcome.flattenedRequirementCount}
Requirements with no tests: ${requirementOutcome.requirementsWithoutTestsCount}


Estimated unimplemented tests: ${requirementOutcome.estimatedUnimplementedTests}
Estimated unimplemented or pending requirements: ${pending}
Estimated ignored or skipped requirements: ${ignored}"
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
                                                <div class="ignoredbar"
                                                     style="width: ${ignoredbar?string("0")}px;"
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
                                        </td>
                                    </tr>
                                </table>
                            </td>

                        </tr>
                        </#foreach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    </#if>
    <#if testOutcomes.tests?has_content >
    <#--- Test Results -->

    <div id="test-tabs">
        <ul>
            <li><a href="#test-tabs-1">Acceptance Tests (${testOutcomes.total})</a></li>
        </ul>
        <div id="test_list_tests" class="table">
            <div class="test-results">
                <table id="test-results-table">
                    <thead>
                    <tr>
                        <th width="30" class="test-results-heading">&nbsp;</th>
                        <th width="%" class="test-results-heading">Acceptance Tests</th>
                        <th width="70" class="test-results-heading">Steps</th>
                        <#if reportOptions.showStepDetails>
                            <th width="65" class="test-results-heading">Fail</th>
                            <th width="65" class="test-results-heading">Errors</th>
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
                                <#assign stability_rank = 4>
                            </#if>

                        <tr class="test-${testOutcome.result}">
                            <td><img src="images/${testrun_outcome_icon}" title="${testOutcome.result}"
                                     class="summary-icon"/><span style="display:none">${testOutcome.result}</span></td>
                            <td class="${testOutcome.result}-text"><a
                                    href="${relativeLink!}${testOutcome.reportName}.html" title="${testOutcome.errorMessage}">${testOutcome.unqualified.titleWithLinks} ${testOutcome.formattedIssues}</a>
                            </td>

                            <td class="lightgreentext">${testOutcome.nestedStepCount}</td>
                            <#if reportOptions.showStepDetails>
                                <td class="redtext">${testOutcome.total.withResult("FAILURE")}</td>
                                <td class="redtext">${testOutcome.total.withResult("ERROR")}</td>
                                <td class="bluetext">${testOutcome.total.withResult("PENDING")}</td>
                                <td class="bluetext">${testOutcome.total.withResult("SKIPPED")}</td>
                                <td class="bluetext">${testOutcome.total.withResult("IGNORED")}</td>
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
    </div>
    </#if>
</div>
</#if>
<#if (requirements.parentRequirement.isPresent() && requirements.parentRequirement.get().hasExamples())>
<div id="tabs-2" class="capabilities-table">
<#-- Examples -->
    <div id="examples" class="table">
        <div class="test-results">
            <table id="examples-table">
                <thead>
                <tr>
                    <th width="100" class="test-results-heading">&nbsp;</th>
                    <th width="%" class="test-results-heading">Description</th>
                </tr>
                </thead>
                <tbody>
                    <#assign examples = requirements.parentRequirement.get().examples >
                    <#foreach example in examples>
                    <tr>
                        <td class="cardNumber requirementRowCell">
                            <#if example.cardNumber.isPresent() >
                                                    ${formatter.addLinks(example.cardNumber.get())}
                                                </#if>
                        </td>
                        <td class="lightgreentext requirementRowCell"> ${formatter.addLineBreaks(example.description)}</td>
                    </tr>
                    </#foreach>
                </tbody>
            </table>
        </div>
    </div>
</div>
</#if>
</div>
</div>
</div>
</div>
</div>
</div>
<div id="bottomfooter">
    <span class="version">Thucydides version ${thucydidesVersionNumber} - Build ${buildNumber}</span>
</div>
</body>
</html>
