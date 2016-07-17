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
<#assign pie = true>
<#include "libraries/jqplot.ftl">

<#assign successfulManualTests = (testOutcomes.count("manual").withResult("SUCCESS") > 0)>
<#assign pendingManualTests = (testOutcomes.count("manual").withResult("PENDING") > 0)>
<#assign ignoredManualTests = (testOutcomes.count("manual").withResult("IGNORED") > 0)>
<#assign failingManualTests = (testOutcomes.count("manual").withResult("FAILURE") > 0)>

    <script class="code" type="text/javascript">$(document).ready(function () {
        var test_results_plot = $.jqplot('test_results_pie_chart', [
            [
                ['Passing', ${testOutcomes.proportionOf("automated").withResult("success")}],
                <#if (successfulManualTests)>['Passing (manual)', ${testOutcomes.proportionOf("manual").withResult("success")}],</#if>
                ['Pending', ${testOutcomes.proportionOf("automated").withResult("pending")}],
                <#if (pendingManualTests)>['Pending (manual)', ${testOutcomes.proportionOf("manual").withResult("pending")}],</#if>
                ['Ignored', ${testOutcomes.proportionOf("automated").withResult("ignored")}],
                <#if (pendingManualTests)>['Ignored (manual)', ${testOutcomes.proportionOf("manual").withResult("ignored")}],</#if>
                ['Failing', ${testOutcomes.proportionOf("automated").withResult("failure")}],
                <#if (failingManualTests)>['Failing (manual)', ${testOutcomes.proportionOf("manual").withResult("failure")}],</#if>
                ['Errors',  ${testOutcomes.proportionOf("automated").withResult("error")}]
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
                '#fc6e1f'],
            seriesDefaults: {
                renderer: $.jqplot.PieRenderer,
                trendline: { show: false },
                rendererOptions: { padding: 8, showDataLabels: true }
            },
            legend: {
                show: true,
                placement: 'outside',
                rendererOptions: {
                    numberRows: 2
                },
                location: 's',
                marginTop: '15px'
            },
            series: [
                {label: '${testOutcomes.count("automated").withResult("success")} / ${testOutcomes.total} tests passed' },
            <#if (successfulManualTests)>
                {label: '${testOutcomes.count("manual").withResult("success")} / ${testOutcomes.total} manual tests passed' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("pending")} / ${testOutcomes.total} tests pending'},
            <#if (pendingManualTests)>
                {label: '${testOutcomes.count("manual").withResult("pending")} / ${testOutcomes.total} manual tests pending' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("ignored")} / ${testOutcomes.total} tests not executed'},
            <#if (ignoredManualTests)>
                {label: '${testOutcomes.count("manual").withResult("ignored")} / ${testOutcomes.total} manual tests not executed' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("failure")} / ${testOutcomes.total} tests failed'},
            <#if (failingManualTests)>
                {label: '${testOutcomes.count("manual").withResult("failure")} / ${testOutcomes.total} manual tests failed' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("error")} / ${testOutcomes.total} errors'}
            ]
        });

        var weighted_test_results_plot = $.jqplot('weighted_test_results_pie_chart', [
            [
                ['Passing', ${testOutcomes.proportionalStepsOf("automated").withResult("success")}],
                <#if (successfulManualTests)>['Passing (manual)', ${testOutcomes.proportionalStepsOf("manual").withResult("success")}],</#if>
                ['Pending', ${testOutcomes.proportionalStepsOf("automated").withResult("pending")}],
                <#if (pendingManualTests)>['Pending (manual)', ${testOutcomes.proportionalStepsOf("manual").withResult("pending")}],</#if>
                ['Ignored', ${testOutcomes.proportionalStepsOf("automated").withResult("ignored")}],
                <#if (ignoredManualTests)>['Pending (manual)', ${testOutcomes.proportionalStepsOf("manual").withResult("ignored")}],</#if>
                ['Failing', ${testOutcomes.proportionalStepsOf("automated").withResult("failure")}],
                <#if (failingManualTests)>['Failing (manual)', ${testOutcomes.proportionalStepsOf("manual").withResult("failure")}],</#if>
                ['Errors', ${testOutcomes.proportionalStepsOf("automated").withResult("error")}]
            ]
        ], {

            gridPadding: {top: 0, bottom: 38, left: 0, right: 0},
            seriesColors: ['#30cb23',
                <#if (successfulManualTests)>'#28a818',</#if>
                '#a2f2f2',
                <#if (pendingManualTests)>'#8be1df',</#if>
                '#eeeadd',
                <#if (ignoredManualTests)>'#d3d3d3',</#if>
                '#f8001f',
                <#if (failingManualTests)>'#e20019',</#if>
                '#fc6e1f'],

            seriesDefaults: {
                renderer: $.jqplot.PieRenderer,
                trendline: { show: false },
                rendererOptions: { padding: 8, showDataLabels: true }
            },
            legend: {
                show: true,
                placement: 'outside',
                rendererOptions: {
                    numberRows: 2
                },
                location: 's',
                marginTop: '15px'
            },
            series: [
                {label: '${testOutcomes.count("automated").withResult("success")} / ${testOutcomes.total} tests passed (${testOutcomes.decimalPercentageSteps("automated").withResult("success")}% of all test steps)' },
            <#if (successfulManualTests)>
                {label: '${testOutcomes.count("manual").withResult("success")} / ${testOutcomes.total} manual tests passed (${testOutcomes.decimalPercentageSteps("manual").withResult("success")}% of all test steps)' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("pending")} / ${testOutcomes.total} tests pending'},
            <#if (pendingManualTests)>
                {label: '${testOutcomes.count("manual").withResult("pending")} / ${testOutcomes.total} manual tests pending' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("ignored")} / ${testOutcomes.total} tests not executed'},
            <#if (ignoredManualTests)>
                {label: '${testOutcomes.count("manual").withResult("ignored")} / ${testOutcomes.total} manual tests not executed' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("failure")} / ${testOutcomes.total} tests failed (${testOutcomes.decimalPercentageSteps("automated").withResult("failure")}% of all test steps)'},
            <#if (failingManualTests)>
                {label: '${testOutcomes.count("manual").withResult("failure")} / ${testOutcomes.total} manual tests failed (${testOutcomes.decimalPercentageSteps("manual").withResult("failure")}% of all test steps)' },
            </#if>
                {label: '${testOutcomes.count("automated").withResult("error")} / ${testOutcomes.total} errors (${testOutcomes.decimalPercentageSteps("automated").withResult("error")}% of all test steps)'}
            ]
        });

        // Results table
        $('#test-results-table').DataTable({
            "order": [
                [ 1, "asc" ]
            ],
            "pageLength": 25
        });

        // Pie charts
        $('#test-results-tabs').tabs();

        $('#toggleNormalPieChart').click(function () {
            $("#test_results_pie_chart").toggle();
        });

        $('#toggleWeightedPieChart').click(function () {
            $("#weighted_test_results_pie_chart").toggle();
        });

    <#if !reportOptions.displayPiechart>
        $("#test_results_pie_chart").hide();
        $("#weighted_test_results_pie_chart").hide();
    </#if>


    })
    ;
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


            </div>
        <#--- Test Results end -->
        </div>
    </div>
</div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter">
    <span class="version">Serenity version ${serenityVersionNumber}</span>
</div>

</body>
</html>
