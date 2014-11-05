<!DOCTYPE html>
<html>
<#assign pageTitle = inflection.of(requirements.type).inPluralForm().asATitle() >
<#assign requirementTypeTitle = inflection.of(requirements.type).asATitle() >
<head>
    <meta charset="UTF-8" />
    <title>${pageTitle}</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" type="text/css" href="jqplot/jquery.jqplot.min.css"/>

    <!--[if IE]>
    <script language="javascript" type="text/javascript" src="jit/Extras/excanvas.js"></script><![endif]-->

    <script type="text/javascript" src="scripts/jquery.js"></script>
    <script type="text/javascript" src="datatables/media/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="scripts/dygraph-combined.js"></script>


    <link type="text/css" href="jqueryui/css/start/jquery-ui-1.8.18.custom.css" rel="Stylesheet" />
    <script type="text/javascript" src="jqueryui/js/jquery-ui-1.8.18.custom.min.js"></script>

    <link type="text/css" media="screen" href="css/screen.css" rel="Stylesheet" />
</head>

<body>
<div id="topheader">
    <div id="topbanner">
        <div id="logo"><a href="index.html"><img src="images/logo.jpg" border="0"/></a></div>
        <div id="projectname-banner" style="float:right">
            <span class="projectname">${reportOptions.projectName}</span>
        </div>
    </div>
</div>


<div class="middlecontent">
    <div id="contenttop">
        <div class="middlebg">
            <span class="bluetext"><a href="index.html" class="bluetext">Home</a> > ${pageTitle} </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
    <div class="menu">
        <ul>
            <li><a href="index.html">Test Results</a></li>
            <li><a href="capabilities.html">Requirements</a></li>
            <li><a href="progress-report.html" class="current">Progress</a></li>
            <li><a href="releases.html">Releases</a></li>
            <#foreach tagType in allTestOutcomes.firstClassTagTypes>
                <#assign tagReport = reportName.forTagType(tagType) >
                <#assign tagTypeTitle = inflection.of(tagType).inPluralForm().asATitle() >
                <li><a href="${tagReport}">${tagTypeTitle}</a></li>
            </#foreach>
            <li><a href="history.html">History</a></li>
        </ul>
        <span class="date-and-time">Tests run ${timestamp}</span>
        <br style="clear:left"/>
    </div>

    <div class="clr"></div>
    <div id="beforetable"></div>
    <div id="results-dashboard">
        <div class="middlb">
            <div class="table">
                <div id="progress-graph"></div>
            <#assign requirementsTitle = inflection.of(requirements.type).inPluralForm().asATitle() >
                <script type="text/javascript">
                    g = new Dygraph(
                            document.getElementById("progress-graph"),
                            "Build,${requirementsTitle},Passed,Failed,Estimated,Total\n" +
                            <#foreach snapshot in progress>
                                "${snapshot.formattedTime}, ${snapshot.total}, ${snapshot.completed}, ${snapshot.failed}, ${snapshot.estimated}, ${snapshot.total}\n"<#if snapshot_has_next> +</#if>
                            </#foreach>,
                            { fillGraph: true,
                                width: 640,
                                height: 480,
                                title: 'Project Progress (${requirementsTitle})',
                                titleHeight: 32,
                                colors: ['#ffda4b', 'green', '#EE1111','#bccb8e', '#00aeff'],
                                legend: 'always',
                                drawAxesAtZero: true,
                                includeZero: true,

                                'Estimated': {
                                    fillGraph: false
                                },
                                'Total': {
                                    fillGraph: false
                                }
                            }
                    );
                </script>
            </div>
        </div>
    </div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter"></div>

</body>
</html>
