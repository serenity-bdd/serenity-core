<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8"/>
    <title>${release.name}</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" type="text/css" href="jqplot/jquery.jqplot.min.css"/>

    <!--[if IE]>
    <script language="javascript" type="text/javascript" src="jit/Extras/excanvas.js"></script><![endif]-->

    <script type="text/javascript" src="scripts/jquery.js"></script>
    <script type="text/javascript" src="datatables/media/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="jqplot/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="jqplot/plugins/jqplot.pieRenderer.min.js"></script>

    <link type="text/css" href="jqueryui/css/start/jquery-ui-1.8.18.custom.css" rel="Stylesheet"/>
    <script type="text/javascript" src="jqueryui/js/jquery-ui-1.8.18.custom.min.js"></script>

    <script src="jqtree/tree.jquery.js"></script>
    <link rel="stylesheet" href="jqtree/jqtree.css">

    <link rel="stylesheet" href="font-awesome/css/font-awesome.min.css">
    <!--[if IE 7]>
    <link rel="stylesheet" href="font-awesome/css/font-awesome-ie7.min.css">
    <![endif]-->
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/link.css"/>
    <link type="text/css" media="screen" href="css/screen.css" rel="Stylesheet" />

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
            // Results table
            $('#req-results-table').dataTable({
                "aaSorting": [
                    [ 2, "asc" ]
                ],
                "iDisplayLength": 25,
                "bJQueryUI": true
            });

            // Results table
            $('#release-test-results-table').dataTable({
                "aaSorting": [
                    [ 2, "asc" ]
                ],
                "bJQueryUI": true
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
            <span class="bluetext"><a href="releases.html" class="bluetext">Releases</a>
            <#foreach parent in release.parents>
                >&nbsp<a href="${parent.reportName}">${parent.name}</a>
            </#foreach>
                >
            ${release.name}
            </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
<#include "menu.ftl">
<@main_menu selected="releases" />
    <div class="clr"></div>


    <div id="beforetable"></div>
    <div id="results-dashboard">
        <div class="middlb">
            <div class="table">
            <div id="tabs">
                <div id="releases">
                    <h3>Release Details</h3>
                    <table>
                        <tr>
                            <td valign="top"><a class="label" href="releases.html">Releases</a></td>
                            <td valign="top">
                            <#foreach parent in release.parents>
                                >&nbsp<a class="label" href="${parent.reportName}">${parent.name}</a>
                            </#foreach>
                                &nbsp:
                            </td>
                            <td class="release-context-tree">
                                <div id="release-tree"></div>
                            </td>
                        </tr>
                    </table>

                    <script>
                        var releaseData = ${releaseData};
                        $(function () {
                            $('#release-tree').tree({
                                data: releaseData
                            });
                        });

                        $('#release-tree').bind(
                                'tree.click',
                                function (event) {
                                    window.location.href = event.node.reportName;
                                }
                        );

                    </script>

                    <div>
                        <#assign releaseTestReport = reportName.withPrefix(currentTag).forTestResult("pending") >
                    </div>

                    <div id="release-coverage">
                        <div id="tabs" class="ui-tabs ui-widget ui-widget-content ui-corner-all">
                            <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
                                <li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active requirementTitle">
                                    <a href="#tabs-1">Scheduled Requirements</a></li>
                            </ul>
                            <!----->

                            <div id="tabs-1" class="capabilities-table">
                            <#--- Requirements -->
                                <div id="req_list_tests" class="table">
                                    <div class="test-results">
                                        <table id="req-results-table">
                                            <thead>
                                            <tr>
                                                <th width="40" class="test-results-heading">&nbsp;</th>
                                                <#if secondLevelRequirementTypeTitle??>
                                                    <th width="250" class="test-results-heading">${requirementType}</th>
                                                    <th width="250"class="test-results-heading">${secondLevelRequirementTypeTitle}</th>
                                                <#else>
                                                    <th width="500" class="test-results-heading">${requirementType}</th>
                                                </#if>
                                                <th class="test-results-heading" width="50px">Total.<br/>Tests</th>
                                                <th class="test-results-heading" width="50px">%Pass</th>
                                                <th class="test-results-heading" width="50px">Auto.<br/>Tests</th>
                                                <th class="test-results-heading" width="50px">%Pass</th>
                                                <th class="test-results-heading" width="25px"><i
                                                        class="icon-check icon-large"
                                                        title="Tests passed (automated)"></i></th>
                                                <th class="test-results-heading" width="25px"><i
                                                        class="icon-ban-circle icon-large"
                                                        title="Tests skipped or pending (automated)"></th>
                                                <th class="test-results-heading" width="25px"><i
                                                        class="icon-thumbs-down icon-large"
                                                        title="Tests failed (automated)"></th>
                                                <th class="test-results-heading" width="25px"><i
                                                        class="icon-exclamation-sign icon-large"
                                                        title="Tests failed with an error (automated)"></th>
                                            <#if reportOptions.showManualTests>
                                                <th class="test-results-heading" width="50">Manual<br/>Tests</th>
                                                <th class="test-results-heading" width="50px">%Pass</th>
                                                <th class="test-results-heading" width="25px"><i
                                                        class="icon-check icon-large" title="Tests passed (manual)"></i>
                                                </th>
                                                <th class="test-results-heading" width="25px"><i
                                                        class="icon-ban-circle icon-large"
                                                        title="Tests skipped or pending (manual)"></th>
                                                <th class="test-results-heading" width="25px"><i
                                                        class="icon-thumbs-down icon-large"
                                                        title="Tests failed (manual)"></th>
                                                <th class="test-results-heading" width="25px"><i
                                                        class="icon-exclamation-sign icon-large"
                                                        title="Tests failed with an error (manual)"></th>
                                            </#if>
                                            </tr>
                                            <tbody>

                                            <#foreach requirementOutcome in releaseRequirementOutcomes>
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
                                                <td class="release-title">
                                                    <#assign requirementReport = reportName.forRequirement(requirementOutcome.requirement) >
                                                    <a href="${requirementReport}">${requirementOutcome.requirement.name}</a>
                                                </td>
                                                <#if secondLevelRequirementTypeTitle??>
                                                <td class="release-title">
                                                    <ul class="second-level-requirements">
                                                        <#foreach childRequirement in requirementOutcome.requirement.children>
                                                            <#assign childRequirementReport = reportName.forRequirement(childRequirement) >
                                                            <li>
                                                                <a href="${childRequirementReport}">${childRequirement.name}</a>
                                                            </li>
                                                        </#foreach>
                                                    </ul>
                                                </td>
                                                </#if>

                                                <#assign totalAutomated = requirementOutcome.tests.count("AUTOMATED").withAnyResult()/>
                                                <#assign automatedPassed = requirementOutcome.tests.count("AUTOMATED").withResult("SUCCESS")/>
                                                <#assign automatedPending = requirementOutcome.tests.count("AUTOMATED").withIndeterminateResult()/>
                                                <#assign automatedFailed = requirementOutcome.tests.count("AUTOMATED").withResult("FAILURE")/>
                                                <#assign automatedError = requirementOutcome.tests.count("AUTOMATED").withResult("ERROR")/>
                                                <#if (totalAutomated > 0) >
                                                    <#assign automatedPercentagePassed =  (automatedPassed / totalAutomated)/>
                                                <#else>
                                                    <#assign automatedPercentagePassed = 0.0/>
                                                </#if>

                                                <#assign totalManual = requirementOutcome.tests.count("MANUAL").withAnyResult()/>
                                                <#assign manualPending = requirementOutcome.tests.count("MANUAL").withIndeterminateResult()/>
                                                <#assign manualPassed = requirementOutcome.tests.count("MANUAL").withResult("SUCCESS")/>
                                                <#assign manualFailed = requirementOutcome.tests.count("MANUAL").withResult("FAILURE")/>
                                                <#assign manualError = requirementOutcome.tests.count("MANUAL").withResult("ERROR")/>

                                                <#if (totalManual > 0)>
                                                    <#assign manualPercentagePassed = (manualPassed / totalManual)/>
                                                <#else>
                                                    <#assign manualPercentagePassed = 0.0/>
                                                </#if>

                                                <#assign totalTests = totalAutomated + totalManual/>
                                                <#if (totalTests > 0)>
                                                    <#assign percentagePassed = ((automatedPassed + manualPassed) / totalTests)/>
                                                <#else>
                                                    <#assign percentagePassed = 0.0/>
                                                </#if>

                                                <#if (automatedFailed + automatedError > 0)>
                                                    <#assign automatedColor = "redtext"/>
                                                <#elseif (automatedPending > 0)>
                                                    <#assign automatedColor = "bluetext"/>
                                                <#elseif (totalAutomated == 0)>
                                                    <#assign automatedColor = "bluetext"/>
                                                <#else>
                                                    <#assign automatedColor = "greentext"/>
                                                </#if>

                                                <#if (automatedFailed + automatedError + manualFailed + manualError > 0)>
                                                    <#assign totalColor = "redtext"/>
                                                <#elseif (automatedPending + manualPending > 0)>
                                                    <#assign totalColor = "bluetext"/>
                                                <#elseif (totalAutomated == 0)>
                                                    <#assign totalColor = "bluetext"/>
                                                <#else>
                                                    <#assign totalColor = "greentext"/>
                                                </#if>

                                                <td class="${totalColor} highlighted-value">${totalTests}</td>
                                                <td class="${totalColor}">${percentagePassed?string.percent} </td>
                                                <td class="${automatedColor} highlighted-value">${totalAutomated}</td>
                                                <td class="${automatedColor}">${automatedPercentagePassed?string.percent} </td>
                                                <td class="greentext">${automatedPassed}</td>
                                                <td class="bluetext">${automatedPending}</td>
                                                <td class="redtext">${automatedFailed}</td>
                                                <td class="lightorangetext">${automatedError}</td>
                                                <#if reportOptions.showManualTests>
                                                    <#if (manualFailed + manualError > 0)>
                                                        <#assign manualColor = "redtext"/>
                                                    <#elseif (manualPending > 0)>
                                                        <#assign manualColor = "bluetext"/>
                                                    <#elseif (totalManual == 0)>
                                                        <#assign manualColor = "bluetext"/>
                                                    <#else>
                                                        <#assign manualColor = "greentext"/>
                                                    </#if>
                                                    <td class="${manualColor} highlighted-value">${totalManual}</td>
                                                    <td class="${manualColor}">${manualPercentagePassed?string.percent}  </td>
                                                    <td class="greentext">${manualPassed}</td>
                                                    <td class="bluetext">${manualPending}</td>
                                                    <td class="redtext">${manualFailed}</td>
                                                    <td class="lightorangetext">${manualError}</td>
                                                </#if>
                                            </tr>
                                            </#foreach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>

                            <!----->
                            <#if releaseTestOutcomes.tests?has_content >
                            <#--- Test Results -->

                            <div id="test-tabs">
                                <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
                                    <li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active requirementTitle">
                                        <a href="#test-tabs-1">Tests (${releaseTestOutcomes.total})</a>
                                    </li>
                                </ul>
                                <div id="test_list_tests" class="table">
                                    <div class="test-results">
                                        <table id="release-test-results-table">
                                            <thead>
                                            <tr>
                                                <th width="30" class="test-results-heading">&nbsp;</th>
                                                <th width="%" class="test-results-heading">Tests</th>
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
                                                <#assign testResultSet = releaseTestOutcomes.tests >
                                                <#foreach testOutcome in testResultSet>
                                                    <#if testOutcome.result == "PENDING" || testOutcome.result == "IGNORED">
                                                        <#assign testrun_outcome_icon = "pending.png">
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
                                                    <td><img src="images/${testrun_outcome_icon}" title="${testOutcome.result}" class="summary-icon"/><span style="display:none">${testOutcome.result}</span></td>
                                                    <td class="${testOutcome.result}-text"><a href="${relativeLink!}${testOutcome.reportName}.html">${testOutcome.unqualified.titleWithLinks} ${testOutcome.formattedIssues}</a></td>

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
