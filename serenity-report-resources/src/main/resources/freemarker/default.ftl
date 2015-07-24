<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>${testOutcome.unqualified.title}</title>
    <link rel="shortcut icon" href="favicon.ico">

<#include "libraries/common.ftl">
<#include "libraries/jquery-ui.ftl">
<#include "libraries/datatables.ftl">
<#include "libraries/imgpreview.ftl">

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

<#-- HEADER
-->
<div class="middlecontent">
    <div id="contenttop">
        <div class="middlebg">
        <span class="breadcrumbs">
            <a href="index.html" class="breadcrumbs">Home</a>

        <#list breadcrumbs as breadcrumb>
            <#assign breadcrumbReport = absoluteReportName.forRequirement(breadcrumb) />
            <#assign breadcrumbTitle = inflection.of(breadcrumb.shortName).asATitle() >
            > <a href="${breadcrumbReport}">${formatter.truncatedHtmlCompatible(breadcrumbTitle,40)}</a>
        </#list>
            > ${formatter.truncatedHtmlCompatible(testOutcome.title,80)}
        </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
<#include "menu.ftl">
<@main_menu selected="home" />
    <div class="clr"></div>

<#if testOutcome.result == "FAILURE"><#assign outcome_icon = "fail.png"><#assign outcome_text = "failing-color">
<#elseif testOutcome.result == "ERROR"><#assign outcome_icon = "cross.png"><#assign outcome_text = "error-color">
<#elseif testOutcome.result == "SUCCESS"><#assign outcome_icon = "success.png"><#assign outcome_text = "success-color">
<#elseif testOutcome.result == "PENDING"><#assign outcome_icon = "pending.png"><#assign outcome_text = "pending-color">
<#else><#assign outcome_icon = "ignor.png"><#assign outcome_text = "ignore-color">
</#if>
<#-- TEST TITLE-->
    <div id="contentbody">
        <div class="titlebar">
            <div class="story-title">
                <table class="outcome-header">
                    <tr>
                        <td>
                        <#if (parentRequirement?? && parentRequirement.isPresent())>
                            <div>
                                <#assign parentTitle = inflection.of(parentRequirement.get().name).asATitle() >
                                <#assign parentType = inflection.of(parentRequirement.get().type).asATitle() >
                                <#if (parentRequirement.get().cardNumber?has_content) >
                                    <#assign issueNumber = "[" + formatter.addLinks(parentRequirement.get().cardNumber) + "]" >
                                <#else>
                                    <#assign issueNumber = "">
                                </#if>
                                <h3 class="discreet-story-header">
                                    <i class="fa fa-comments-o"></i>
                                    <span class="story-header-title">${parentTitle} ${issueNumber}</span>
                                </h3>

                                <div class="discreet-requirement-narrative-title">
                                ${formatter.renderDescription(parentRequirement.get().narrative.renderedText)}
                                </div>
                            </div>
                        <#elseif (featureOrStory?? && featureOrStory.isPresent())>
                            <div>
                                <#assign parentTitle = inflection.of(featureOrStory.get().name).asATitle() >
                                <#assign parentType = inflection.of(featureOrStory.get().type.toString()).asATitle() >
                                <h3 class="discreet-story-header">
                                    <i class="fa fa-comments-o"></i>
                                    <span class="story-header-title">${parentTitle}</span>
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
                        <td valign="top">
                        <#list filteredTags as tag>
                            <#assign tagReport = absoluteReportName.forRequirementOrTag(tag) />
                            <#assign tagTitle = inflection.of(tag.shortName).asATitle() >
                            <p class="tag">
                                <span class="badge tag-badge">
                                    <i class="fa fa-tag"></i>&nbsp;<a class="tagLink" href="${tagReport}">${tagTitle}
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
                            <span class="outcome-icon"><img class="story-outcome-icon"
                                                            src="images/${outcome_icon}"/></span>
                        <#if (testOutcome.videoLink)??>
                            <a href="${relativeLink!}${testOutcome.videoLink}"><img class="story-outcome-icon"
                                                                                    src="images/video.png" width="25"
                                                                                    height="25" alt="Video"/></a>
                        </#if>
                            <span class="test-case-title">
                            <#assign testOutcomeTitle = inflection.of(testOutcome.unqualified.titleWithLinks).asATitle() >

                                <span class="${outcome_text}">${testOutcomeTitle}
                                    <span class="related-issue-title">${testOutcome.formattedIssues}</span>
                            </span>
                        </span>
                        <#if (testOutcome.driver)??>
                            <span style="float:right"><img src="images/driver-${testOutcome.driver}.png" height="20"
                                                           alt="${testOutcome.driver}"
                                                           title="${testOutcome.driver}"/></span>
                        </#if>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <div class="clr"></div>

