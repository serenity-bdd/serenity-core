<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8"/>
    <title>Releases</title>
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

            $("#tabs").tabs();
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
    <div id="contenttop">
        <div class="middlebg">
            <span class="bluetext">Releases </span>
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
                <div id="releases">
                    <h3>Release Outline</h3>

                    <div id="release-tree"></div>
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

                </div>

                <div id="release-coverage">
                    <div id="tabs">
                        <ul>
                            <li class="requirementTitle">
                                <a href="#tabs-1">Release Coverage Summary</a></li>
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
                                            <th width="500" class="test-results-heading">Release</th>
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

                                        <#foreach release in releases>
                                            <#assign releaseOutcomes = requirements.getReleasedRequirementsFor(release)/>
                                            <#if releaseOutcomes.testOutcomes.stepCount == 0 || releaseOutcomes.testOutcomes.result == "PENDING" || releaseOutcomes.testOutcomes.result == "IGNORED">
                                                <#assign status_icon = "traffic-yellow.gif">
                                                <#assign status_rank = 0>
                                            <#elseif releaseOutcomes.testOutcomes.result == "ERROR">
                                                <#assign status_icon = "traffic-orange.gif">
                                                <#assign status_rank = 1>
                                            <#elseif releaseOutcomes.testOutcomes.result == "FAILURE">
                                                <#assign status_icon = "traffic-red.gif">
                                                <#assign status_rank = 2>
                                            <#elseif releaseOutcomes.testOutcomes.result == "SUCCESS">
                                                <#assign status_icon = "traffic-green.gif">
                                                <#assign status_rank = 3>
                                            </#if>

                                        <tr class="test-${releaseOutcomes.testOutcomes.result} requirementRow">
                                            <td class="requirementRowCell">
                                                <img src="images/${status_icon}" class="summary-icon"
                                                     title="${releaseOutcomes.testOutcomes.result}"/>
                                                <span style="display:none">${status_rank}</span>
                                            </td>
                                            <td class="release-title">
                                                <#assign releaseReport = reportName.forRelease(release) >
                                                <a href="${releaseReport}">${release.name}</a>
                                            </td>
                                            <#assign totalAutomated = releaseOutcomes.testOutcomes.count("AUTOMATED").withAnyResult()/>
                                            <#assign automatedPassed = releaseOutcomes.testOutcomes.count("AUTOMATED").withResult("SUCCESS")/>
                                            <#assign automatedPending = releaseOutcomes.testOutcomes.count("AUTOMATED").withIndeterminateResult()/>
                                            <#assign automatedFailed = releaseOutcomes.testOutcomes.count("AUTOMATED").withResult("FAILURE")/>
                                            <#assign automatedError = releaseOutcomes.testOutcomes.count("AUTOMATED").withResult("ERROR")/>
                                            <#if (totalAutomated > 0) >
                                                <#assign automatedPercentagePassed =  (automatedPassed / totalAutomated)/>
                                            <#else>
                                                <#assign automatedPercentagePassed = 0.0/>
                                            </#if>

                                            <#assign totalManual = releaseOutcomes.testOutcomes.count("MANUAL").withAnyResult()/>
                                            <#assign manualPending = releaseOutcomes.testOutcomes.count("MANUAL").withIndeterminateResult()/>
                                            <#assign manualPassed = releaseOutcomes.testOutcomes.count("MANUAL").withResult("SUCCESS")/>
                                            <#assign manualFailed = releaseOutcomes.testOutcomes.count("MANUAL").withResult("FAILURE")/>
                                            <#assign manualError = releaseOutcomes.testOutcomes.count("MANUAL").withResult("ERROR")/>
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

                                            <td class="${totalColor} highlighted-value">${totalAutomated}</td>
                                            <td class="${totalColor}">${automatedPercentagePassed?string.percent}</td>
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
                                                <td class="${manualColor}">${manualPercentagePassed?string.percent}</td>
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


                    </div>
                </div>
            </div>
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
