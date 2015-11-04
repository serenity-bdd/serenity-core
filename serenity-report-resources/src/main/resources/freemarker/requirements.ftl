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
    <link rel="shortcut icon" href="favicon.ico">

<#include "libraries/common.ftl">
<#include "libraries/jquery-ui.ftl">
<#include "libraries/datatables.ftl">
<#assign pie = true>
<#include "libraries/jqplot.ftl">

<#include "components/test-outcomes.ftl">
<#include "components/requirements-list.ftl">

<#assign successfulManualTests = (requirements.count("manual").withResult("SUCCESS") > 0)>
<#assign pendingManualTests = (requirements.count("manual").withResult("PENDING") > 0)>
<#assign ignoredManualTests = (requirements.count("manual").withResult("IGNORED") > 0)>
<#assign failingManualTests = (requirements.count("manual").withResult("FAILURE") > 0)>

    <script class="code" type="text/javascript">$(document).ready(function () {
        var plot1 = $.jqplot('coverage_pie_chart', [
            [
                ['Passing', ${requirements.proportionOf("automated").withResult("SUCCESS")}],
            <#if (successfulManualTests == true)>
                ['Passing (manual)', ${requirements.proportionOf("manual").withResult("SUCCESS")}],
            </#if>
                ['Pending', ${requirements.proportionOf("automated").withResult("PENDING")}],
            <#if (pendingManualTests)>
                ['Pending (manual)', ${requirements.proportionOf("manual").withResult("PENDING")}],
            </#if>
                ['Ignored', ${requirements.proportionOf("automated").withResult("IGNORED")}],
            <#if (ignoredManualTests)>
                ['Ignored (manual)', ${requirements.proportionOf("manual").withResult("IGNORED")}],
            </#if>
                ['Failing', ${requirements.proportionOf("automated").withResult("FAILURE")}],
            <#if (failingManualTests)>
                ['Failing (manual)', ${requirements.proportionOf("manual").withResult("FAILURE")}],
            </#if>
                ['Errors',  ${requirements.proportion.withResult("ERROR")}],
                ['Compromised Tests',  ${requirements.proportion.withResult("COMPROMISED")}],
            ]
        ], {
            gridPadding: {top: 0, bottom: 38, left: 0, right: 0},
            seriesColors: ['#30cb23',
                <#if (successfulManualTests)>'#009818',</#if>
                '#a2f2f2',
                <#if (pendingManualTests)>'#8bb1df',</#if>
                '#eeeadd',
                <#if (ignoredManualTests)>'#d3d3d3',</#if>
                '#f8001f',
                <#if (failingManualTests)>'#a20019',</#if>
                '#fc6e1f',
                'fuchsia'],
            seriesDefaults: {
                renderer: $.jqplot.PieRenderer,
                trendline: {show: false},
                rendererOptions: {padding: 8, showDataLabels: true}
            },
            legend: {
                show: true,
                placement: 'outside',
                rendererOptions: {
                    numberRows: 3
                },
                location: 's',
                marginTop: '15px'
            },
            series: [
                {label: '${requirements.formattedPercentage.withResult("SUCCESS")} requirements tested successfully'},
                {label: '${requirements.formattedPercentage.withIndeterminateResult()} requirements untested'},
                {label: '${requirements.formattedPercentage.withResult("FAILURE")}} requirements failing'},
                {label: '${requirements.formattedPercentage.withResult("ERROR")}} requirements with errors'},
                {label: '${requirements.formattedPercentage.withResult("COMPROMISED")}} requirements with compromised tests'}
            ]
        });
        // Results table
        $('#test-results-table').DataTable({
            "order": [
                [ 1, "asc" ]
            ],
            "pageLength": 100,
            "lengthMenu": [ [50, 100, 200, -1] , [50, 100, 200, "All"] ]
        });

        // Results table
        $('#req-results-table').DataTable({
            "order": [
                [ 1, "asc" ]
            ],
            "pageLength": 100,
            "lengthMenu": [ [50, 100, 200, -1] , [50, 100, 200, "All"] ]
        });

        $('#examples-table').DataTable({
            "order": [
                [2, "asc"]
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
        <div id="logo"><a href="index.html"><img src="images/serenity-bdd-logo.png" border="0"/></a></div>
        <div id="projectname-banner" style="float:right">
            <span class="projectname">${reportOptions.projectName}</span>
        </div>
    </div>
</div>

<#if (requirements.parentRequirement.isPresent())>
    <#assign parentRequirement = requirements.parentRequirement.get() >
    <#assign parentTitle = inflection.of(parentRequirement.name).asATitle() >
    <#assign parentType = inflection.of(parentRequirement.type).asATitle() >
    <#if (parentRequirement.cardNumber?has_content) >
        <#assign issueNumber = "[" + formatter.addLinks(parentRequirement.cardNumber) + "]" >
    <#else>
        <#assign issueNumber = "">
    </#if>
</#if>

<div class="middlecontent">
    <div id="contenttop">
        <div class="middlebg">
            <span class="breadcrumbs">
            <a href='index.html'>Home</a>
            <#if requirements.parentRequirement.isPresent()>

                <#assign parent = requirements.parentRequirement.get()>
                <#assign parentTitle = inflection.of(parent.asTag().shortName).asATitle() >

                <#if (requirements.parentRequirement.get().parent?has_content)>
                    <#assign rootReport = reportName.forRequirement(requirements.parentRequirement.get().parent) >
                    <#assign rootTitle = inflection.of(requirements.parentRequirement.get().parent!).asATitle() >
                    > <a href="${rootReport}">${rootTitle}</a>
                </#if>
                > ${parentTitle}
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
                    <h2><i class="fa fa-book"></i> ${parentType}: ${issueNumber} ${parentTitle}</h2>

                    <#if parentRequirement.narrative.renderedText?has_content>
                        <div class="requirementNarrativeTitle">
                        ${formatter.renderDescription(parentRequirement.narrative.renderedText)}
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

            <#if (requirements.totalTestCount > 0 || requirements.flattenedRequirementCount > 0)>
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
                                        <th>Untested&nbsp;<i class="icon-question"></i></th>
                                    </tr>
                                </head>
                                <body>
                                    <#foreach requirementType in requirements.types>
                                    <tr>
                                        <#assign requirementTitle = inflection.of(requirementType).inPluralForm().asATitle() />
                                        <td class="summary-leading-column">${requirementTitle}</td>
                                        <td>${requirements.requirementsOfType(requirementType).requirementCount}</td>
                                        <td>${requirements.requirementsOfType(requirementType).completedRequirementsCount}</td>
                                        <td>${requirements.requirementsOfType(requirementType).failingRequirementsCount}</td>
                                        <td>${requirements.requirementsOfType(requirementType).pendingRequirementsCount}</td>
                                        <td>${requirements.requirementsOfType(requirementType).ignoredRequirementsCount}</td>
                                        <td>${requirements.requirementsOfType(requirementType).requirementsWithoutTestsCount}</td>
                                    </tr>
                                    </#foreach>
                                <tr>
                                    <td class="summary-leading-column">Acceptance Criteria (tests)</td>
                                    <td>${requirements.testOutcomes.testCount}</td>
                                    <td>${requirements.testOutcomes.havingResult("success").testCount}</td>
                                    <td>${requirements.testOutcomes.havingResult("failure").testCount
                                    + requirements.testOutcomes.havingResult("error").testCount
                                    + requirements.testOutcomes.havingResult("compromised").testCount}</td>
                                    <td>${requirements.testOutcomes.havingResult("pending").testCount}</td>
                                    <td>${requirements.testOutcomes.havingResult("ignored").testCount + requirements.testOutcomes.havingResult("skipped").testCount}</td>
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
                <div id="tabs">
                    <#if (requirements.requirementOutcomes?has_content || (requirements.parentRequirement.isPresent() && requirements.parentRequirement.get().hasExamples()))>
                        <ul>
                            <#if (requirements.requirementOutcomes?has_content)>
                                <li><a href="#tabs-1">
                                ${requirementsSectionTitle} (${requirements.requirementCount})
                                </a></li>
                            </#if>
                            <#if (requirements.parentRequirement.isPresent() && requirements.parentRequirement.get().hasExamples())>
                                <li><a href="#tabs-2">Examples (${requirements.parentRequirement.get().exampleCount}
                                    )</a></li>
                            </#if>
                        </ul>
                    </#if>
                    <#if (requirements.requirementOutcomes?has_content)>
                        <div id="tabs-1" class="capabilities-table">
                            <div id="tabs-1" class="capabilities-table">
                                <@requirements_results requirements=requirements title=requirementTypeTitle id="req-results-table"/>
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
                                                    ${formatter.addLinks(example.cardNumber.get())}
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
<div id="bottomfooter">
    <span class="version">Serenity BDD version ${serenityVersionNumber}</span>
</div>
</body>
</html>