<#if (testOutcome.isDataDriven())>
    <div class="story-title">
        <h3 class="story-header">Scenario:</h3>

        <div class="scenario">${formatter.formatWithFields(testOutcome.dataDrivenSampleScenario, testOutcome.exampleFields)}</div>

    </div>
</#if>

    <div id="beforetable"></div>

<#if (testOutcome.descriptionText.isPresent() && testOutcome.descriptionText.get()?has_content)>
    <div class="story-title">
        <div class="requirementNarrativeTitle">
        ${formatter.renderDescription(testOutcome.descriptionText.get())}
        </div>
    </div>
</#if>

<#if (testOutcome.isDataDriven())>

    <#list testOutcome.dataTable.dataSets as dataSet >
        <h3 class="story-header">Examples:<#if dataSet.title??>&nbsp;${dataSet.title}</#if></h3>
        <#if dataSet.description??>
            <div class="requirementNarrative">${dataSet.description}</div>
        </#if>
        <div class="example-table">
            <table>
                <thead>
                <tr>
                    <#list testOutcome.dataTable.headers as header>
                        <th>${inflection.of(header).asATitle()}</th>
                    </#list>
                </tr>
                </thead>
                <tbody>
                    <#assign rowIndex = dataSet.startRow >
                    <#list dataSet.rows as row>
                    <tr>
                        <#list row.values as value>
                            <td class="test-${row.result}"><a href="#${rowIndex}">${formatter.htmlCompatible(value)}</a>
                            </td>
                        </#list>
                    </tr>
                        <#assign rowIndex = rowIndex + 1 >
                    </#list>
                </tbody>
            </table>
        </div>
    </#list>
