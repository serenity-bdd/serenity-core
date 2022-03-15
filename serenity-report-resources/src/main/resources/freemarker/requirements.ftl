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
    <#include "components/result-chart.ftl">
    <#include "components/requirements-result-chart.ftl">
    <#include "components/result-summary.ftl">
    <#include "components/duration-chart.ftl">
    <#include "components/functional-coverage-chart.ftl">
    <#include "components/tag_cloud.ftl">

    <#assign testResultData = resultCounts.byTypeFor("success","pending","ignored","skipped","aborted","failure","error","compromised") >
    <#assign testLabels = resultCounts.percentageLabelsByTypeFor("success","pending","ignored","skipped","aborted","failure","error","compromised") >
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
                levels: 10,
                showTags: true,
                expandIcon: "bi bi-journal-plus",
                collapseIcon: "bi bi-journal-minus",
                emptyIcon: "bi bi-journal"
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

<#assign successReport = reportName.withPrefix(currentTag).forTestResult("success") >
<#assign brokenReport = reportName.withPrefix(currentTag).forTestResult("broken") >
<#assign failureReport = reportName.withPrefix(currentTag).forTestResult("failure") >
<#assign errorReport = reportName.withPrefix(currentTag).forTestResult("error") >
<#assign compromisedReport = reportName.withPrefix(currentTag).forTestResult("compromised") >
<#assign pendingReport = reportName.withPrefix(currentTag).forTestResult("pending") >
<#assign skippedReport = reportName.withPrefix(currentTag).forTestResult("skipped") >
<#assign abortedReport = reportName.withPrefix(currentTag).forTestResult("aborted") >
<#assign ignoredReport = reportName.withPrefix(currentTag).forTestResult("ignored") >

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

                                <#if isLeafRequirement && requirementTags?has_content >
                                   <div class="col-sm-8">
                                <#else>
                                    <div class="col-sm-12">
                                 </#if>
                                        <span>
                                            <h2><i class="bi bi-book"></i> ${parentType}: ${formatter.htmlCompatibleStoryTitle(parentTitle)}</h2>
                                        </span>
                                    </div>
                                    <#if isLeafRequirement && requirementTags?has_content >
                                        <div class="col-sm-4">
                                        <span class="feature-tags">
                                            <p class="tag">
                                            <#list requirementTags as tag>
                                                <#assign tagReport = absoluteReportName.forRequirementOrTag(tag) />
                                                <#assign tagTitle = inflection.of(tag.shortName).asATitle() >
                                                    <#assign tagStyle = styling.tagStyleFor(tag) >
                                                    <span class="badge feature-tag tag-badge" style="${tagStyle}">
                                                        <i class="bi bi-tag-fill"></i>&nbsp;<a class="tagLink" style="${tagStyle}" href="${tagReport}">${formatter.tagLabel(tag)}</a>
                                                    </span>
                                            </#list>
                                            </p>
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
                                        <i class="bi bi-plus-square"></i>
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
                                <a data-toggle="tab" href="#specs"><i class="bi bi-journal-text"></i> Specifications</a>
                            </li>
                            <li>
                                <a data-toggle="tab" href="#results"><i class="bi bi-speedometer"></i> Test Results</a>
                            </li>
                        </ul>

                        <!-- Specifications and Results tabs -->
                        <div class="card border">
                            <div class="tab-content" id="pills-tabContent">
                                <div id="specs" class="tab-pane fade in active">
                                    <div class="container-fluid">
                                        <div class="row">
                                            <div class="col-sm-4">
                                                <h3>Feature Coverage By Scenario</h3>

                                                <!-- REQUIREMENTS PIE CHART -->
                                                <#if testOutcomes.total != 0>
                                                    <div class="chart-container" style="position: relative; width:30vw">
                                                        <canvas id="requirementChart" width="300" height="300"></canvas>
                                                    </div>
                                                </#if>

                                            </div>
                                            <div class="col-sm-8">
                                                <#if !isLeafRequirement>
                                                <h3>Requirements Overview</h3>

                                                <div id="tree"></div>

                                                <#else>
                                                <!--- TOC --->
                                                <div id="toc">
                                                    <h3>Overview</h3>
                                                    <table class="table" id="toc-table">
                                                        <#list scenarioGroups as scenarioGroup>
                                                            <#assign scenarioInRule = false />
                                                            <#if scenarioGroup.ruleName?has_content>
                                                                <#assign scenarioInRule = true />
                                                                <tr>
                                                                    <td class="rule-toc-entry" colspan="2">
                                                                        <a href="#${scenarioGroup.id}"
                                                                           title="View scenario details for this rule">
                                                                            Rule: ${scenarioGroup.ruleName}
                                                                    </td>
                                                                </tr>
                                                            </#if>
                                                            <#if scenarioGroup.hasScenarios() >
                                                                <#list scenarioGroup.mainScenarios as scenario>
                                                                    <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />
                                                                    <tr>
                                                                        <td style="width:95%;" class="toc-title">
                                                                            <#if scenarioInRule>
                                                                                <#assign scenarioClass = "scenario-toc-entry"/>
                                                                            <#else>
                                                                                <#assign scenarioClass = "orphan-scenario-toc-entry"/>
                                                                            </#if>
                                                                            <span class="${scenarioClass}">
                                                                                    <a href="#${scenario.id}"
                                                                                       title="View scenario details">
                                                                                        <i class="bi bi-chat-dots"></i> ${scenario.type}: ${formatter.renderTitle(scenario.simplifiedName)}
                                                                                    </a>
                                                                                    <#if scenario.hasExamples() >
                                                                                        (${scenario.numberOfExamples})
                                                                                    </#if>
                                                                                </span>
                                                                        </td>
                                                                        <td>

                                                                        </td>
                                                                        <td style="width:5%;">
                                                                            <table>
                                                                                <tr>
                                                                                    <td class="icons-bar">
                                                                                        <#if outcome_icon?has_content>
                                                                                        <a style="margin-left:0.5em;padding-right: 1.5em;"
                                                                                           href="${scenario.scenarioReport}"
                                                                                           title="View test results">
                                                                                            </#if>
                                                                                            ${outcome_icon}
                                                                                            <#if outcome_icon?has_content></a></#if>

                                                                                        <#if (scenario.isManual())>
                                                                                            <i class="bi bi-person manual"
                                                                                               title="Manual test"></i></#if>
                                                                                        <#if scenario.scenarioReport?has_content>
                                                                                            <a style="margin-left:0.5em;padding-right: 1.5em;"
                                                                                               href="${scenario.scenarioReport}"
                                                                                               title="View test results">
                                                                                                <i class="bi bi-eyeglasses"></i>
                                                                                            </a>
                                                                                        </#if>
                                                                                    </td>
                                                                                </tr>
                                                                            </table>
                                                                        </td>
                                                                    </tr>
                                                                </#list>
                                                            </#if>
                                                        </#list>
                                                    </table>
                                                </div>
                                                <!--- END OF TOC --->
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-sm-12">
                                                    <h3>Scenarios</h3>

                                                    <#list scenarioGroups as scenarioGroup>
                                                        <a name="${scenarioGroup.id}"></a>
                                                        <#if scenarioGroup.ruleName?has_content>
                                                            <div class="rule-title">
                                                                Rule: ${scenarioGroup.ruleName}</div>
                                                        </#if>
                                                        <p class="rule-tags inline-tag">
                                                            <#list scenarioGroup.filteredTags as tag>
                                                            <#assign tagReport = absoluteReportName.forRequirementOrTag(tag) />
                                                            <#assign tagTitle = tagInflector.ofTag(tag.type, tag.shortName).toFinalView() >
                                                                <#assign tagStyle = styling.tagStyleFor(tag) >
                                                                <span class="badge tag-badge" style="${tagStyle}">
                                                                    <i class="bi bi-tag-fill"></i>&nbsp;
                                                                    <a class="tagLink" style="${tagStyle}"
                                                                       href="${tagReport}">${formatter.tagLabel(tag)}</a>
                                                                </span>
                                                            </#list>
                                                        </p>
                                                        <#if scenarioGroup.ruleDescription?has_content>
                                                            <div class="scenario-comments">
                                                                <i class="bi bi-info-circle"></i>${formatter.renderText(scenarioGroup.ruleDescription)}
                                                            </div>
                                                        </#if>
                                                        <#if scenarioGroup.hasBackground()>
                                                            <div class="scenario-docs card">
                                                                <div class="scenario-docs card-header" style="min-height:1.5em;">
                                                                    <span class="scenario-heading">
                                                                    Background: <#if scenarioGroup.backgroundTitle?has_content>${formatter.renderText(scenarioGroup.backgroundTitle)}</#if></span>
                                                                    </span>
                                                                    <#if scenarioGroup.backgroundDescription?has_content>
                                                                        <div class="scenario-comments">
                                                                            <i class="bi bi-info-circle"></i>${formatter.renderText(scenarioGroup.backgroundDescription)}
                                                                        </div>
                                                                    </#if>
                                                                </div>
                                                                <div class="scenario-docs card-body">
                                                                    <#list scenarioGroup.backgroundSteps as step>
                                                                        <p>${formatter.renderHtmlEscapedDescription(step)}</p>
                                                                    </#list>
                                                                </div>
                                                            </div>
                                                        </#if>

                                                        <#list scenarioGroup.mainScenarios as scenario>
                                                            <#assign outcome_icon = formatter.resultIcon().forResult(scenario.result) />

                                                            <div class="scenario-docs card" id="${scenario.id}">
                                                                <div class="scenario-docs card-header ${scenario.resultStyle}" style="min-height:1.5em;">
                                                                    <div>
                                                                        <span class="scenario-heading">
                                                                            <a href="${scenario.scenarioReport}"
                                                                               title="View test results">${scenario.type}: ${formatter.renderTitle(scenario.title)}</a>
                                                                        </span>

                                                                        <#if scenario.scenarioTags??>
                                                                        <span class="example-tags inline-tag">
                                                                            <#list scenario.scenarioTags as tag>
                                                                                <#assign tagReport = absoluteReportName.forRequirementOrTag(tag) />
                                                                                <#assign tagTitle = tagInflector.ofTag(tag.type, tag.shortName).toFinalView() >
                                                                                <#assign tagStyle = styling.tagStyleFor(tag) >
                                                                                <span class="badge tag-badge" style="${tagStyle}">
                                                                                    <i class="bi bi-tag-fill"></i>&nbsp;
                                                                                    <a class="tagLink" style="${tagStyle}" href="${tagReport}">${formatter.tagLabel(tag)}</a>
                                                                                </span>
                                                                            </#list>
                                                                        </span>
                                                                        </#if>

                                                                        <span class="scenario-result-icon">
                                                                    <#if (scenario.isManual())> <i
                                                                        class="bi bi-person manual"
                                                                        title="Manual test"></i></#if>

                                                                            <#if outcome_icon?has_content>
                                                                                ${outcome_icon}
                                                                            <#else>
                                                                                <i class="bi bi-pause-circle"
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
                                                                            <i class="bi bi-info-circle"></i> ${formatter.renderHtmlEscapedDescription(scenario.description)}
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
                                                    </#list>
                                                </#if>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div id="results" class="tab-pane fade">
                                    <div class="container-fluid">
                                        <div class="row">
                                            <div class="col-sm-4">
                                                <h3>Feature Coverage By Scenario</h3>
                                                <!-- TEST RESULT PIE CHART -->
                                                <#if testOutcomes.total != 0>
                                                    <div class="chart-container" style="position: relative; width:30vw">
                                                        <canvas id="resultChart" width="300" height="300"></canvas>
                                                    </div>
                                                </#if>
                                            </div>
                                            <div class="col-sm-4">
                                                <h4><i class="bi bi-check-square"></i> Test Outcomes</h4>
                                                <!-- Severity bar chart -->
                                                <div class="chart-container" style="position: relative; width:30vw">
                                                    <canvas id="severityChart" width="300" height="300"></canvas>
                                                </div>
                                            </div>

                                            <div class="col-sm-4">
                                                <h4><i class="bi bi-graph-up"></i> Test Performance</h4>

                                                <!-- Duration bar chart -->
                                                <div class="chart-container" style="position: relative; width:30vw">
                                                    <canvas id="durationChart" width="300" height="300"></canvas>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="container-fluid">
                                        <div class="row">
                                            <div class="col-sm-12">
                                                <h3><i class="bi bi-gear"></i> Automated Tests</h3>

                                                <#if (automatedTestCases?has_content)>
                                                    <table class="scenario-result-table table"
                                                           id="scenario-results">
                                                        <thead>
                                                        <tr>
                                                            <#if !isLeafRequirement>
                                                                <th>${leafRequirementType}</th>
                                                            </#if>
                                                            <th class="test-name-column">Scenario</th>
                                                            <th>Steps</th>
                                                            <th>Started</th>
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
                                                                        <i class="bi bi-pause-circle"
                                                                           title="No test has been implemented yet"></i>
                                                                    </#if>
                                                                    <#if (scenario.externalLink)?? && (scenario.externalLink.url)??>&nbsp;
                                                                        <a href="${scenario.externalLink.url}" class="tag" title="${scenario.externalLink.type}">
                                                                            <i class="fs-2 bi bi-camera-reels"></i>
                                                                        </a>
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
                                                <h3><i class="bi bi-hand-index-thumb"></i> Manual Tests</h3>

                                                <#if (manualTestCases?has_content)>
                                                    <table class="scenario-result-table table"
                                                           id="manual-scenario-results">
                                                        <thead>
                                                        <tr>
                                                            <#if !isLeafRequirement>
                                                                <th>${leafRequirementType}</th>
                                                            </#if>
                                                            <th class="test-name-column" style="width:60em;">
                                                                Scenario
                                                            </th>
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
                                                                            style="display:none">${scenario.result}</span>
                                                                </td>
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
                                                    <h3><i class="bi bi-download"></i> Evidence</h3>
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
                                        <@tag_cloud />
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
    </div>
</div>
<!-- Chart data -->
<@result_chart id='resultChart' />
<@requirements_result_chart id='requirementChart' />
<@result_summary id='severityChart' />
<@duration_chart id='durationChart' />
</body>
</html>
