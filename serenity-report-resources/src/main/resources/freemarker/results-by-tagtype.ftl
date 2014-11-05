<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Home</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" href="css/core.css"/>
    <link type="text/css" media="screen" href="css/screen.css" rel="Stylesheet" />
    <link rel="stylesheet" type="text/css" href="jqplot/jquery.jqplot.min.css"/>

    <!--[if IE]>
    <script language="javascript" type="text/javascript" src="jit/Extras/excanvas.js"></script><![endif]-->

    <script type="text/javascript" src="scripts/jquery.js"></script>
    <script type="text/javascript" src="datatables/media/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="jqplot/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="jqplot/plugins/jqplot.pieRenderer.min.js"></script>

    <link type="text/css" href="jqueryui/css/start/jquery-ui-1.8.18.custom.css" rel="Stylesheet" />
    <script type="text/javascript" src="jqueryui/js/jquery-ui-1.8.18.custom.min.js"></script>

    <script class="code" type="text/javascript">$(document).ready(function () {

        // Results table
        $('#tag-list-table').dataTable( {
            "aaSorting": [[ 1, "asc" ]],
            "bJQueryUI": true,
            "iDisplayLength": 25
        } );
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

    <#if (testOutcomes.label == '')>
        <#assign resultsContext = ''>
    <#else>
        <#assign resultsContext = '- ' + testOutcomes.label>
    </#if>
    <#if (reportName.context == '')>
      <#assign contextTitle = ''>
    <#else>
      <#assign contextTitle = (' > ' + inflection.of(reportName.context).asATitle()) >
    </#if>
    <#assign pageTitle = inflection.of(tagType).inPluralForm().asATitle() >
    <div id="contenttop">
        <#--<div class="leftbg"></div>-->
        <div class="middlebg">
            <span class="bluetext"><a href="index.html" class="bluetext">Home</a> ${contextTitle} > ${formatter.truncatedHtmlCompatible(pageTitle,60)}</span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
    <#include "menu.ftl">
    <@main_menu selected="home" />


    <#assign tagTypeTitlePlural = inflection.of(tagType).inPluralForm().asATitle() >
    <#assign tagTypeTitle = inflection.of(tagType).asATitle() >

   <div class="clr"></div>
    <div id="beforetable"></div>
    <div id="results-dashboard">
        <div class="middlb">
            <div class="table">
                <#--- Tag Types -->
                <table>
                 <tr>
                   <td>
                    <div><h3 id="test_list_title">${tagTypeTitlePlural}</h3></div>
                    <div id="test_list_tests" class="table">
                        <div class="test-results">
                            <table id="tag-list-table">
                                <thead>
                                    <tr>
                                        <th width="30" class="test-results-heading">&nbsp;</th>
                                        <th width="750" class="test-results-heading">${tagTypeTitle}</th>
                                        <th width="70" class="test-results-heading">Tests</th>
                                        <th width="70" class="test-results-heading">Steps</th>
                                        <#if reportOptions.showStepDetails>
                                        <th width="65" class="test-results-heading">Fail</th>
                                        <th width="65" class="test-results-heading">Pend</th>
                                        <th width="65" class="test-results-heading">Skip</th>
                                        </#if>
                                        <th width="65" class="test-results-heading">Stable</th>
                                        <th width="65" class="test-results-heading">Duration</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <#foreach tag in testOutcomes.getMostSpecificTagsOfType(tagType)>
                                    <tr><td><h3>${tag.shortName}</h3></td></tr>
                                    <#assign tagTitle = inflection.of(tag.shortName).asATitle() >
                                    <#assign outcomesForTag = testOutcomes.withTag(tag) >
                                    <#assign tagReport = reportName.forTag(tag) >
                                    <#if outcomesForTag.result == "FAILURE">
                                        <#assign outcome_icon = "fail.png">
                                        <#assign outcome_text = "failing-color">
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

                                    <#assign stability = outcomesForTag.recentStability>
                                    <#if (outcomesForTag.total == outcomesForTag.totalTests.withIndeterminateResult())>
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

                                    <tr class="test-${outcomesForTag.result}">
                                        <td><img src="images/${outcome_icon}" title="${outcomesForTag.result}" class="summary-icon"/><span style="display:none">${outcomesForTag.result}</span></td>
                                        <td class="${outcomesForTag.result}-text"><a href="${tagReport}">${tagTitle}</a></td>

                                        <td class="lightgreentext">${outcomesForTag.total}</td>
                                        <td class="lightgreentext">${outcomesForTag.stepCount}</td>

                                        <#if reportOptions.showStepDetails>
                                        <td class="redtext">${outcomesForTag.totalTests.withResult("failure")}</td>
                                        <td class="bluetext">${outcomesForTag..totalTests.withResult("pending")}</td>
                                        <td class="bluetext">${outcomesForTag..totalTests.withResult("skipped")}</td>
                                        </#if>
                                        <td class="bluetext">
                                            <img src="images/${stability_icon}"  class="summary-icon"/>
                                            <span style="display:none">${stability_rank }</span>
                                        </td>
                                        <td class="lightgreentext">${outcomesForTag.duration / 1000}</td>
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
<div id="bottomfooter"></div>

</body>
</html>
