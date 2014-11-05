<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<#assign pageTitle = inflection.of(requirementType).inPluralForm().asATitle() >
<#assign requirementTypeTitle = inflection.of(requirementType).asATitle() >
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

    <link type="text/css" media="screen" href="css/screen.css" rel="Stylesheet" />
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
<#assign pendingManualTests = (requirements.count("manual").withIndeterminateResult() > 0)>
<#assign failingManualTests = (requirements.count("manual").withResult("FAILURE") > 0)>

    <script class="code" type="text/javascript">$(document).ready(function () {

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
        <span class="bluetext"><a href="index.html" class="bluetext">Home</a> > Requirements > ${formatter.truncatedHtmlCompatible(pageTitle,60)} </span>
    </div>
    <div class="rightbg"></div>
</div>

<div class="clr"></div>

<!--/* starts second table*/-->
<#include "menu.ftl">
<@main_menu selected="${requirementType}" />

<div class="clr"></div>

<div id="beforetable"></div>
<div id="results-dashboard">
<div class="middlb">
<div class="table">

<#if (requirements.requirementOutcomes?has_content || testOutcomes.total > 0)>
    <div id="tabs">
        <#if (requirements.requirementOutcomes?has_content || (requirements.parentRequirement.isPresent() && requirements.parentRequirement.get().hasExamples()))>
            <ul>
                <#if (requirements.requirementOutcomes?has_content)>
                    <li><a href="#tabs-1">
                    ${pageTitle} (${requirements.requirementCount})
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
                                                                                 title="Tests passed (automated)"></i>
                                </th>
                                <th class="test-results-heading" width="25px"><i class="icon-ban-circle icon-large"
                                                                                 title="Tests skipped or pending (automated)">
                                </th>
                                <th class="test-results-heading" width="25px"><i class="icon-thumbs-down icon-large"
                                                                                 title="Tests failed (automated)"></th>
                                <th class="test-results-heading" width="25px"><i
                                        class="icon-exclamation-sign icon-large"
                                        title="Tests failed with an error (automated)"></th>
                                <#if reportOptions.showManualTests>
                                    <th class="test-results-heading" width="75px">Manual<br/>Tests</th>
                                    <th class="test-results-heading" width="25px"><i class="icon-check icon-large"
                                                                                     title="Tests passed (manual)"></i>
                                    </th>
                                    <th class="test-results-heading" width="25px"><i class="icon-ban-circle icon-large"
                                                                                     title="Tests skipped or pending (manual)">
                                    </th>
                                    <th class="test-results-heading" width="25px"><i class="icon-thumbs-down icon-large"
                                                                                     title="Tests failed (manual)"></th>
                                    <th class="test-results-heading" width="25px"><i
                                            class="icon-exclamation-sign icon-large"
                                            title="Tests failed with an error (manual)"></th>
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
                                        <a href="javaScript:void(0)" class="read-more-link"><img src="images/plus.png"
                                                                                                 height="20"/></a>
                                        <span class="requirementName"><a
                                                href="${requirementReport}">${requirementOutcome.requirement.displayName}</a></span>

                                        <div class="requirementNarrative read-more-text">${formatter.renderDescription(requirementOutcome.requirement.narrative.renderedText)}</div>
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
                                    <#assign automatedPendingPercentage = requirementOutcome.tests.getFormattedPercentage("AUTOMATED").withIndeterminateResult()/>
                                    <#assign automatedPassed = requirementOutcome.tests.count("AUTOMATED").withResult("SUCCESS")/>
                                    <#assign automatedPending = requirementOutcome.tests.count("AUTOMATED").withIndeterminateResult()/>
                                    <#assign automatedFailed = requirementOutcome.tests.count("AUTOMATED").withResult("FAILURE")/>
                                    <#assign automatedError = requirementOutcome.tests.count("AUTOMATED").withResult("ERROR")/>
                                    <#assign totalManual = requirementOutcome.tests.count("MANUAL").withAnyResult()/>
                                    <#assign manualPassedPercentage = requirementOutcome.tests.getFormattedPercentage("MANUAL").withResult("SUCCESS")/>
                                    <#assign manualFailedPercentage = requirementOutcome.tests.getFormattedPercentage("MANUAL").withFailureOrError()/>
                                    <#assign manualPending = requirementOutcome.tests.count("MANUAL").withIndeterminateResult()/>
                                    <#assign manualPendingPercentage = requirementOutcome.tests.getFormattedPercentage("MANUAL").withIndeterminateResult()/>
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
    </div>
</#if>
</div>
</div>
</div>
</div>
</div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter">
    <span class="version">Thucydides version ${thucydidesVersionNumber} - Build ${buildNumber}</span>
</div>


</body>
</html>
