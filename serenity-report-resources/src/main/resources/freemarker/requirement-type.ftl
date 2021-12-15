<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<#assign pageTitle = inflection.of(requirementType).inPluralForm().asATitle() >
<#assign requirementTypeTitle = inflection.of(requirementType).asATitle() >
<head>
    <meta charset="UTF-8"/>
    <title>${pageTitle}</title>
<#include "libraries/favicon.ftl">

<#include "libraries/common.ftl">
<#include "libraries/jquery-ui.ftl">
<#include "libraries/datatables.ftl">
<#include "components/requirements-list.ftl">

<#assign pie = true>
<#assign successfulManualTests = (requirements.count("manual").withResult("SUCCESS") > 0)>
<#assign pendingManualTests = (requirements.count("manual").withIndeterminateResult() > 0)>
<#assign failingManualTests = (requirements.count("manual").withResult("FAILURE") > 0)>

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
        <span class="breadcrumbs"><a href="index.html">Home</a>
            > <span class="truncate-60">${formatter.htmlCompatible(pageTitle)}</span>
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

            <#if (requirements.visibleOutcomes?has_content || testOutcomes.total > 0)>
                <#assign workingRequirementsTitle = inflection.of(requirementType).inPluralForm().asATitle() >

                <@requirements_results requirements=requirements title=requirementTypeTitle requirementType=workingRequirementsTitle id="requirements-table"/>

            </#if>
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