</#if>

    <div id="tablecontents">
        <div>
            <table class="step-table">
                <tr class="step-titles">
                    <th width="65"><#if (testOutcome.manual)><img src="images/worker.png" title="Manual test"/></#if>
                        &nbsp;
                    </th>

                <#if testOutcome.hasScreenshots()>
                    <th width="%" class="step-description-column greentext"><#if (testOutcome.manual)>Manual </#if>
                        Steps
                    </th>
                <#else>
                    <th width="%" class="step-description-wide-column greentext"><#if (testOutcome.manual)>Manual </#if>
                        Steps
                    </th>
                </#if>
                <#if testOutcome.hasScreenshots()>
                    <th width="120" class="greentext">Screenshot</th>
                </#if>
                    <th width="100" class="greentext">Outcome</th>
                    <th width="75" class="greentext">Duration</th>
                </tr>
                <tr class="step-table-separator">
                    <td colspan="5"></td>
                </tr>
            <#assign level = 1>
            <#assign screenshotCount = 0>
            <#macro write_step(step, step_number)>
                <@step_details step=step step_number=step_number level=level/>
                <#if step.isAGroup()>
                    <#if level == 1>
                    <tr>
                    <td colspan="5">
                    <table id="stepSection${step_number}" style="display:none; width:100%">

                    </#if>
                    <#assign level = level + 1>
                    <#list step.children as nestedStep>
                        <@write_step step=nestedStep step_number=""/>
                    </#list>
                    <#assign level = level-1>

                    <#if level == 1>
                    </table>
                    </td>
                    <tr>
                    </#if>
                </#if>
            </#macro>

            <#macro stacktrace(cause) >
                <div><!-- Stack trace -->
                    <button type="button" class="btn btn-danger btn-sm" data-toggle="modal"
                            data-target="#stacktraceModal">
                        View stack trace
                    </button>
                </div>
                <!-- Modal -->
                <div class="modal fade" id="stacktraceModal" tabindex="-1" role="dialog"
                     aria-labelledby="stacktraceModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"
                                        aria-label="Close"><span aria-hidden="true">&times;</span>
                                </button>
                                <h4 class="modal-title" id="stacktraceModalLabel">
                                ${cause.errorType} <#if (cause.message)??>:  ${cause.message} </#if>
                                </h4>
                            </div>
                            <div class="modal-body">
                                <#list cause.stackTrace as element>
                                ${element.className}.${element.methodName}(${element.fileName}
                                    :${element.lineNumber})
                                </#list>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">
                                    Close
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </#macro>

            <#macro restQueryData(restQuery, number) >
                <span>
                    <button type="button" class="btn btn-success btn-sm" data-toggle="modal"
                            data-target="#restModal-${number}">
                        REST Query
                    </button>
                </span>
                <!-- Modal -->
                <div class="modal fade" id="restModal-${number}" tabindex="-1" role="dialog"
                     aria-labelledby="restModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"
                                        aria-label="Close"><span aria-hidden="true">&times;</span>
                                </button>
                                <h4 class="modal-title" id="restModalLabel">
                                ${restQuery.formattedQuery}
                                </h4>
                            </div>
                            <div class="modal-body">
                                <p>Status code: ${restQuery.statusCode}</p>
                                <#if restQuery.contentType?has_content>
                                    <p>Content Type: ${restQuery.contentType}</p>
                                </#if>
                                <#if restQuery.content?has_content>
                                    <h5>Content Body</h5>
                                    <pre>${(formatter.renderXML(restQuery.content))!}</pre>
                                </#if>
                                <h5>Response Body</h5>
                                <pre>${formatter.renderXML(restQuery.responseBody)}</pre>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">
                                    Close
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </#macro>


            <#macro step_details(step, step_number, level)>
                <#if step.result == "FAILURE">
                    <#assign step_outcome_icon = "fail.png">
                <#elseif step.result == "ERROR">
                    <#assign step_outcome_icon = "cross.png">
                <#elseif step.result == "SUCCESS">
                    <#assign step_outcome_icon = "success.png">
                <#elseif step.result == "PENDING">
                    <#assign step_outcome_icon = "pending.png">
                <#else>
                    <#assign step_outcome_icon = "ignor.png">
                </#if>
                <#assign step_icon_size = 20>
                <#if (level>1)>
                    <#if step.isAGroup()>
                        <#assign step_class_root = "nested">
                    <#else>
                        <#assign step_class_root = "nested-group">
                    </#if>
                <#else>
                    <#assign step_class_root = "top-level">
                </#if>
                <#assign step_indent = level*20>
                <#if level == 1 && step.isAGroup()>
                    <#assign showAccordion = true/>
                <#else>
                    <#assign showAccordion = false/>
                </#if>
                <tr class="test-${step.result}">
                    <td width="60" class="step-icon">
                        <#if step_number?has_content><a name="${step_number}"/></#if>
                        <#if showAccordion>
                            <a href="javaScript:void(0)" onClick="toggleDiv('stepSection${step_number}')"
                               style="display:block">
                                <img src="images/plus.png" width="24" class="imgstepSection${step_number}"
                                     style="margin-left: 20px; float:left;  padding-right:5px"/>
                            </a>
                        <#else>
                            <img style="margin-left: ${step_indent}px; margin-right: 5px;"
                                 src="images/${step_outcome_icon}" class="${step_class_root}-icon"/>
                        </#if>
                    </td>
                    <td>
                        <div class="step-description">
                            <#if showAccordion>
                            <a href="javaScript:void(0)" onClick="toggleDiv('stepSection${step_number}')"
                               style="display:block">
                            </#if>
                            <span class="${step_class_root}-step">${formatter.formatWithFields(step.description,testOutcome.exampleFields)}</span>
                            <#if showAccordion>
                            </a>
                            </#if>

                            <#if step.hasRestQuery()>
                                <span class="rest-query">
                                    <@restQueryData restQuery=step.restQuery number=step.number />
                                </span>
                            </#if>
                        </div>
                    </td>
                    <#if testOutcome.hasScreenshots()>
                        <td width="100" class="${step.result}-text">
                            <#if !step.isAGroup() && step.firstScreenshot??>
                                <a href="${relativeLink!}${testOutcome.screenshotReportName}.html#screenshots?screenshot=${screenshotCount}">
                                    <!-- Added invalid href-attribute to img for imgpreviewer -->
                                    <img src="${step.firstScreenshot.screenshot.name}"
                                         href="${step.firstScreenshot.screenshot.name}"
                                         class="screenshot"
                                         width="48" height="48"/>
                                    <#assign screenshotCount = screenshotCount + step.screenshotCount />
                                </a>
                            </#if>
                        </td>
                    </#if>
                    <td width="100"><span class="${step_class_root}-step">${step.result}</span></td>
                    <td width="100"><span class="${step_class_root}-step">${step.durationInSeconds}s</span></td>
                </tr>
                <#if (step.errorMessage?has_content) && !step.hasNestedErrors()>
                    <tr class="test-${step.result}">
                        <td width="40">&nbsp</td>
                        <#if step.errorMessage?has_content>
                            <#assign errorMessageTitle = step.errorMessage?html>
                        <#else>
                            <#assign errorMessageTitle = "">
                        </#if>
                        <td width="%" colspan="4" class="error-message-cell">
                            <span class="error-message ellipsis"
                                  title='${formatter.htmlAttributeCompatible(errorMessageTitle)}'><pre>${formatter.htmlAttributeCompatible(errorMessageTitle)!''}</pre></span>
                            <#if step.nestedException?has_content>
                                <@stacktrace cause=step.nestedException />
                            </#if>
                        </td>
                    </tr>
                </#if>
            </#macro>
            <#-- Test step results -->
            <#list testOutcome.testSteps as step>
                <@write_step step=step step_number=step_index />
            </#list>
            <#if testOutcome.stepCount == 0 || testOutcome.hasNonStepFailure()>
                <#if testOutcome.result == "FAILURE">
                    <#assign step_outcome_icon = "fail.png">
                <#elseif testOutcome.result == "ERROR">
                    <#assign step_outcome_icon = "cross.png">
                </#if>
                <#if step_outcome_icon?has_content>
                    <tr class="test-${testOutcome.result}">
                        <td width="40">
                            <img style="margin-left: 20px; margin-right: 5px;" src="images/${step_outcome_icon}"
                                 class="top-level-icon"/>
                        </td>
                        <td width="%">
                            <span class="top-level-step">An error occurred outside of step execution.</span>
                        </td>
                        <td width="100"><span class="top-level-step">${testOutcome.result}</span></td>
                        <td width="100"><span class="top-level-step">${testOutcome.durationInSeconds}s</span></td>
                    </tr>
                    <tr class="test-${testOutcome.result}">
                        <td width="40">&nbsp</td>
                        <td width="%" colspan="4">
                            <#if (testOutcome.errorMessage)??>
                                <span class="error-message"
                                      title="${formatter.htmlAttributeCompatible(testOutcome.errorMessage)}">${testOutcome.errorMessage}</span>
                                <@stacktrace cause=testOutcome.nestedTestFailureCause />
                            </#if>
                        </td>
                    </tr>
                </#if>
            </#if>
                <tr class="test-${testOutcome.result}">
                    <td colspan="2"></td>
                    <td width="100"><span class="top-level-step"><em>${testOutcome.result}</em></span></td>
                    <td width="100"><span class="top-level-step"><em>${testOutcome.durationInSeconds}s</em></span></td>
                </tr>

            </table>
        </div>
    </div>
    <div id="beforefooter"></div>
    <div id="bottomfooter">
        <span class="version">Serenity BDD version ${serenityVersionNumber}</span>
    </div>


    <script type="text/javascript">
        function toggleDiv(divId) {
            $("#" + divId).toggle();
            var imgsrc = $(".img" + divId).attr('src');
            if (imgsrc == 'images/plus.png') {
                $(".img" + divId).attr("src", function () {
                    return "images/minus.png";
                });

            }
            else {
                $(".img" + divId).attr("src", function () {
                    return "images/plus.png";
                });
            }
        }
    </script>

    <script type="text/javascript">
        $('.example-table table').DataTable({
            "order": [
                [1, "asc"]
            ],
            "pageLength": 25,
            "scrollX": "100%",
            "scrollXInner": "100%",
            "scrollCollapse": true
        });
    </script>

    <script type="text/javascript">
        //<![CDATA[

        $(document).ready(function () {
            $('img.screenshot').imgPreview({
                imgCSS: {
                    width: '500px'
                },
                distanceFromCursor: {top: 10, left: -200}
            });
        });

        //]]>
    </script>
    <div id="imgPreviewContainer" style="position: absolute; top: 612px; left: 355px; display: none; " class=""><img
            src="" style="display: none; "></div>
    <div id="imgPreviewContainer2" style="position: absolute; top: 925px; left: 320px; display: none; " class="">
        <img style="width: 200px; display: none; " src=""></div>
    <div id="imgPreviewWithStyles" style="position: absolute; top: 1272px; left: 321px; display: none; " class="">
        <img style="height: 200px; opacity: 1; display: none; " src=""></div>
    <div id="imgPreviewWithStyles2" style="display: none; position: absolute; "><img style="height: 200px; "></div>
    <div id="imgPreviewWithStyles3" style="display: none; position: absolute; "><img style="height: 200px; "></div>

</body>
</html>
