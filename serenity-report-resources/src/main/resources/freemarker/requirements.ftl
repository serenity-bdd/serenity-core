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
<#include "libraries/favicon.ftl">

<#include "libraries/common.ftl">
<#include "libraries/jquery-ui.ftl">
<#include "libraries/datatables.ftl">
<#assign pie = true>
<#--<#include "libraries/jqplot.ftl">-->

    <link type="text/css" rel="stylesheet" href="tocify/jquery.tocify.css" />
    <script src="tocify/jquery.tocify.min.js""></script>

<#include "components/test-outcomes.ftl">
<#include "components/requirements-list.ftl">

<#list requirements.types as requirementType>
    <#assign requirementsOfType = requirements.requirementsOfType(requirementType) />
    <#assign total = requirementsOfType.totalRequirements />
    <#assign successfulRequirements= requirementsOfType.completedRequirementsCount>
    <#assign pendingRequirements = requirementsOfType.pendingRequirementsCount>
    <#assign ignoredRequirements = requirementsOfType.ignoredRequirementsCount >
    <#assign skippedRequirements = requirementsOfType.skippedRequirementsCount >
    <#assign failingRequirements = requirementsOfType.failingRequirementsCount >
    <#assign errorRequirements = requirementsOfType.errorRequirementsCount  >
    <#assign compromisedRequirements = requirementsOfType.compromisedRequirementsCount  >

    <#assign successfulRequirementsAsPercentage = 100*requirementsOfType.completedRequirementsCount/total >
    <#assign pendingRequirementsAsPercentage = 100*requirementsOfType.pendingRequirementsCount/total>
    <#assign ignoredRequirementsAsPercentage = 100*requirementsOfType.ignoredRequirementsCount/total >
    <#assign skippedRequirementsAsPercentage = 100*requirementsOfType.skippedRequirementsCount/total >
    <#assign failingRequirementsAsPercentage = 100*requirementsOfType.failingRequirementsCount/total >
    <#assign errorRequirementsAsPercentage = 100*requirementsOfType.errorRequirementsCount/total  >
    <#assign compromisedRequirementsAsPercentage = 100*requirementsOfType.compromisedRequirementsCount/total  >

    <#assign untesteddRequirements = requirementsOfType.requirementsWithoutTestsCount >

    <#assign testResultData = requirementCounts.byType() >
    <#assign testLabels = requirementCounts.percentageLabelsByType() >
    <#assign graphType="aggregated-results"/>

<#else>

    <#assign testResultData = resultCounts.byTypeFor("success","pending","ignored","skipped","failure","error","compromised") >
    <#assign testLabels = resultCounts.percentageLabelsByTypeFor("success","pending","ignored","skipped","failure","error","compromised") >
    <#assign graphType="automated-and-manual-results"/>

    <#assign totalTests = testOutcomes.totalTests >
    <#assign total = totalTests.total />
    <#assign successfulRequirements= totalTests.withResult("success") >
    <#assign pendingRequirements = totalTests.withResult("pending") >
    <#assign ignoredRequirements = totalTests.withResult("ignored") >
    <#assign skippedRequirements = totalTests.withResult("skipped")>
    <#assign failingRequirements = totalTests.withResult("failure") >
    <#assign errorRequirements = totalTests.withResult("error") >
    <#assign compromisedRequirements = totalTests.withResult("compromised") >

    <#assign successfulRequirementsAsPercentage = totalTests.percentageWithResult("success") >
    <#assign pendingRequirementsAsPercentage = totalTests.percentageWithResult("pending") >
    <#assign ignoredRequirementsAsPercentage = totalTests.percentageWithResult("ignored") >
    <#assign skippedRequirementsAsPercentage = totalTests.percentageWithResult("skipped")>
    <#assign failingRequirementsAsPercentage = totalTests.percentageWithResult("failure") >
    <#assign errorRequirementsAsPercentage = totalTests.percentageWithResult("error") >
    <#assign compromisedRequirementsAsPercentage = totalTests.percentageWithResult("compromised") >

    <#assign untesteddRequirements = 0 >

