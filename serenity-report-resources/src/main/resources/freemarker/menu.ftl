<#macro main_menu(selected)>
<div class="menu">
    <ul>
        <li><a href="index.html" <#if selected=="home">class="current"</#if>>Overall Test Results</a></li>
        <li><a href="capabilities.html" <#if selected=="requirements">class="current"</#if>>Requirements</a></li>
    <#if (reportOptions.showReleases)>
        <li><a href="releases.html" <#if selected=="releases">class="current"</#if>>Releases</a></li>
    </#if>
    <#if reportOptions.showProgress>
        <li><a href="progress-report.html" <#if selected=="progress">class="current"</#if>>Progress</a></li>
    </#if>
    <#foreach requirementType in requirementTypes>
        <#assign requirmentReport = absoluteReportName.forRequirementType(requirementType) >
        <#assign typeTitle = inflection.of(requirementType).inPluralForm().asATitle() >
        <li><a href="${requirmentReport}" <#if selected=="${requirementType}">class="current"</#if>>${typeTitle}</a></li>
    </#foreach>
    <#if reportOptions.showTagMenus>
        <#foreach tagType in allTestOutcomes.firstClassTagTypes>
            <#assign tagReport = absoluteReportName.forTagType(tagType) >
            <#assign tagTypeTitle = inflection.of(tagType).inPluralForm().asATitle() >
            <li><a href="${tagReport}" <#if selected=="${tagType}">class="current"</#if>>>${tagTypeTitle}</a></li>
        </#foreach>
    </#if>
    <#if reportOptions.showHistory>
        <li><a href="history.html" <#if selected=="history">class="current"</#if>>History</a></li>
    </#if>
    </ul>
    <span class="date-and-time">Report generated ${timestamp}</span>
    <br style="clear:left"/>
</div>
</#macro>