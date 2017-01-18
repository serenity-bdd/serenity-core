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


<#list requirements.types as requirementType>
    <#assign requirementsOfType = requirements.requirementsOfType(requirementType) />
    <#assign successfulRequirements= requirementsOfType.completedRequirementsCount >
    <#assign pendingRequirements = requirementsOfType.pendingRequirementsCount>
    <#assign ignoredRequirements = requirementsOfType.ignoredRequirementsCount >
    <#assign failingRequirements = requirementsOfType.failingRequirementsCount >
    <#assign errorRequirements = requirementsOfType.errorRequirementsCount  >
    <#assign compromisedRequirements = requirementsOfType.compromisedRequirementsCount  >
    <#assign untesteddRequirements = requirementsOfType.requirementsWithoutTestsCount >
<#else>
    <#assign totalTests= testOutcomes.totalTests >
    <#assign successfulRequirements= totalTests.withResult("success") >
    <#assign pendingRequirements = totalTests.withResult("pending") >
    <#assign ignoredRequirements = totalTests.withResult("ignored") + testOutcomes.totalTests.withResult("skipped")>
    <#assign failingRequirements = totalTests.withResult("failure") >
    <#assign errorRequirements = totalTests.withResult("error") >
    <#assign compromisedRequirements = totalTests.withResult("compromised") >
    <#assign untesteddRequirements = 0 >
</#list>

    <script class="code" type="text/javascript">$(document).ready(function () {
        var plot1 = $.jqplot('coverage_pie_chart', [
            [
                ['Passing', ${successfulRequirements}],
                ['Pending', ${pendingRequirements}],
                ['Ignored', ${ignoredRequirements}],
                ['Failing', ${failingRequirements}],
                ['Errors',  ${errorRequirements}],
                ['Compromised',  ${compromisedRequirements}],
                ['Untested',  ${untesteddRequirements}],
            ]
        ], {
            gridPadding: {top: 0, bottom: 38, left: 0, right: 0},
            seriesColors: [
                '#30cb23',
                '#a2f2f2',
                '#eeeadd',
                '#f8001f',
                '#fc6e1f',
                'fuchsia',
                'darkgrey'],
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
                {label: '${successfulRequirements} requirements tested successfully'},
                {label: '${pendingRequirements} requirements pending'},
                {label: '${ignoredRequirements}} requirements skipped'},
                {label: '${failingRequirements}} requirements with failures'},
                {label: '${errorRequirements}} requirements with errors'},
                {label: '${compromisedRequirements}} requirements compromised'},
                {label: '${untesteddRequirements}} requirements untested'},
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
        $("#requirements-tabs").tabs();
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
    <#assign parentTitle = inflection.of(parentRequirement.displayName).asATitle() >
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
            <a href='index.html'>Home</a> > <a href="capabilities.html">Requirements</a>
            <#if requirements.parentRequirement.isPresent()>

                <#assign parent = requirements.parentRequirement.get()>
                <#assign parentTitle = inflection.of(parent.displayName).asATitle() >

                <#if (requirements.grandparentRequirement.isPresent())>
                    <#assign ancestor = reportName.forRequirement(requirements.grandparentRequirement.get()) >
                    <#assign rootReport = reportName.forRequirement(requirements.grandparentRequirement.get()) >
                    <#assign rootTitle = inflection.of(requirements.grandparentRequirement.get().displayName).asATitle() >
                    > <a href="${rootReport}" title="${rootTitle}">${formatter.truncatedHtmlCompatible(rootTitle,40)}</a>
                </#if>
                > ${formatter.truncatedHtmlCompatible(parentTitle,40)}
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
                    <h2><i class="fa fa-book"></i> ${parentType}: ${issueNumber} ${formatter.htmlCompatible(parentTitle)}</h2>
                    <#if parentRequirement.narrative.renderedText?has_content>
                        <div class="requirementNarrativeTitle">
                        ${formatter.addLineBreaks(formatter.renderDescription(parentRequirement.narrative.renderedText))}
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
                                        <td class="summary-leading-column"><a href="${requirementReport}">${requirementTitle}</a></td>
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
                                    <td class="summary-leading-column"><a href="#test-results-table">Acceptance Criteria (tests)</a></td>
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
                                <li><a href="#tabs-2">Examples (${requirements.parentRequirement.get().exampleCount}
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
