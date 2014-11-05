<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Home</title>
    <link rel="shortcut icon" href="favicon.ico" >
    <link href="css/core.css" rel="stylesheet" type="text/css"/>
    <style type="text/css">
	#slider {
	    position:relative;
	    width:1000px; /* Change this to your images width */
	    height:800px; /* Change this to your images height */
	    background:url(images/loading.gif) no-repeat 50% 50%;
	}
	#slider img {
	    position:absolute;
	    top:0px;
	    left:0px;
	    display:none;
	}
	#slider a {
	    border:0;
	    display:block;
	}

    </style>

	<!-- CSS Files -->
    <link href="slides/js/global.css" rel="stylesheet" type="text/css"/>

    <script src="scripts/jquery.js"></script>

	<link rel="stylesheet" href="nivo-slider/nivo-slider.css" type="text/css" media="screen" />
	<link rel="stylesheet" href="nivo-slider/themes/default/default.css" type="text/css" media="screen" />

	<script src="nivo-slider/jquery.nivo.slider.pack.js" type="text/javascript"></script>

	<script type="text/javascript">

    function getUrlVars() {
        var vars = {};
        var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
            vars[key] = value;
        });
        return vars;
    }

    var screenshotIndex = getUrlVars()["screenshot"];

	$(window).load(function() {
	    $('#slider').nivoSlider({
            startSlide:screenshotIndex,
            effect:'fade',
			animSpeed:200,
			directionNavHide:false,
			manualAdvance:true,
			keyboardNav:true
		});
	});
	</script>

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

<#-- HEADER
-->
<#if testOutcome.result == "FAILURE"><#assign outcome_icon = "fail.png"><#assign outcome_text = "failing-color">
<#elseif testOutcome.result == "ERROR"><#assign outcome_icon = "cross.png"><#assign outcome_text = "error-color">
<#elseif testOutcome.result == "SUCCESS"><#assign outcome_icon = "success.png"><#assign outcome_text = "success-color">
<#elseif testOutcome.result == "PENDING"><#assign outcome_icon = "pending.png"><#assign outcome_text = "pending-color">
<#else><#assign outcome_icon = "ignor.png"><#assign outcome_text = "ignore-color">
</#if>
<div class="middlecontent">
    <div id="contenttop">
        <div class="middlebg">
            <span class="bluetext"><a href="index.html" class="bluetext">Home</a> > <a href="${narrativeView}.html">${formatter.truncatedHtmlCompatible(testOutcome.unqualified.title,60)} </a> > Screenshots </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
    <#include "menu.ftl">
    <@main_menu selected="home" />

    <div class="clr"></div>
    <div id="contentbody">
        <div class="titlebar">
        <div class="story-title">
            <table width="1005">
                <td width="50"><img class="story-outcome-icon" src="images/${outcome_icon}" width="25" height="25"/> </td>
            <#if (testOutcome.videoLink)??>
                <td width="25"><a href="${testOutcome.videoLink}"><img class="story-outcome-icon" src="images/video.png" width="25" height="25" alt="Video"/></a></td>
            </#if>
                <td width="%"><span class="test-case-title"><span
                        class="${outcome_text}">${testOutcome.titleWithLinks}<span class="related-issue-title">${testOutcome.formattedIssues}</span></span></span>
                </td>
                <td width="100"><span class="test-case-duration"><span class="greentext">${testOutcome.durationInSeconds}s</span></span>
                </td>
                </tr>
                <tr>
                    <td colspan="3">
                    <#if (parentRequirement.isPresent())>
                        <div>
                            <#assign parentTitle = inflection.of(parentRequirement.get().name).asATitle() >
                            <#assign parentType = inflection.of(parentRequirement.get().type).asATitle() >
                            <#if (parentRequirement.get().cardNumber?has_content) >
                                <#assign issueNumber = "[" + formatter.addLinks(parentRequirement.get().cardNumber) + "]" >
                            <#else>
                                <#assign issueNumber = "">
                            </#if>
                            <h3>${parentType}: ${issueNumber} ${parentTitle}</h3>
                            <div class="requirementNarrativeTitle">
                            ${formatter.renderDescription(parentRequirement.get().narrative.renderedText)}
                            </div>
                        </div>
                    </#if>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                    <#list testOutcome.tags as tag>
                        <#assign tagReport = absoluteReportName.forTag(tag) />
                        <#assign tagTitle = inflection.of(tag.shortName).asATitle() >
                        <#assign tagReport = reportName.forTag(tag.name) />
                        <a class="tagLink" href="${tagReport}">${tagTitle} (${tag.type})</a>
                    </#list>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    </div>

    <div class="clr"></div>

    <#if (testOutcome.isDataDriven())>
        <div class="story-title">
            <h3>Scenario:</h3>
            <div class="scenario">${formatter.formatWithFields(testOutcome.dataDrivenSampleScenario, testOutcome.exampleFields)}</div>

        </div>
    </#if>


    <div id="beforetable"></div>
    <div id="contenttilttle">

	 <div class="slider-wrapper theme-default">
		<div id="slider">
            <#foreach screenshot in screenshots>
                <img src="${screenshot.filename}" alt="${screenshot.shortErrorMessage}" title="${screenshot.html.description}" width="${screenshot.width?string.computer}"/>
            </#foreach>
        </div>
	  </div>


    </div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter">
    <span class="version">Thucydides version ${thucydidesVersionNumber} - Build ${buildNumber}</span>
</div>
</body>
</html>
