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

    <#include "components/test-outcomes.ftl">
    <#include "components/requirements-list.ftl">

    <#assign testResultData = resultCounts.byTypeFor("success","pending","ignored","skipped","failure","error","compromised") >
    <#assign testLabels = resultCounts.percentageLabelsByTypeFor("success","pending","ignored","skipped","failure","error","compromised") >
    <#assign graphType="automated-and-manual-results" >

    <script>

        $(document).ready(function () {

            var scenarioTable = $('.scenario-result-table').DataTable({

                "order": [],
                "language": {
                    searchPlaceholder: "Filter",
                    search: ""
                }
            });

            $('.example-table table').DataTable({
                searching: false,
                ordering: false,
                paging: false,
                info: false
            });

            $('.example-table-in-scenario table').DataTable({
                searching: false,
                ordering: false,
                paging: false,
                info: false
            });

            $('#evidence-table').DataTable(
                <#if evidence?size <= 10 >
                    {
                        searching: false,
                        ordering: false,
                        paging: false,
                        info: false
                    }
                </#if>
            );


            $(".scenario-docs .card-body table").wrap("<div class='table-responsive'></div>");

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

    <script>
        $(document).ready(function () {
            $(".scenario-docs > div > p").html(function () {
                var text = $(this).text().trim().split(" ");
                var first = text.shift();
                return (text.length > 0 ? "<strong>" + first + "</strong> " : first) + text.join(" ");
            });
        });
    </script>
    <#if (prettyTables) >
        <style>
            .example-table-in-scenario th:last-child {
                border-right: solid white 1px;
                border-top: solid white 1px;
                border-bottom: solid white 1px;
            }

            .example-table-in-scenario td:last-child {
                border-right: solid white 1px;
                border-top: solid white 1px;
                border-bottom: solid white 1px;
            }

            .scenario-docs table.dataTable {
                border: 0px !important;
            }

        </style>
    </#if>
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
                <#assign breadcrumbTitle = inflection.of(breadcrumb.displayName).asATitle() >
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
        <div class="table">
            <#if (requirements.parentRequirement.isPresent())>
                <div class=row">
                    <div class="col-sm-12">
                        <div>
                            <div clas="table">
                                <div class="row">
                                    <#if isLeafRequirement && filteredTags?has_content >
                                    <div class="col-sm-8">
                                    <#else>
                                    <div class="col-sm-12">
                                    </#if>
                                    <span>
                                            <h2><i class="fa fa-book"></i> ${parentType}: ${issueNumber} ${formatter.htmlCompatibleStoryTitle(parentTitle)}</h2>
                                        </span>
                                </div>
                                    <#if isLeafRequirement && filteredTags?has_content >
                                    <div class="col-sm-4">
                                        <span class="feature-tags">
                                            <#list filteredTags as tag>
                                                <#assign tagReport = absoluteReportName.forRequirementOrTag(tag) />
                                                <#assign tagTitle = inflection.of(tag.shortName).asATitle() >
                                                <p class="tag">
                                                    <#assign tagStyle = styling.tagStyleFor(tag) >
                                                    <span class="badge tag-badge" style="${tagStyle}">
                                                        <i class="fa fa-tag"></i>&nbsp;<a class="tagLink"
                                                                                          style="${tagStyle}"
                                                                                          href="${tagReport}">${formatter.htmlCompatible(tagTitle)}
                                                        (${tag.type})</a>
                                                    </span>
                                                </p>
                                            </#list>
                                        </span>
                                    </div>
                                    </#if>
                                </div>
                                </div>
                            </div>
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
                    </div>
            </#if>


            <#if (requirements.overview?has_content)>
                <div class="row">
                    <div class="col-sm-12">
                        <div class="requirements-overview panel panel-default">
                            <div class="panel-body">
                                ${formatter.renderDescriptionWithFormattedTables(requirements.overview, requirements)}
                            </div>
                        </div>
                    </div>
                </div>
            </#if>
            <div class="row">
                <div class="col-sm-12">

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
                                <div class="container-fluid">
                                    <div class="row">
                                        <div class="col-sm-12">

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
                                                            <td style="width:95%;" class="toc-title">
                                                                <a href="#${scenario.id}" title="View scenario details" >${formatter.humanReadableFormOf(formatter.renderHeaders(scenario.simplifiedName))}</a>
                                                                 <#if scenario.hasExamples() >
                                                                     (${scenario.numberOfExamples})</#if>

                                                            </td>
                                                            <td style="width:5%;">
                                                                <table>
                                                                    <tr>
                                                                        <td>
                                                                            <#if outcome_icon?has_content>
                                                                            <a style="margin-left:0.5em;padding-right: 1.5em;"
                                                                               href="${scenario.scenarioReport}"
                                                                               title="View test results">
                                                                            </#if>
                                                                            ${outcome_icon}
                                                                            <#if outcome_icon?has_content></a></#if>

                                                                        <#if (scenario.isManual())> <i class="fa fa-user manual"
                                                                                           title="Manual test"></i></#if>
                                                                        </td>
                                                                        <#if outcome_icon?has_content>
                                                                        <td>
                                                                            <a style="margin-left:0.5em;padding-right: 1.5em;"
                                                                               href="${scenario.scenarioReport}"
                                                                               title="View test results">
                                                                                <i class="fas fa-search-plus"></i>
                                                                            </a>
                                                                        </td>
                                                                        </#if>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                        </tr>
                                                    </#list>
                                                </table>

                                            </div>

                                            <h3>Scenario details</h3>
                                            <#list scenarios as scenario>
                                                <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />

                                                <div class="scenario-docs card" id="${scenario.id}">
                                                    <div class="scenario-docs card-header ${scenario.resultStyle}" style="min-height:1.5em;">
                                                        <div>
                                                            <span class="scenario-heading">
                                                                <a href="${scenario.scenarioReport}" title="View test results">${formatter.renderTitle(scenario.title)}</a>
                                                            </span>
                                                            <span class="scenario-result-icon">
                                                                <#if (scenario.isManual())> <i class="fa fa-user manual" title="Manual test"></i></#if>

                                                                <#if outcome_icon?has_content>
                                                                    ${outcome_icon}
                                                                <#else>
                                                                    <i class="fas fa-pause"
                                                                       title="No test has been implemented yet"></i>
                                                                </#if>
                                                            </span>
                                                        </div>
                                                        <#--<#if outcome_icon?has_content>-->
                                                        <#--<div class="scenario-report-badges">-->
                                                            <#--<#list scenario.scenarioReportBadges as scenarioReporBadge>-->
                                                                <#--<span class="scenario-report-badge" style="float:right;">${scenarioReporBadge}</span>-->
                                                            <#--</#list>-->
                                                        <#--</div>-->
                                                        <#--</#if>-->
                                                    </div>
                                                    <div class="scenario-docs card-body">
                                                        <#if scenario.description?has_content>
                                                            <div class="scenario-text">
                                                                <i class="fa fa-info-circle"></i> ${formatter.renderHtmlEscapedDescription(scenario.description)}
                                                            </div>
                                                        </#if>
                                                        <#list scenario.steps as step>
                                                            <p>${formatter.renderHtmlEscapedDescription(step)}</p>
                                                        </#list>
                                                        <div class="examples">
                                                        <#list scenario.examples as example>
                                                            <p>${formatter.renderTableDescription(example, requirements)}</p>
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
                                <div class="container-fluid">
                                    <div class="row">
                                    <#if resultCounts.getTotalOverallTestCount() != 0>
                                        <div class="col-sm-4">

                                            <div style="width:300px;" class="chart-container ${graphType}">
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
                                        <div class="col-sm-8">
                                    <#else>
                                        <div class="col-sm-12">
                                    </#if>
                                                <#assign successReport = reportName.withPrefix(currentTag).forTestResult("success") >
                                                <#assign brokenReport = reportName.withPrefix(currentTag).forTestResult("broken") >
                                                <#assign failureReport = reportName.withPrefix(currentTag).forTestResult("failure") >
                                                <#assign errorReport = reportName.withPrefix(currentTag).forTestResult("error") >
                                                <#assign compromisedReport = reportName.withPrefix(currentTag).forTestResult("compromised") >
                                                <#assign pendingReport = reportName.withPrefix(currentTag).forTestResult("pending") >
                                                <#assign skippedReport = reportName.withPrefix(currentTag).forTestResult("skipped") >
                                                <#assign ignoredReport = reportName.withPrefix(currentTag).forTestResult("ignored") >

                                        <table class="table">
                                            <thead>
                                            <tr>
                                                <th scope="col">Scenario Results (including rows of test data)</th>
                                                <th scope="col" colspan="2" class="automated-stats">
                                                    Automated
                                                </th>
                                                    <#if resultCounts.hasManualTests() >
                                                        <th scope="col" colspan="2" class="manual-stats"> Manual</th>
                                                        <th scope="col" colspan="2" class="total-stats"> Total</th>
                                                    </#if>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("success") != 0)>
                                                                    <td class="aggregate-result-count">
                                                                        <a href="${successReport}"><i
                                                                                class='fa fa-check-circle-o success-icon'></i>&nbsp;Passing</a>
                                                                    </td>
                                                                <#else>
                                                                    <td class="aggregate-result-count"><i
                                                                            class='fa fa-check-circle-o success-icon'></i>&nbsp;Passing
                                                                    </td>
                                                                </#if>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("success")}</td>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("success")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                <td class="manual-stats">${resultCounts.getManualTestCount("success")}</td>
                                                                <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("success")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestCount("success")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("success")}</td>
                                                                </#if>
                                            </tr>
                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("pending") != 0)>
                                                                    <td class="aggregate-result-count">
                                                                        <a href="${pendingReport}"><i
                                                                                class='fa fa-stop-circle-o pending-icon'></i>&nbsp;Pending</a>
                                                                    </td>
                                                                <#else>
                                                                    <td class="aggregate-result-count"><i
                                                                            class='fa fa-stop-circle-o pending-icon'></i>&nbsp;Pending
                                                                    </td>
                                                                </#if>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("pending")}</td>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("pending")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                <td class="manual-stats">${resultCounts.getManualTestCount("pending")}</td>
                                                                <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("pending")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestCount("pending")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("pending")}</td>
                                                                </#if>
                                            </tr>
                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("ignored") != 0)>
                                                                    <td class="aggregate-result-count">
                                                                        <a href="${ignoredReport}"><i
                                                                                class='fa fa-ban ignored-icon'></i>&nbsp;Ignored</a>
                                                                    </td>
                                                                <#else>
                                                                <td class="aggregate-result-count"><i
                                                                        class='fa fa-ban ignored-icon'></i>&nbsp;Ignored
                                                                </td>
                                                                </#if>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("ignored")}</td>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("ignored")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                <td class="manual-stats">${resultCounts.getManualTestCount("ignored")}</td>
                                                                <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("ignored")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestCount("ignored")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("ignored")}</td>
                                                                </#if>
                                            </tr>
                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("skipped") != 0)>
                                                                    <td class="aggregate-result-count">
                                                                        <a href="${skippedReport}"><i
                                                                                class='fa fa-fast-forward skip-icon'></i>&nbsp;Skipped</a>
                                                                    </td>
                                                                <#else>
                                                                <td class="aggregate-result-count"><i
                                                                        class='fa fa-fast-forward skip-icon'></i>&nbsp;Skipped
                                                                </td>
                                                                </#if>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("skipped")}</td>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("skipped")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                <td class="manual-stats">${resultCounts.getManualTestCount("skipped")}</td>
                                                                <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("skipped")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestCount("skipped")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("skipped")}</td>
                                                                </#if>
                                            </tr>
                                            <tr>
                                                                 <#if resultCounts.hasManualTests() >
                                                                        <#if (resultCounts.getOverallTestsCount("failure","error","compromised") != 0)>
                                                                           <td colspan="7"><a href="${brokenReport}"><i
                                                                                   class='fa fa-times failure-icon'></i>&nbsp;<em>Unsuccessful</em></a>
                                                                           </td>
                                                                        <#else>
                                                                            <td colspan="7"><i
                                                                                class='fa fa-times failure-icon'></i>&nbsp;<em>Unsuccessful</em></a>
                                                                            </td>
                                                                        </#if>
                                                                <#else>
                                                                        <#if (resultCounts.getOverallTestsCount("failure","error","compromised") != 0)>
                                                                             <td colspan="3"><a href="${brokenReport}"><i
                                                                                    class='fa fa-times failure-icon'></i>&nbsp;<em>Unsuccessful</em></a>
                                                                            </td>
                                                                        <#else>
                                                                            <td colspan="3"><i
                                                                                class='fa fa-times failure-icon'></i>&nbsp;<em>Unsuccessful</em></a>
                                                                            </td>
                                                                        </#if>
                                                                </#if>

                                            </tr>

                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("failure") != 0)>
                                                                    <td class="aggregate-result-count indented-error-category">
                                                                        <a href="${failureReport}"><i
                                                                                class='fa fa-times-circle failure-icon'></i>&nbsp;Failed</a>
                                                                    </td>
                                                                <#else>
                                                                <td class="aggregate-result-count indented-error-category">
                                                                    <i
                                                                            class='fa fa-times-circle failure-icon'></i>&nbsp;Failed
                                                                </td>
                                                                </#if>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("failure")}</td>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("failure")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                <td class="manual-stats">${resultCounts.getManualTestCount("failure")}</td>
                                                                <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("failure")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestCount("failure")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("failure")}</td>
                                                                </#if>
                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("error") != 0)>
                                                                    <td class="aggregate-result-count indented-error-category">
                                                                        <a href="${errorReport}"><i
                                                                                class='fa fa-exclamation-triangle error-icon'></i>&nbsp;Broken</a>
                                                                    </td>
                                                                <#else>
                                                                <td class="aggregate-result-count indented-error-category">
                                                                    <i
                                                                            class='fa fa-exclamation-triangle error-icon'></i>&nbsp;Broken
                                                                </td>
                                                                </#if>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("error")}</td>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("error")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                <td class="manual-stats">${resultCounts.getManualTestCount("error")}</td>
                                                                <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("error")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestCount("error")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("error")}</td>
                                                                </#if>
                                            <tr>
                                                                <#if (resultCounts.getOverallTestCount("compromised") != 0)>
                                                                    <td class="aggregate-result-count indented-error-category">
                                                                        <a href="${compromisedReport}"><i
                                                                                class='fa fa-chain-broken compromised-icon'></i>&nbsp;Compromised</a>
                                                                    </td>
                                                                <#else>
                                                                <td class="aggregate-result-count indented-error-category">
                                                                    <i
                                                                            class='fa fa-chain-broken compromised-icon'></i>&nbsp;Compromised
                                                                </td>
                                                                </#if>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestCount("compromised")}</td>
                                                <td class="automated-stats">${resultCounts.getAutomatedTestPercentageLabel("compromised")}</td>
                                                                <#if resultCounts.hasManualTests() >
                                                                <td class="manual-stats">${resultCounts.getManualTestCount("compromised")}</td>
                                                                <td class="manual-stats">${resultCounts.getManualTestPercentageLabel("compromised")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestCount("compromised")}</td>
                                                                <td class="total-stats">${resultCounts.getOverallTestPercentageLabel("compromised")}</td>
                                                                </#if>
                                            </tr>
                                            <tr class="summary-stats">
                                                <td class="aggregate-result-count">Total</td>
                                                <td class="automated-stats">${resultCounts.getTotalAutomatedTestCount()}</td>
                                                <td class="automated-stats"></td>
                                                            <#if resultCounts.hasManualTests() >
                                                            <td class="manual-stats">${resultCounts.getTotalManualTestCount()}</td>
                                                            <td class="manual-stats"></td>
                                                            <td class="total-stats">${resultCounts.getTotalOverallTestCount()}</td>
                                                            <td class="total-stats"></td>
                                                            </#if>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    </div>

                                        <div class="row">
                                            <div class="col-sm-12">
                                                <table class="table">
                                                    <thead>
                                                    <tr>
                                                        <th>Execution Time</th>
                                                        <th>Clock Time</th>
                                                        <th>Fastest Test</th>
                                                        <th>Slowest Test</th>
                                                        <th>Average Execution Time</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    <tr>
                                                        <td>${totalTestDuration}</td>
                                                        <td>${totalClockDuration}</td>
                                                        <td>${minTestDuration}</td>
                                                        <td>${maxTestDuration}</td>
                                                        <td>${averageTestDuration}</td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>

                                    <#if (requirements.requirementOutcomes?has_content)>
                                        <#assign workingRequirementsTitle = inflection.of(requirements.type).inPluralForm().asATitle() >

                                        <div class="row">
                                            <div class="col-sm-12">
                                                <h3><i class="fas fa-clipboard-check"></i> Functional Coverage</h3>

                                                <@requirements_results requirements=requirements title=requirementTypeTitle requirementType=workingRequirementsTitle id="req-results-table"/>

                                            </div>
                                        </div>
                                    </#if>

                                        <div class="row">
                                            <div class="col-sm-12">
                                                <h3><i class="fas fa-cogs"></i> Automated Tests</h3>

                                            <#if (automatedTestCases?has_content)>
                                            <table class="scenario-result-table table" id="scenario-results">
                                                <thead>
                                                <tr>
                                                    <#if !isLeafRequirement>
                                                    <th>${leafRequirementType}</th>
                                                    </#if>
                                                    <th class="test-name-column">Scenario</th>
                                                    <th>Steps</th>
                                                    <th>Start Time</th>
                                                    <th>Duration</th>
                                                    <th>Result</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <#list automatedTestCases as scenario>
                                                <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />
                                                <tr class="scenario-result ${scenario.result}">
                                                    <#if !isLeafRequirement>
                                                    <td>
                                                        <#if scenario.parentName?has_content>
                                                            <a href="${scenario.parentReport}">${scenario.parentName}</a>
                                                        </#if>
                                                    </td>
                                                    </#if>
                                                    <td>
                                                        <#if outcome_icon?has_content>
                                                            <a href="${scenario.scenarioReport}">${formatter.renderTitle(scenario.title)}</a>
                                                        <#else>
                                                            ${formatter.renderTitle(scenario.title)}
                                                        </#if>
                                                        <#if scenario.hasExamples() >
                                                               (${scenario.numberOfExamples})
                                                        </#if>
                                                    </td>
                                                    <td>${scenario.stepCount}</td>
                                                    <td>${scenario.formattedStartTime}</td>
                                                    <td>${scenario.formattedDuration}</td>
                                                    <td>
                                                        <#if outcome_icon?has_content >
                                                            ${outcome_icon} <span
                                                                style="display:none">${scenario.result}</span>
                                                        <#else>
                                                            <i class="fas fa-pause"
                                                               title="No test has been implemented yet"></i>
                                                        </#if>
                                                    </td>
                                                </tr>
                                                </#list>
                                                </tbody>
                                            </table>
                                            <#else>
                                            No automated tests were executed
                                            </#if>

                                            </div>
                                        </div>

                                        <div class="row">
                                            <div class="col-sm-12">
                                                <h3><i class="fas fa-edit"></i> Manual Tests</h3>

                                            <#if (manualTestCases?has_content)>
                                            <table class="scenario-result-table table" id="manual-scenario-results">
                                                <thead>
                                                <tr>
                                                    <#if !isLeafRequirement>
                                                    <th>${leafRequirementType}</th>
                                                    </#if>
                                                    <th class="test-name-column" style="width:60em;">Scenario</th>
                                                    <th>Steps</th>
                                                    <th>Result</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <#list manualTestCases as scenario>
                                                    <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />
                                                <tr class="scenario-result ${scenario.result}">
                                                     <#if !isLeafRequirement>
                                                    <td>
                                                        <#if scenario.parentName?has_content>
                                                            <a href="${scenario.parentReport}">${scenario.parentName}</a>
                                                        </#if>
                                                    </td>
                                                     </#if>
                                                    <td>
                                                        <a href="${scenario.scenarioReport}">${scenario.title}</a>
                                                        <#if scenario.hasExamples() >
                                                           (${scenario.numberOfExamples})
                                                        </#if>
                                                    </td>
                                                    <td>${scenario.stepCount}</td>
                                                    <td>${outcome_icon} <span
                                                            style="display:none">${scenario.result}</span></td>
                                                </tr>
                                                </#list>
                                                </tbody>
                                            </table>
                                            <#else>
                                                No manual tests were recorded
                                            </#if>

                                            </div>
                                        </div>

                                    <#if evidence?has_content>
                                        <div class="row">
                                            <div class="col-sm-12">
                                                <h3><i class="far fa-file"></i> Evidence</h3>
                                                <table id="evidence-table" class="table table-bordered">
                                                    <thead>
                                                    <tr>
                                                        <th>Scenario</th>
                                                        <th>Title</th>
                                                        <th>Details</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                     <#list evidence as evidenceRecord>
                                                     <tr>
                                                         <td>${evidenceRecord.scenario}</td>
                                                         <td>${evidenceRecord.title}</td>
                                                         <td>${evidenceRecord.detailsLink}</td>
                                                     </tr>
                                                     </#list>
                                                    </tbody>
                                                </table>
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
            <div class="container-fluid">
                <div class="row">
                    <div class="col-sm-12">
                        <span class="version">Serenity BDD version ${serenityVersionNumber!"SNAPSHOT-BUILD"}</span>
                    </div>
                </div>
            </div>
</body>
</html>
