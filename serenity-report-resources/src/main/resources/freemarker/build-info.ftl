<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Serenity Reports</title>

    <link rel="shortcut icon" href="favicon.ico">

    <#include "libraries/common.ftl">
    <#include "libraries/jquery-ui.ftl">
    <#include "libraries/datatables.ftl">

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
    <#--<div class="leftbg"></div>-->
        <div class="middlebg">
            <span class="breadcrumbs"><a href="index.html">Home</a> > Environment details</span>
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
                <h2>Environment details</h2>

                <#assign keys = build.generalProperties?keys>
                <table class="table table-striped build-info">
                    <#list keys as key>
                    <tr>
                        <td style="width: 30%">${key}</td>
                        <td>${build.generalProperties[key]}</td>
                    </tr>
                    </#list>
                </table>

                <#assign drivers = build.drivers>
                <#list drivers as driver>
                    <h2>Driver capabilities: ${driver}</h2>
                    <#assign driverCapabilities = build.driverProperties[driver]>
                    <#assign capabilityKeys = driverCapabilities?keys>
                    <table class="table table-striped build-info">
                        <#list capabilityKeys as capability>
                            <tr>
                                <td style="width: 30%">${capability}</td><td>${driverCapabilities[capability]}</td>
                            </tr>
                        </#list>
                    </table>
                </#list>

                <#assign sectionTitles = build.sectionTitles>
                <#list sectionTitles as sectionTitle>
                    <h2>${sectionTitle}</h2>
                    <#assign sectionValues = build.sections[sectionTitle]>
                    <#assign sectionValueLabels = sectionValues?keys>
                    <table class="table table-striped build-info">
                        <#list sectionValueLabels as section>
                            <tr>
                                <td style="width: 30%">${section}</td><td>${sectionValues[section]}</td>
                            </tr>
                        </#list>
                    </table>
                </#list>

            </div>
        <#--- Test Results end -->
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
