<#macro requirement_section(requirementsOutcomes, requirementOutcome, level)>

<@header_prefix_level header_level=level /> ${formatted.asATitle(requirementOutcome.requirement.type)}: ${formatted.asATitle(requirementOutcome.requirement.name)}
${formatted.asAsciidoc(requirementOutcome.requirement.narrative.text)}

<#list requirementOutcome.requirement.children as childRequirement>
    <#assign childRequirementOutcomes = requirementsOutcomes.requirementOutcomeFor(childRequirement)>
    <#assign nextLevel = level + 1>
    <@requirement_section requirementsOutcomes=requirementsOutcomes requirementOutcome=childRequirementOutcomes level=nextLevel/>
</#list>

</#macro>
<#macro header_prefix_level header_level><#list 0..<header_level as i>=</#list></#macro>