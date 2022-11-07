<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8"/>
    <title>${release.name}</title>
<#include "libraries/favicon.ftl">

    <#include "libraries/common.ftl">
    <#include "libraries/jquery-ui.ftl">
    <#include "libraries/datatables.ftl">
    <#include "libraries/jqtree.ftl">

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
            $('#req-results-table').DataTable({
                "order": [
                    [ 2, "asc" ]
                ],
                "pageLength": 25
            });

            // Results table
            $('#release-test-results-table').DataTable({
                "order": [
                    [ 2, "asc" ]
                ]
            });

            $("#tabs").tabs();
            $("#test-tabs").tabs();
        });
    </script>
</head>

<body>
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
            <div>
                <div id="releases">
                    <h3>Release Details</h3>
                    <table>
                        <tr>
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
                        <div id="tabs">
                            <ul>
                                <li class="requirementTitle">
                                    <a href="#tabs-1">Scheduled Requirements</a></li>
                            </ul>
                            <!----->

                            <div id="tabs-1" class="capabilities-table table">
                            <#--- Requirements -->
                                <div id="req_list_tests" class="table">
                                    <div class="test-results">
                                        <table id="req-results-table">
                                            <thead>
                                            <tr>
                                                <th width="40" class="test-results-heading">&nbsp;</th>
                                                <#if secondLevelRequirementTypeTitle??>
                                                    <th width="250" class="test-results-heading">${requirementType}</th>
                                                    <th width="250" class="test-results-heading">${secondLevelRequirementTypeTitle}</th>
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
                        </div>

                        <!----->
                        <#if releaseTestOutcomes.tests?has_content >
                        <#--- Test Results -->

                        <div id="test-tabs">
                            <ul>
                                <li class="requirementTitle">
                                    <a href="#test-tabs-1">Tests (${releaseTestOutcomes.total})</a>
                                </li>
                            </ul>
                            <div id="test-tabs-1" class="capabilities-table table">
                                <div class="table">
                                    <div class="test-results">
                                        <table id="release-test-results-table">
                                            <thead>
                                            <tr>
                                                <th width="50px" class="test-results-heading">&nbsp;</th>
                                                <th class="test-results-heading">Tests</th>
                                                <th width="70px" class="test-results-heading">Steps</th>
                                                <#if reportOptions.showStepDetails>
                                                    <th width="65px" class="test-results-heading">Fail</th>
                                                    <th width="65px" class="test-results-heading">Errors</th>
                                                    <th width="65px" class="test-results-heading">Pend</th>
                                                    <th width="65px" class="test-results-heading">Skip</th>
                                                    <th width="65px" class="test-results-heading">Abort</th>
                                                    <th width="65px" class="test-results-heading">Ignore</th>
                                                </#if>
                                                <th width="100px" class="test-results-heading">Total Duration<br>(seconds)</th>
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

                                                <tr class="test-${testOutcome.result}">
                                                    <td>
                                                        <img src="images/${testrun_outcome_icon}" title="${testOutcome.result}" class="summary-icon"/>
                                                        <span style="display:none">${testOutcome.result}</span>
                                                        <#if (testOutcome.manual)>
                                                            <i class="bi bi-person fa-2x"  title="Manual test"></i>
                                                        </#if>
                                                    </td>
                                                    <td class="${testOutcome.result}-text"><a href="${relativeLink!}${testOutcome.reportName}.html">${testOutcome.unqualified.titleWithLinks} ${testOutcome.formattedIssues}</a></td>

                                                    <td class="lightgreentext">${testOutcome.nestedStepCount}</td>
                                                    <#if reportOptions.showStepDetails>
                                                        <td class="redtext">${testOutcome.failureCount}</td>
                                                        <td class="redtext">${testOutcome.errorCount}</td>
                                                        <td class="bluetext">${testOutcome.pendingCount}</td>
                                                        <td class="bluetext">${testOutcome.skippedCount}</td>
                                                        <td class="bluetext">${testOutcome.abortedCount}</td>
                                                        <td class="bluetext">${testOutcome.ignoredCount}</td>
                                                    </#if>
                                                    <td class="lightgreentext">${testOutcome.durationInSeconds}</td>
                                                </tr>
                                                </#foreach>
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
        </div>
    </div>
</div>
<div id="beforefooter"></div>
    <div id="bottomfooter">
        <span class="version">Serenity BDD version ${serenityVersionNumber!"SNAPSHOT-BUILD"}</span>
    </div>


</body>
</html>