</#list>


    <#if (successfulRequirementsAsPercentage == 0)>
        <#assign successfulRequirementsLabel = " " >
    <#else>
        <#assign successfulRequirementsLabel = successfulRequirementsAsPercentage + "%" >
    </#if>
    <#if (pendingRequirementsAsPercentage == 0)>
        <#assign pendingRequirementsLabel = " " >
    <#else>
        <#assign pendingRequirementsLabel = pendingRequirementsAsPercentage + "%" >
    </#if>
    <#if (ignoredRequirementsAsPercentage == 0)>
        <#assign ignoredRequirementsLabel = " " >
    <#else>
        <#assign ignoredRequirementsLabel = ignoredRequirementsAsPercentage + "%" >
    </#if>
    <#if (skippedRequirementsAsPercentage == 0)>
        <#assign skippedRequirementsLabel = " " >
    <#else>
        <#assign skippedRequirementsLabel = skippedRequirementsAsPercentage + "%" >
    </#if>
    <#if (failingRequirementsAsPercentage == 0)>
        <#assign failingRequirementsLabel = " " >
    <#else>
        <#assign failingRequirementsLabel = failingRequirementsAsPercentage + "%" >
    </#if>
    <#if (errorRequirementsAsPercentage == 0)>
        <#assign errorRequirementsLabel = " " >
    <#else>
        <#assign errorRequirementsLabel = errorRequirementsAsPercentage + "%" >
    </#if>
    <#if (compromisedRequirementsAsPercentage == 0)>
        <#assign compromisedRequirementsLabel = " " >
    <#else>
        <#assign compromisedRequirementsLabel = compromisedRequirementsAsPercentage + "%" >
    </#if>


    <script class="code" type="text/javascript">$(document).ready(function () {

        // Results table
        $('#test-results-table').DataTable({
            "order": [
                [1, "asc"]
            ],
            "pageLength": 100,
            "lengthMenu": [[50, 100, 200, -1], [50, 100, 200, "All"]]
        });

        // Results table
        $('#req-results-table').DataTable({
            "order": [
                [1, "asc"]
            ],
            "pageLength": 100,
            "lengthMenu": [[50, 100, 200, -1], [50, 100, 200, "All"]]
        });

        $('#examples-table').DataTable({
            "order": [
                [2, "asc"]
            ],
            "pageLength": 25
        });
        $("#requirements-tabs").tabs();
        $("#test-tabs").tabs();

        var requirementsTree = ${requirementsTree};

        $('#tree').treeview({
            data: requirementsTree,
            enableLinks: true,
            showTags: true,
            expandIcon: "glyphicon glyphicon-folder-close",
            collapseIcon: "glyphicon glyphicon-folder-open",
            emptyIcon: "glyphicon glyphicon-book"
        });
    });
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

    <script>
        $(document).ready(function () {
            $(".scenario-docs p").html(function(){
                var text= $(this).text().trim().split(" ");
                var first = text.shift();
                return (text.length > 0 ? "<strong>"+ first + "</strong> " : first) + text.join(" ");
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

<#if (requirements.parentRequirement.isPresent())>
    <#assign parentRequirement = requirements.parentRequirement.get() >
    <#assign parentTitle = inflection.of(parentRequirement.displayName).asATitle() >
    <#assign parentType = inflection.of(parentRequirement.type).asATitle() >
    <#if (parentRequirement.cardNumber?has_content) >
        <#assign issueNumber = "[" + reportFormatter.addLinks(parentRequirement.cardNumber) + "]" >
    <#else>
        <#assign issueNumber = "">
    </#if>
</#if>

<div class="middlecontent">
    <div id="contenttop">
        <div class="middlebg">
            <span class="breadcrumbs">
            <a href='index.html'>Home</a> > <a href="capabilities.html">Requirements</a>

            <#list breadcrumbs as breadcrumb>
                <#assign breadcrumbReport = absoluteReportName.forRequirement(breadcrumb) />
                <#assign breadcrumbTitle = inflection.of(breadcrumb.shortName).asATitle() >
                > <a href="${breadcrumbReport}">${formatter.htmlCompatibleStoryTitle(breadcrumbTitle)}</a>
            </#list>

            <#if requirements.parentRequirement.isPresent()>
                <#assign parent = requirements.parentRequirement.get()>
                <#assign parentTitle = inflection.of(parent.displayName).asATitle() >

                > <span class="truncate-40">${formatter.htmlCompatibleStoryTitle(parentTitle)}</span>
            </#if>

            </span>
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
                    <h2><i class="fa fa-book"></i> ${parentType}
                        : ${issueNumber} ${formatter.htmlCompatibleStoryTitle(parentTitle)}</h2>
                    <#if parentRequirement.narrative.renderedText?has_content>
                        <div class="requirementNarrativeTitle">
                            ${formatter.renderDescriptionWithEmbeddedResults(parentRequirement.narrative.renderedText, requirements)}
                        </div>
                    </#if>

                    <#foreach customField in parentRequirement.customFields >
                        <#if parentRequirement.getCustomField(customField).present>
                            <div>
                                <a href="javaScript:void(0)" class="read-more-link">
                                    <i class="fa fa-plus-square-o"></i>
                                    <span class="custom-field-title">${customField}</span>
                                </a>

                                <div class="requirementNarrativeField read-more-text">${parentRequirement.getCustomField(customField).get().renderedText}</div>
                            </div>
                        </#if>
                    </#foreach>
                </div>
            </#if>


            <#if (requirements.overview?has_content)>
                <div class="requirements-overview panel panel-default">
                    <div class="panel-body">
                        ${formatter.addLineBreaks(formatter.renderDescription(requirements.overview))}
                    </div>
                </div>
            </#if>
                <div>

                    <!-- Requirements result tabs -->
                    <ul class="nav nav-tabs">
                        <li class="active">
                            <a data-toggle="tab" href="#specs"><i class="fas fa-comments"></i> Specifications</a>
                        </li>
                        <li>
                            <a data-toggle="tab" href="#results"><i class="fas fa-tachometer-alt"></i> Test Results</a>
                        </li>
                    </ul>

                    <!-- Specifications and Results tabs -->
                    <div class="card border">
                        <div class="tab-content" id="pills-tabContent">
                            <div id="specs" class="tab-pane fade in active">

                                <div class="container">
                                    <div class="row">
                                        <div class="col-sm-10">

                                        <#if !isLeafRequirement>
                                            <h3>Requirements Overview</h3>

                                            <div id="tree"></div>

                                        <#else>

                                            <h3>Scenarios</h3>

                                            <div id="toc">
                                                <table class="table" id="toc-table">
                                                    <#list scenarios as scenario>
                                                        <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />
                                                       <tr>
                                                           <td><a href="#${scenario.id}">${scenario.title}</a></li></td>
                                                           <td>${outcome_icon}
                                                               <#if (scenario.manual)> <i class="fa fa-user manual" title="Manual test"></i></#if>
                                                           </td>
                                                           <td>
                                                               <#if outcome_icon?has_content>
                                                                   <a href="${scenario.scenarioReport}" class="badge more-details ${scenario.resultStyle}">Details</a>
                                                               </#if>
                                                           </td>
                                                       </tr>
                                                    </#list>

                                                </table>

                                            </div>

                                            <h3>Scenario details</h3>
                                            <#list scenarios as scenario>

                                                <div class="scenario-docs card" id="${scenario.id}">
                                                    <div class="scenario-docs card-header ${scenario.resultStyle}">
                                                        <span class="scenario-heading">
                                                            <a href="${scenario.scenarioReport}" title="More details...">${scenario.title}</a>
                                                        </span>
                                                        <span class="scenario-result">
                                                            <#if (scenario.manual)> <i class="fa fa-user manual" title="Manual test"></i></#if>
                                                            <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />
                                                            ${outcome_icon}
                                                        </span>
                                                    </div>
                                                    <div class="scenario-docs card-body">
                                                        <div class="scenario-text">${formatter.renderDescription(scenario.description)}</div>
                                                        <#list scenario.steps as step>
                                                            <p>${formatter.renderDescription(step)}</p>
                                                        </#list>
                                                        <div class="examples">
                                                        <#list scenario.examples as example>
                                                            <p>${formatter.renderDescription(example)}</p>
                                                        </#list>
                                                         </div>

                                                    </div>
                                                </div>
                                            </#list>
                                        </#if>
                                        </div>
                                    </div>
                                </div>
                            </div>


                            <div id="results" class="tab-pane fade">
                                <div class="container">
                                    <div class="row">
                                        <div class="col-sm-4">

                                            <div class="chart-container ${graphType}">
                                                <div class="ct-chart ct-square"></div>
                                            </div>
                                            <script>

                                                var labels = ${testLabels};
                                                    // Our series array that contains series objects or in this case series data arrays

                                                var series = ${testResultData};

                                                // As options we currently only set a static size of 300x200 px. We can also omit this and use aspect ratio containers
                                                // as you saw in the previous example
                                                var options = {
                                                    width: 350,
                                                    height: 300
                                                };

                                                $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
                                                    // $(e.currentTarget.hash).find('.ct-chart').each(function(el, tab) {
                                                    //   tab.__chartist__.update();
                                                    // });
                                                    new Chartist.Pie('.ct-chart', {
                                                        series: series,
                                                        labels: labels,
                                                    }, {
                                                        donut: true,
                                                        donutWidth: 60,
                                                        donutSolid: true,
                                                        startAngle: 270,
                                                        showLabel: true
                                                    }, options);
                                                });

                                            </script>
                                        </div>
                                        <div class="col-sm-6">
                                            <table class="table">
                                                <thead>
                                                <tr>
                                                    <th scope="col">Scenarios</th>
                                                    <th scope="col" colspan="2" class="automated-stats" >Automated</th>
                                                    <#if resultCounts.hasManualTests() >
                                                        <th scope="col" colspan="2" class="manual-stats"> Manual</th>
                                                        <th scope="col" colspan="2" class="total-stats"> Total</th>
                                                    </#if>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <tr>
                                                    <td class="aggregate-result-count"><i class='fa fa-check-circle-o success-icon'></i>&nbsp;Passing</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestCount("success")}</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestPercentage("success")}</td>
                                                    <#if resultCounts.hasManualTests() >
                                                    <td class="manual-stats">${resultCounts.getManualTestCount("success")}</td>
                                                    <td class="manual-stats">${resultCounts.getManualTestPercentage("success")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestCount("success")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestPercentage("success")}</td>
                                                    </#if>
                                                </tr>
                                                <tr>
                                                    <td class="aggregate-result-count"><i class='fa fa-stop-circle-o pending-icon'></i>&nbsp;Pending</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestCount("pending")}</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestPercentage("pending")}</td>
                                                    <#if resultCounts.hasManualTests() >
                                                    <td class="manual-stats">${resultCounts.getManualTestCount("pending")}</td>
                                                    <td class="manual-stats">${resultCounts.getManualTestPercentage("pending")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestCount("pending")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestPercentage("pending")}</td>
                                                    </#if>
                                                </tr>
                                                <tr>
                                                    <td class="aggregate-result-count"><i class='fa fa-ban ignored-icon'></i>&nbsp;Ignored</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestCount("ignored")}</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestPercentage("ignored")}</td>
                                                    <#if resultCounts.hasManualTests() >
                                                    <td class="manual-stats">${resultCounts.getManualTestCount("ignored")}</td>
                                                    <td class="manual-stats">${resultCounts.getManualTestPercentage("ignored")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestCount("ignored")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestPercentage("ignored")}</td>
                                                    </#if>
                                                </tr>
                                                <tr>
                                                    <td class="aggregate-result-count"><i class='fa fa-fast-forward skip-icon'></i>&nbsp;Skipped</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestCount("skipped")}</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestPercentage("skipped")}</td>
                                                    <#if resultCounts.hasManualTests() >
                                                    <td class="manual-stats">${resultCounts.getManualTestCount("skipped")}</td>
                                                    <td class="manual-stats">${resultCounts.getManualTestPercentage("skipped")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestCount("skipped")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestPercentage("skipped")}</td>
                                                    </#if>
                                                </tr>
                                                <tr>
                                                    <td class="aggregate-result-count"><i class='fa fa-times-circle failure-icon'></i>&nbsp;Failed</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestCount("failure")}</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestPercentage("failure")}</td>
                                                    <#if resultCounts.hasManualTests() >
                                                    <td class="manual-stats">${resultCounts.getManualTestCount("failure")}</td>
                                                    <td class="manual-stats">${resultCounts.getManualTestPercentage("failure")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestCount("failure")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestPercentage("failure")}</td>
                                                    </#if>
                                                <tr>
                                                    <td class="aggregate-result-count"><i
                                                            class='fa fa-exclamation-triangle error-icon'></i>&nbsp;Broken</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestCount("error")}</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestPercentage("error")}</td>
                                                    <#if resultCounts.hasManualTests() >
                                                    <td class="manual-stats">${resultCounts.getManualTestCount("error")}</td>
                                                    <td class="manual-stats">${resultCounts.getManualTestPercentage("error")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestCount("error")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestPercentage("error")}</td>
                                                    </#if>
                                                <tr>
                                                    <td class="aggregate-result-count"><i class='fa fa-chain-broken compromised-icon'></i>&nbsp;Compromised</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestCount("compromised")}</td>
                                                    <td class="automated-stats">${resultCounts.getAutomatedTestPercentage("compromised")}</td>
                                                    <#if resultCounts.hasManualTests() >
                                                    <td class="manual-stats">${resultCounts.getManualTestCount("compromised")}</td>
                                                    <td class="manual-stats">${resultCounts.getManualTestPercentage("compromised")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestCount("compromised")}</td>
                                                    <td class="total-stats">${resultCounts.getOverallTestPercentage("compromised")}</td>
                                                    </#if>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-sm-10">

                                        <#if isLeafRequirement>


                                            <table class="scenario-result table">
                                                <tr>
                                                    <th>Scenario</th>
                                                    <th>Steps</th>
                                                    <th>Start Time</th>
                                                    <th>Duration</th>
                                                    <th>Result</th>
                                                </tr>
                                                <#list scenarios as scenario>
                                                    <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />
                                                       <tr>
                                                           <td>
                                                               <#if (scenario.manual)> <i class="fa fa-user manual" title="Manual test"></i></#if>
                                                               <a href="${scenario.scenarioReport}">${scenario.title}</a></li></td>
                                                           <td>${scenario.stepCount}</td>
                                                           <td>${scenario.formattedStartTime}</td>
                                                           <td>${scenario.formattedDuration}</td>
                                                           <td>${outcome_icon}</td>
                                                       </tr>
                                                </#list>
                                            </table>
                                        </#if>
                                        </div>
                                    </div>
                                </div>


                    <!--
                    -->

                    <#--<div class="card border">-->
                        <#--<div class="tab-content" id="pills-tabContent">-->
                            <#--<div id="specs" class="tab-pane fade in active">-->

                                <#--<div class="container">-->
                                    <#--<div class="row">-->
                                        <#--<div class="col-sm">-->
                                            <#--<p>Overview</p>-->
                                        <#--</div>-->
                                        <#--<div class="col-sm">-->
                                            <#--<p>Details</p>-->
                                        <#--</div>-->
                                    <#--</div>-->
                                <#--</div>-->
                            <#--</div>-->


                            <#--<div id="results" class="tab-pane fade">-->
                                <#--<div class="container">-->
                                    <#--<div class="row">-->
                                        <#--<div class="col-sm-4">-->

                                            <#--<div class="chart-container">-->
                                                <#--<div class="ct-chart ct-square"></div>-->
                                            <#--</div>-->
                                            <#--<script>-->

                                                <#--var data = {-->
                                                    <#--// A labels array that can contain any sort of values-->
                                                    <#--labels: [-->
                                                        <#--'${successfulRequirementsLabel}',-->
                                                        <#--'${pendingRequirementsLabel}',-->
                                                        <#--'${ignoredRequirementsLabel}',-->
                                                        <#--'${skippedRequirementsLabel}',-->
                                                        <#--'${failingRequirementsLabel}',-->
                                                        <#--'${errorRequirementsLabel}',-->
                                                        <#--'${compromisedRequirementsLabel}'-->
                                                    <#--],-->
                                                    <#--// Our series array that contains series objects or in this case series data arrays-->
                                                    <#--series: [-->
                                                    <#--${successfulRequirements},-->
                                                    <#--${pendingRequirements},-->
                                                    <#--${ignoredRequirements},-->
                                                    <#--${skippedRequirements},-->
                                                    <#--${failingRequirements},-->
                                                    <#--${errorRequirements},-->
                                                    <#--${compromisedRequirements}-->
                                                    <#--]-->
                                                <#--};-->

                                                <#--// As options we currently only set a static size of 300x200 px. We can also omit this and use aspect ratio containers-->
                                                <#--// as you saw in the previous example-->
                                                <#--var options = {-->
                                                    <#--width: 350,-->
                                                    <#--height: 300-->
                                                <#--};-->

                                                <#--$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {-->
                                                    <#--// $(e.currentTarget.hash).find('.ct-chart').each(function(el, tab) {-->
                                                    <#--//   tab.__chartist__.update();-->
                                                    <#--// });-->
                                                    <#--new Chartist.Pie('.ct-chart', {-->
                                                        <#--series: [-->
                                                        <#--${successfulRequirements},-->
                                                        <#--${pendingRequirements},-->
                                                        <#--${ignoredRequirements},-->
                                                        <#--${skippedRequirements},-->
                                                        <#--${failingRequirements},-->
                                                        <#--${errorRequirements},-->
                                                        <#--${compromisedRequirements}-->
                                                        <#--],-->
                                                        <#--labels: [-->
                                                            <#--'${successfulRequirementsLabel}',-->
                                                            <#--'${pendingRequirementsLabel}',-->
                                                            <#--'${ignoredRequirementsLabel}',-->
                                                            <#--'${skippedRequirementsLabel}',-->
                                                            <#--'${failingRequirementsLabel}',-->
                                                            <#--'${errorRequirementsLabel}',-->
                                                            <#--'${compromisedRequirementsLabel}'-->
                                                        <#--],-->
                                                    <#--}, {-->
                                                        <#--donut: true,-->
                                                        <#--donutWidth: 60,-->
                                                        <#--donutSolid: true,-->
                                                        <#--startAngle: 270,-->
                                                        <#--showLabel: true-->
                                                    <#--}, options);-->
                                                <#--});-->

                                            <#--</script>-->
                                        <#--</div>-->
                                        <#--<div class="col-sm-6">-->
                                            <#--<table class="table">-->
                                                <#--<thead>-->
                                                <#--<tr>-->
                                                    <#--<th scope="col">Scenarios</th>-->
                                                    <#--<th scope="col">Count</th>-->
                                                    <#--<th scope="col">%</th>-->
                                                <#--</tr>-->
                                                <#--</thead>-->
                                                <#--<tbody>-->
                                                <#--<tr>-->
                                                    <#--<td style="padding-left:2em;"><strong>Automated</strong></td>-->
                                                    <#--<td></td>-->
                                                    <#--<td></td>-->
                                                <#--</tr>-->
                                                <#--<tr>-->
                                                    <#--<td style="padding-left:4em;"><i class='fa fa-check-circle-o success-icon'></i>&nbsp;Passing-->
                                                    <#--</td>-->
                                                    <#--<td>${successfulRequirements}</td>-->
                                                    <#--<td>${successfulRequirementsAsPercentage}%</td>-->
                                                <#--</tr>-->
                                                <#--<tr>-->
                                                    <#--<td style="padding-left:4em;"><i class='fa fa-stop-circle-o pending-icon'></i>&nbsp;Pending-->
                                                    <#--</td>-->
                                                    <#--<td>${pendingRequirements}</td>-->
                                                    <#--<td>${pendingRequirementsAsPercentage}%</td>-->
                                                <#--</tr>-->
                                                <#--<tr>-->
                                                    <#--<td style="padding-left:4em;"><i class='fa fa-ban ignored-icon'></i>&nbsp;Ignored-->
                                                    <#--</td>-->
                                                    <#--<td>${ignoredRequirements}</td>-->
                                                    <#--<td>${ignoredRequirementsAsPercentage}%</td>-->
                                                <#--</tr>-->
                                                <#--<tr>-->
                                                    <#--<td style="padding-left:4em;"><i class='fa fa-fast-forward skip-icon'></i>&nbsp;Skipped-->
                                                    <#--</td>-->
                                                    <#--<td>${skippedRequirements}</td>-->
                                                    <#--<td>${skippedRequirementsAsPercentage}%</td>-->
                                                <#--</tr>-->
                                                <#--<tr>-->
                                                    <#--<td style="padding-left:4em;"><i class='fa fa-times-circle failure-icon'></i>&nbsp;Failed-->
                                                    <#--</td>-->
                                                    <#--<td>${failingRequirements}</td>-->
                                                    <#--<td>${failingRequirementsAsPercentage}%</td>-->
                                                <#--</tr>-->
                                                <#--<tr>-->
                                                    <#--<td style="padding-left:4em;"><i-->
                                                            <#--class='fa fa-exclamation-triangle error-icon'></i>&nbsp;Broken-->
                                                    <#--</td>-->
                                                    <#--<td>${errorRequirements}</td>-->
                                                    <#--<td>${errorRequirementsAsPercentage}%</td>-->
                                                <#--</tr>-->
                                                <#--<tr>-->
                                                    <#--<td style="padding-left:4em;"><i class='fa fa-chain-broken compromised-icon'></i>&nbsp;Compromised-->
                                                    <#--</td>-->
                                                    <#--<td>${compromisedRequirements}</td>-->
                                                    <#--<td>${compromisedRequirementsAsPercentage}%</td>-->
                                                <#--</tr>-->
                                                <#--</tbody>-->
                                            <#--</table>-->
                                        <#--</div>-->
                                    <#--</div>-->
                                <#--</div>-->

                                <#--<!---->
                                <#if (requirements.totalTestCount > 0 || requirements.flattenedRequirementCount > 0)>
                                    <#assign untestedCount = 0>
                                    <#foreach requirementType in requirements.types>
                                        <#assign untestedCount = untestedCount + requirements.requirementsOfType(requirementType).requirementsWithoutTestsCount>
                                    </#foreach>
                                    <div id="requirements-summary">
                                        <div id="coverage_pie_chart"
                                             style="margin-top:10px; margin-left:10px; width:250px; height:250px;"></div>
                                        <div id="coverage_summary">
                                            <div>
                                                <h4>Requirements Overview</h4>
                                                <table class="summary-table">
                                                    <head>
                                                        <tr>
                                                            <th>Requirement Type</th>
                                                            <th>Total</th>
                                                            <th>Pass&nbsp;<i class="icon-check"></i></th>
                                                            <th>Fail&nbsp;<i class="icon-thumbs-down"></i></th>
                                                            <th>Pending&nbsp;<i class="icon-calendar"></i></th>
                                                            <th>Ignored&nbsp;<i class="icon-ban-circle"></i></th>
                                                            <#if (untestedCount > 0)>
                                                                <th>Untested&nbsp;<i class="icon-question"></i></th>
                                                            </#if>
                                                        </tr>
                                                    </head>
                                                    <body>
                                                        <#foreach requirementType in requirements.types>
                                                        <tr>
                                                            <#if requirements.type == requirementType>
                                                                <#assign requirementReport = "#requirements-tabs" />
                                                            <#else>
                                                                <#assign requirementReport = reportName.forRequirementType(requirementType) />
                                                            </#if>
                                                            <#assign requirementTitle = inflection.of(requirementType).inPluralForm().asATitle() />
                                                            <td class="summary-leading-column"><a
                                                                    href="${requirementReport}">${requirementTitle}</a>
                                                            </td>
                                                            <td>${requirements.requirementsOfType(requirementType).requirementCount}</td>
                                                            <td>${requirements.requirementsOfType(requirementType).completedRequirementsCount}</td>
                                                            <td>${requirements.requirementsOfType(requirementType).failingRequirementsCount}</td>
                                                            <td>${requirements.requirementsOfType(requirementType).pendingRequirementsCount}</td>
                                                            <td>${requirements.requirementsOfType(requirementType).ignoredRequirementsCount}</td>
                                                            <#if (untestedCount > 0)>
                                                                <td>${requirements.requirementsOfType(requirementType).requirementsWithoutTestsCount}</td>
                                                            </#if>
                                                        </tr>
                                                        </#foreach>

                                                        <#assign requirementTestsTotalCount = testOutcomes.totalTests.total >
                                                        <#assign requirementTestsSuccessCount = testOutcomes.totalTests.withResult("success") >
                                                        <#assign requirementTestsPendingCount = testOutcomes.totalTests.withResult("pending") >
                                                        <#assign requirementTestsIgnoredCount = testOutcomes.totalTests.withResult("ignored") >
                                                        <#assign requirementTestsSkippedCount = testOutcomes.totalTests.withResult("skipped") >
                                                        <#assign requirementTestsFailureOrErrorCount = testOutcomes.totalTests.withFailureOrError() >
                                                    <tr>
                                                        <td class="summary-leading-column"><a
                                                                href="#test-results-table">Acceptance Criteria
                                                            (tests)</a></td>
                                                        <td>${requirementTestsTotalCount}</td>
                                                        <td>${requirementTestsSuccessCount}</td>
                                                        <td>${requirementTestsFailureOrErrorCount}</td>
                                                        <td>${requirementTestsPendingCount}</td>
                                                        <td>${requirementTestsIgnoredCount + requirementTestsSkippedCount}</td>
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
                                    <div id="requirements-tabs">
                                        <#if (requirements.requirementOutcomes?has_content || (requirements.parentRequirement.isPresent() && requirements.parentRequirement.get().hasExamples()))>
                                            <ul>
                                                <#if (requirements.requirementOutcomes?has_content)>
                                                    <li><a href="#tabs-1">
                                                        ${requirementsSectionTitle} (${requirements.requirementCount})
                                                    </a></li>
                                                </#if>
                                                <#if (requirements.parentRequirement.isPresent() && requirements.parentRequirement.get().hasExamples())>
                                                    <li><a href="#tabs-2">Examples
                                                        (${requirements.parentRequirement.get().exampleCount}
                                                        )</a></li>
                                                </#if>
                                            </ul>
                                        </#if>
                                        <#if (requirements.requirementOutcomes?has_content)>
                                            <div id="tabs-1" class="capabilities-table">
                                                <div id="tabs-1" class="capabilities-table">
                                                    <@requirements_results requirements=requirements title=requirementTypeTitle requirementType=requirementsSectionTitle id="req-results-table"/>
                                                </div>
                                            </div>
                                        </#if>
                                        <#if testOutcomes.tests?has_content >
                                            <@test_results testOutcomes=testOutcomes title="Acceptance Criteria" id="test-results-table"/>
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
                                                                    ${reportFormatter.addLinks(example.cardNumber.get())}

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
        </div>
    </div>
</div>
</div>
<div id="bottomfooter">
    <span class="version">Serenity BDD version ${serenityVersionNumber}</span>
</div>
</body>
</html>
