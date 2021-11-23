<#include "requirement-section.ftl">

= ${report.title}
:revdate: ${report.date?date}
:revnumber: ${report.version}
:version-label!:

<#if (requirementsOutcomes.overview?has_content)>
= Introduction
${formatted.asAsciidoc(requirementsOutcomes.overview)}
</#if>

= Features
<#list requirementsOutcomes.visibleOutcomes as requirementOutcome>
<@requirement_section requirementsOutcomes=requirementsOutcomes requirementOutcome=requirementOutcome level=2/>
</#list>

= Acceptance Tests


== Colophon
<#noparse>
Version: ${revnumber}

Version Date: ${revdate}
</#noparse>