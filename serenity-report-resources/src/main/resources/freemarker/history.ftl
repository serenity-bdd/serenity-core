<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Home</title>
<#include "libraries/favicon.ftl">

    <#include "libraries/common.ftl">
    <#include "libraries/jquery-ui.ftl">

    <script class="code" type="text/javascript">$(document).ready(function () {

        var specified = [];
        var done = [];
        var skipped = [];
        var failing = [];
        var min_date;

        <#assign row = 0>
        <#assign max_specified = 0>
        <#foreach snapshot in history>
        var date = new Date(${snapshot.time.toString('yyyy')}, ${snapshot.time.toString('M')?number - 1}, ${snapshot.time.toString('d')},${snapshot.time.toString('H')}, ${snapshot.time.toString('m')}, ${snapshot.time.toString('s')});

        specified.push([date, ${snapshot.specifiedSteps}]);
        done.push([date,${snapshot.passingSteps}]);
        skipped.push([date,${snapshot.skippedSteps}]);
        failing.push([date,${snapshot.failingSteps}]);

        <#if row == 0 >
            min_date = date;
        </#if>

        <#if snapshot.specifiedSteps &gt; max_specified >
            <#assign max_specified = snapshot.specifiedSteps>
        </#if>

        <#assign row = row + 1>
        </#foreach>

        targetPlot = $.jqplot('chart_div', [failing,skipped,done,specified], {

            axesDefaults : {
                labelRenderer : $.jqplot.CanvasAxisLabelRenderer
            },

            axes : {

                xaxis : {
                    renderer : $.jqplot.DateAxisRenderer,
                    tickOptions: {formatString:'%b %#d'},
                    min : min_date,
                    tickInterval: '1 week'
                },

                yaxis : {
                    min: 0,
                    max: ${max_specified},
                    tickInterval: ${max_specified} / 5,
                    tickOptions: {formatString: '%d' }
                }

            },

            legend: {
                show:true,
                location: 'nw'
            },

			cursor:{
					show: true,
					zoom:true,
					showTooltip:true
    		},

            series: [
                {color:'#ff0000', label:'Failing'},
   				{color:'#ff9131', label:'Skipped'},
                {color:'#00ff00', label:'Done'},
                {color:'#0000ff', label:'Specified'}

            ]


        });

        controllerPlot = $.jqplot('controller_div', [failing,skipped,done,specified], {

            seriesDefaults:{ showMarker: false },

            series: [
                {color:'#ff0000', label:'Failing'},
				{color:'#ff9131', label:'Skipped'},
				{color:'#00ff00', label:'Done'},
				{color:'#0000ff', label:'Specified'}
            ],

            cursor:{
                show: true,
                showTooltip: false,
                zoom:true,
                constrainZoomTo: 'x'
            },

            axesDefaults: {
                useSeriesColor:true,
                rendererOptions: {
                    alignTicks: true
                }
            },

            axes : {

                xaxis : {
                    renderer : $.jqplot.DateAxisRenderer,
                    tickOptions: {formatString:'%b %#d'},
                    min : min_date,
                    tickInterval: '1 week'
                },

                yaxis : {
				show:false,
                    min: 0,
                    max: ${max_specified},
                    tickInterval: ${max_specified} / 5,
                    tickOptions: {formatString: '%d' }
                }
            } //axes
        }); //conroller plot

        $.jqplot.Cursor.zoomProxy(targetPlot, controllerPlot);

        $.jqplot._noToImageButton = true;

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
    <div id="contenttop">
        <div class="leftbg"></div>
        <div class="middlebg">
            <span class="bluetext"><a href="index.html" class="bluetext">Home</a> > History</span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
<#include "menu.ftl">
<@main_menu selected="history" />
    <div class="clr"></div>
    <div id="beforetable"></div>
    <div id="results-dashboard">
        <div class="middlb">
            <div class="table">
                 <table class='overview'>
                  <tr>
                     <td>
                       <div id='chart_div' style='width: 700px; height: 400px;'></div>
                     </td>
                  <tr>
                  <tr>
                     <td>
                       <div id='controller_div' style='width: 700px; height: 100px;'></div>
                     </td>
                  <tr>


                 </table>
            </div>
        </div>
    </div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter"></div>

</body>
</html>
