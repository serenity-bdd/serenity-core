<#macro reportData(reportData, number) >

    <script>
        function copyContent(id) {
            console.log("copy content of " + id)
            const content = document.getElementById(id).innerText
            navigator.clipboard.writeText(content);
        }
    </script>

    <#if reportData.contents?has_content>
                <a name="${reportData.id}"></a>
                <span>
                    <button type="button" class="btn btn-success btn-sm" data-toggle="collapse"
                            data-target="#reportData-${number}">
                        ${reportData.title}
                    </button>
                </span>
                <div class="rest-query-details">
                    <div class="collapse multi-collapse" id="reportData-${number}" tabindex="-1" role="dialog"
                         aria-labelledby="restModalLabel" aria-hidden="true">
                        <div class="card">
                            <h4 class="card-header" id="restModalLabel">
                                ${reportData.title}
                            <a role="button" class="btn btn-xs copy-button" onclick="copyContent('reportDataContent-${number}')">
                                <i class="bi bi-clipboard"></i>&nbsp;Copy
                            </a>
                            </h4>
                        </div>
                        <div class="card-body">
                            <pre id="reportDataContent-${number}">${(formatter.renderText(reportData.contents))!}</pre>
                        </div>
                    </div>
                </div>
    <#else>
                <span>
                    <a role="button" class="btn btn-success btn-sm" href="${(reportData.path)!}">
                        <i class="bi bi-download"></i>&nbsp;${reportData.title}
                    </a>
                </span>
    </#if>
</#macro>