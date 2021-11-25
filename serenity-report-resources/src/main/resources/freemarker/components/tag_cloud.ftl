<#macro tag_cloud()>
    <#if tagResults?has_content >
        <div class="row">
            <div class="col-sm-12">
                <h3>Tags</h3>
                <#list tagResults as tagResultGroup >
                    <div class="card">
                        <div class="card-body">
                            <#if tagResultGroup.tagType?has_content>
                                <h5 class="card-title">${inflection.of(tagResultGroup.tagType).asATitle()}</h5>
                            </#if>
                            <div>
                                <#list tagResultGroup.tagResults as tagResult >
                                    <a href="${tagResult.report}">
                                    <span class="tag-badge badge" style="background-color:${tagResult.color}; margin:1em;padding:4px;">
                                        <i class="bi bi-tag-fill"></i> ${tagInflector.ofTag(tagResult.tag.type, tagResult.tag.name).toFinalView()}&nbsp;&nbsp;&nbsp;${tagResult.count}
                                    </span>
                                    </a>
                                </#list>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
    </#if>
</#macro>