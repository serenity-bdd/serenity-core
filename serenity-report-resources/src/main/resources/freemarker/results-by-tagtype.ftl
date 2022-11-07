<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Home</title>

<#include "libraries/favicon.ftl">

    <#include "libraries/common.ftl">
    <#include "libraries/jquery-ui.ftl">
    <#include "libraries/datatables.ftl">

    <script class="code" type="text/javascript">$(document).ready(function () {

        // Results table
        $('#tag-list-table').DataTable( {
            "order": [
                [ 1, "asc" ]
            ],
            "pageLength": 25
        } );

        $("test-list").tabs();
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
            <span class="breadcrumbs"><a href="index.html">Home</a> ${contextTitle}
                <span class="truncate-60">${formatter.htmlCompatible(pageTitle)}</span>
            </span>
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
                    <div id="test-list">
                        <ul>
                            <li><a href="test-list-1">${tagTypeTitlePlural}</a></li>
                        </ul>
                    </div>
                    <div id="test-list-1" class="table">
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
                                        <th width="65" class="test-results-heading">Total Duration</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <#foreach tag in testOutcomes.getMostSpecificTagsOfType(tagType)>
                                    <tr>
                                        <td>&nbsp;</td>
                                        <td><h3>${tag.shortName}</h3></td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                        <#if reportOptions.showStepDetails>
                                            <td>&nbsp;</td>
                                            <td>&nbsp;</td>
                                            <td>&nbsp;</td>
                                        </#if>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                    </tr>
                                    <#assign tagTitle = inflection.of(tag.shortName).asATitle() >
                                    <#assign outcomesForTag = testOutcomes.withTag(tag) >
                                    <#assign tagReport = reportName.forTag(tag) >
                                    <#assign tag_outcome_icon = formatter.resultIcon().inLarge().forResult(outcomesForTag.result) />

                                    <tr class="test-${outcomesForTag.result}">
                                        <td><span class="summary-icon"/>${tag_outcome_icon}</span><span style="display:none">${outcomesForTag.result}</span></td>
                                        <td class="${outcomesForTag.result}-text"><a href="${tagReport}">${tagTitle}</a></td>

                                        <td class="lightgreentext">${outcomesForTag.total}</td>
                                        <td class="lightgreentext">${outcomesForTag.stepCount}</td>

                                        <#if reportOptions.showStepDetails>
                                        <td class="redtext">${outcomesForTag.totalTests.withResult("failure")}</td>
                                        <td class="bluetext">${outcomesForTag.totalTests.withResult("pending")}</td>
                                        <td class="bluetext">${outcomesForTag.totalTests.withResult("skipped")}</td>
                                        </#if>
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
