<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<#assign pageTitle = inflection.of(requirementType).inPluralForm().asATitle() >
<#assign requirementTypeTitle = inflection.of(requirementType).asATitle() >
<head>
    <meta charset="UTF-8"/>
    <title>${pageTitle}</title>
    <link rel="shortcut icon" href="favicon.ico">

    <#include "libraries/common.ftl">
    <#include "libraries/jquery-ui.ftl">
    <#include "libraries/datatables.ftl">
    <#assign pie = true>
    <#include "libraries/jqplot.ftl">
    <#include "components/requirements-list.ftl">

<#assign successfulManualTests = (requirements.count("manual").withResult("SUCCESS") > 0)>
<#assign pendingManualTests = (requirements.count("manual").withIndeterminateResult() > 0)>
<#assign failingManualTests = (requirements.count("manual").withResult("FAILURE") > 0)>

    <script class="code" type="text/javascript">$(document).ready(function () {

        // Results table
        $('#req-results-table').DataTable({
            "order": [
                [ 2, "asc" ]
            ],
            "pageLength": 25
        });
        $('#test-results-table').DataTable({
            "order": [
                [ 2, "asc" ]
            ],
            "pageLength": 25
        });
        $('#examples-table').DataTable({
            "order": [
                [ 2, "asc" ]
            ],
            "pageLength": 25
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

<body class="results-page">
<div id="topheader">
    <div id="topbanner">
        <div id="logo"><a href="index.html"><img src="images/serenity-logo.png" border="0"/></a></div>
        <div id="projectname-banner" style="float:right">
            <span class="projectname">${reportOptions.projectName}</span>
        </div>
    </div>
</div>


<div class="middlecontent">
<div id="contenttop">
    <div class="middlebg">
        <span class="breadcrumbs"><a href="index.html">Home</a>
            > ${formatter.truncatedHtmlCompatible(pageTitle,60)}
        </span>
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
    <#assign workingRequirementsTitle = inflection.of(requirements.type).inPluralForm().asATitle() >
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
                <@requirements_results requirements=requirements title=requirementTypeTitle requirementType=workingRequirementsTitle id="req-results-table"/>
            </div>
        </#if>
    </div>
</#if>
</div>
</div>
</div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter">
<span class="version">Serenity BDD version ${serenityVersionNumber}</span>
</div>


</body>
</html>
