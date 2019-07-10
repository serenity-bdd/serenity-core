<#macro main_menu(selected)>
<#--<div class="menu">-->
<div>
    <ul class="nav nav-tabs" role="tablist">
        <li <#if selected=="home">class="active"</#if>>
            <#if selected=="home"><a href="#"><#else><a href="index.html"></#if><i class="fa fa-check-square-o"></i> Overall Test Results</a>
        </li>
        <li <#if selected=="requirements">class="active"</#if>>
            <#if selected=="requirements"><a href="#"><#else><a href="capabilities.html"></#if><i class="fa fa-book"></i> Requirements</a>
        </li>
    <#if (reportOptions.showReleases)>
        <li <#if selected=="releases">class="active"</#if>>
            <#if selected=="releases"><a href="#"><#else><a href="releases.html"></#if><i class="fa fa-paper-plane"></i> Releases</a>
        </li>
    </#if>
    <#if reportOptions.showProgress>
        <li <#if selected=="progress">class="active"</#if>>
            <#if selected=="progress"><a href="#"><#else><a href="progress-report.html"></#if>Progress</a>
        </li>
    </#if>
    <#foreach requirementType in requirementTypes>
        <#assign requirmentReport = absoluteReportName.forRequirementType(requirementType) >
        <#assign typeTitle = inflection.of(requirementType).inPluralForm().asATitle() >
        <li <#if selected=="${requirementType}">class="active"</#if>>
            <#if selected=="${requirementType}"><a href="#"><#else><a href="${requirmentReport}"></#if><i class="fa fa-comments-o"></i> ${typeTitle}</a>
        </li>
    </#foreach>
    <#if reportOptions.showTagMenus>
        <#foreach tagType in allTestOutcomes.firstClassTagTypes>
            <#assign tagReport = absoluteReportName.forTagType(tagType) >
            <#assign tagTypeTitle = inflection.of(inflection.humanize(tagType)).inPluralForm().asATitle() >
            <li <#if selected=="${tagType}">class="active"</#if>>
                <#if selected=="${tagType}"><a href="#"><#else><a href="${tagReport}"></#if><i class="fa fa-tags"></i> ${tagTypeTitle}</a>
            </li>
        </#foreach>
    </#if>
    <#if reportOptions.showHistory>
        <li <#if selected=="history">class="active"</#if>>
            <#if selected=="history"><a href="#"><#else><a href="history.html"></#if>History</a>
        </li>
    </#if>
    </ul>
    <span class="date-and-time"><a href="build-info.html"><i class="fa fa-info-circle"></i></a> Report generated ${timestamp}</span>
    <br style="clear:left"/>
</div>
</#macro>