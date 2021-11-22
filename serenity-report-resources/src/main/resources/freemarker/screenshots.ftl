<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Screenshots</title>

<#include "libraries/favicon.ftl">

<#include "libraries/common.ftl">
<#include "libraries/nivo-slider.ftl">

<#include "components/stacktrace.ftl">

    <script type="text/javascript">

        function getUrlVars() {
            var vars = {};
            var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function (m, key, value) {
                vars[key] = value;
            });
            return vars;
        }

        var screenshotIndex = getUrlVars()["screenshot"];

        $(window).load(function () {
            $('#slider').nivoSlider({
                startSlide: screenshotIndex,
                effect: 'fade',
                animSpeed: 200,
                manualAdvance: true
            });
        });

        $(window).keydown(function(e) {
            switch(e.which) {
                case 37:
                    $('a.nivo-prevNav').click();
                    break;
                case 39:
                    $('a.nivo-nextNav').click();
                    break;
            }
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

<#-- HEADER
-->
<div class="middlecontent">
    <div id="contenttop">
        <div class="middlebg">
            <span class="breadcrumbs">
                <a href="index.html">Home</a>
            <#if (parentLink?has_content)>
                > <span class="truncate-40"><a
                    href="${parentLink}">${formatter.htmlCompatible(inflection.of(parentTitle).asATitle())}</a></span>
            </#if>
                > <span class="truncate-60"><a
                    href="${narrativeView}.html">${formatter.htmlCompatible(testOutcome.unqualified.title)} </a></span>
                > Screenshots
            </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
<#include "menu.ftl">
<@main_menu selected="home" />

    <div class="clr"></div>

<#if testOutcome.result == "FAILURE"><#assign outcome_text = "failing-color">
<#elseif testOutcome.result == "ERROR"><#assign outcome_text = "error-color">
<#elseif testOutcome.result == "SUCCESS"><#assign outcome_text = "success-color">
<#elseif testOutcome.result == "PENDING"><#assign outcome_text = "pending-color">
<#elseif testOutcome.result == "COMPROMISED"><#assign outcome_text = "compromised-color">
<#else><#assign outcome_text = "ignore-color">
</#if>

<#assign title_outcome_icon =  formatter.resultIcon().inLarge().forResult(testOutcome.result) />

<#-- TEST TITLE-->
    <div id="contentbody">
        <div class="titlebar">
            <div class="story-title">
                <table class="outcome-header">
                    <tr>
                        <td>
                        <#if (parentRequirement?? && parentRequirement.isPresent())>
                            <div>
                                <#assign parentTitle = inflection.of(parentRequirement.get().displayName).asATitle() >
                                <#assign parentType = inflection.of(parentRequirement.get().type).asATitle() >
                                <#if (parentRequirement.get().cardNumber?has_content) >
                                    <#assign issueNumber = "[" + reportFormatter.addLinks(parentRequirement.get().cardNumber) + "]" >
                                <#else>
                                    <#assign issueNumber = "">
                                </#if>
                                <h3 class="discreet-story-header">
                                    <i class="fs-2 bi bi-chat-left-quote large-icon"></i>
                                    <span class="story-header-title">${parentTitle} ${issueNumber}</span>
                                    <#assign tagStyle = styling.tagStyleFor(tag) >
                                    <span class="badge tag-badge" style="${tagStyle}">${parentType}</span>
                                </h3>

                                <div class="discreet-requirement-narrative-title">
                                ${formatter.renderDescription(parentRequirement.get().narrative.renderedText)}
                                </div>
                            </div>
                        <#elseif (featureOrStory?? && featureOrStory.isPresent())>
                            <div>
                                <#assign parentTitle = formatter.renderDescription(inflection.of(featureOrStory.get().displayName).asATitle())>
                                <#assign parentType = inflection.of(featureOrStory.get().type).asATitle() >
                                <h3 class="discreet-story-header">
                                    <i class="fs-2 bi bi-chat-left-quote large-icon"></i>
                                    <span class="story-header-title">${parentTitle}</span>
                                    <#assign tagStyle = styling.tagStyleFor(tag) >
                                    <span class="badge tag-badge" style="${tagStyle}">${parentType}</span>
                                </h3>

                                <div class="discreet-requirement-narrative-title">
                                ${formatter.renderDescription(featureOrStory.get().narrative)}
                                </div>
                            </div>
                        </#if>

                        <#if (testOutcome.backgroundTitle?has_content)>
                            <div class="requirementNarrative">Background: ${testOutcome.backgroundTitle}
                                <#if (testOutcome.backgroundTitle?has_content)>
                                    <p>${testOutcome.backgroundDescription}</p>
                                </#if>
                            </div>
                        </#if>
                        </td>
                        <td valign="top" align="right">
                        <#list filteredTags as tag>
                            <#assign tagReport = absoluteReportName.forTag(tag) />
                            <#assign tagTitle = inflection.of(tag.shortName).asATitle() >
                            <p class="tag">
                                <#assign tagStyle = styling.tagStyleFor(tag) >
                                <span class="badge tag-badge" style="${tagStyle}"
                                    <i class="bi bi-tag-fill"></i>&nbsp;<a class="tagLink" style="${tagStyle}" href="${tagReport}">${formatter.htmlCompatible(tagTitle)}
                                    (${tag.type})</a>
                                </span>
                            </p>
                        </#list>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="story-title">
                <table class="outcome-header">
                    <tr>
                        <td colspan="2" class="test-title-bar">
                            <span class="outcome-icon">${title_outcome_icon}</span>
                        <#if (testOutcome.videoLink)??>
                            <a href="${relativeLink!}${testOutcome.videoLink}"><img class="story-outcome-icon"
                                                                                    src="images/video.png" width="25"
                                                                                    height="25" alt="Video"/></a>
                        </#if>
                            <span class="test-case-title">
                                <span class="${outcome_text}">${formatter.htmlCompatibleStoryTitle(testOutcome.unqualified.titleWithLinks)}
                                    <span class="related-issue-title">${testOutcome.formattedIssues}</span>
                                </span>
                            </span>

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
        <div class="scenario">${formatter.formatWithFields(testOutcome.dataDrivenSampleScenario)}</div>
    </div>
</#if>

    <div id="beforetable"></div>
    <div id="contenttilttle">
        <#if (testOutcome.result == "FAILURE" || testOutcome.result == "ERROR" || testOutcome.result == "COMPROMISED")>
            <div class="screenshotFailure panel panel-danger">
                <#if (testOutcome.errorMessage)??>
                    <#if (testOutcome.nestedTestFailureCause)??>
                        <@stacktrace title=formatter.htmlAttributeCompatible(testOutcome.conciseErrorMessage) cause=testOutcome.nestedTestFailureCause id="overall" />
                    <#else>
                        <div class="panel-heading title="${formatter.htmlAttributeCompatible(testOutcome.conciseErrorMessage)}">
                            ${formatter.htmlAttributeCompatible(testOutcome.conciseErrorMessage, 244)}
                        </div>
                    </#if>
                </#if>
                <div class="panel-body">
                    <a href="${testOutcome.failureDetails.pageSourceLink}" target="_blank" class="btn btn-info">HTML Source</a>
                </div>
            </div>
        </#if>
        <div class="slider-wrapper theme-default">
            <div id="slider">
            <#foreach screenshot in screenshots>
                <#if screenshot_has_next>
                    <#assign caption = "${screenshot.html.description}">
                <#else>
                    <#if testOutcome.conciseErrorMessage??>
                        <#assign caption = "${screenshot.html.description}: <span class='${outcome_text}'>${testOutcome.result}</span>">
                    <#else>
                        <#assign caption = "${screenshot.html.description}">
                    </#if>
                </#if>
                <#list 0..<screenshot.depth as i></#list>
                <img src="${screenshot.filename}" title="${(formatter.depthIndicatorForLevel(screenshot.depth))!} ${caption}" width="${screenshot.width?string.computer}"/>
            </#foreach>
            </div>
        </div>


    </div>
</div>
<#macro repeat input times>
    <#list 0..<times as i>${input}</#list>
</#macro>
<div id="beforefooter"></div>
<div id="bottomfooter">
    <span class="version">Serenity BDD version ${serenityVersionNumber!"SNAPSHOT-BUILD"}</span>
</div>
</body>
</html>
