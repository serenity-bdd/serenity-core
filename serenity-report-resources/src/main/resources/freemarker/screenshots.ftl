<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Screenshots</title>

    <#include "libraries/favicon.ftl">
    <#include "libraries/common.ftl">
    <#include "components/stacktrace.ftl">

    <!-- Swiper.js local styles and script -->
    <link rel="stylesheet" href="swiper/swiper-bundle.min.css" />
    <script src="swiper/swiper-bundle.min.js"></script>

    <script type="text/javascript">
        document.addEventListener("DOMContentLoaded", function () {
            const urlParams = new URLSearchParams(window.location.search);
            const startIndex = parseInt(urlParams.get("screenshot")) || 0;

            new Swiper('.swiper', {
                initialSlide: startIndex,
                effect: 'fade',
                speed: 200,
                pagination: {
                    el: ".swiper-pagination",
                    clickable: true,
                    renderBullet: function (index, className) {
                        return '<span class="' + className + '">' + (index + 1) + "</span>";
                    },
                },
                navigation: {
                    nextEl: '.swiper-button-next',
                    prevEl: '.swiper-button-prev'
                },
                keyboard: {
                    enabled: true,
                    onlyInViewport: true
                }
            });
        });
    </script>

    <style>
        .swiper {
            width: 100%;
            max-width: 800px;
            margin: 20px auto;
        }
        .swiper-slide img {
            width: 100%;
            height: auto;
            object-fit: contain;
        }

        .swiper-slide img {
            display: block;
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .swiper-pagination-bullet {
            width: 20px;
            height: 20px;
            text-align: center;
            line-height: 20px;
            font-size: 12px;
            color: #000;
            opacity: 1;
            background: lightgrey;
        }

        .swiper-pagination-bullet-active {
            color: #fff;
            background: #007aff;
        }
    </style>
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
                                    <#if tag.type != "context" && tag.type != "tag">
                                        (${tag.type})
                                    </#if>
                                </a>
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
                            <#if (testOutcome.externalLink)?? && (testOutcome.externalLink.url)??>
                                <td valign="top">
                                    <a href="${testOutcome.externalLink.url}" class="tag"
                                       title="${testOutcome.externalLink.type}">
                                        <i class="fs-2 bi bi-camera-reels"></i>
                                    </a>
                                </td>
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
                <#if (testOutcome.failureDetails.pageSourceExists()) >
                    <div class="panel-body">
                        <a href="${testOutcome.failureDetails.pageSourceLink}" target="_blank" class="btn btn-info">HTML Source</a>
                    </div>
                </#if>
            </div>
        </#if>
    <!-- Swiper Carousel -->
    <div class="swiper">
        <div class="swiper-wrapper">
            <#foreach screenshot in screenshots>
                <#if screenshot_has_next>
                    <#assign caption = "${screenshot.html.description}">
                <#else>
                    <#if testOutcome.conciseErrorMessage??>
                        <#assign caption = "${screenshot.description}: <span class='${outcome_text}'>${testOutcome.result}</span>">
                    <#else>
                        <#assign caption = "${screenshot.description}">
                    </#if>
                </#if>
                <div class="swiper-slide">
                    <img src="${screenshot.filename}" title="${(formatter.depthIndicatorForLevel(screenshot.depth))!} ${caption}" width="${screenshot.width?string.computer}" />
                </div>
            </#foreach>
        </div>
        <div class="swiper-button-prev"></div>
        <div class="swiper-button-next"></div>
        <div class="swiper-pagination"></div>
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
