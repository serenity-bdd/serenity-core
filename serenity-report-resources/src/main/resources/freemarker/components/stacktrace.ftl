<#macro stacktrace(title, cause, id) >
    <div style="display:block">
        <div class="table">
            <div class="row">
                <div class="col-sm-10" style="padding-left:1.5em; padding-top:0.5em; padding-bottom:0.5em;">
                    <span class="error-message error-message-title">${title}</span>
                </div>
                <div class="col-sm-2">
                    <a class="btn btn-warning details-button" data-toggle="collapse" href="#stacktraceModal-${id}""
                       role="button" aria-expanded="false" aria-controls="multiCollapseExample1"><i class="far fa-eye"></i> More details</a>
                </div>
             </div>
        </div>
    <div>

    <!-- Details Section -->
    <div class="row stacktrace-details">
      <div class="col">
        <div class="collapse multi-collapse" id="stacktraceModal-${id}">
            <div class="card">
                <div class="card-header">
                    <h4>${cause.errorType} </h4>
                </div>
                <div class="card-body">
                    <div>
                        <pre><#list cause.stackTrace as element>${element.className?trim}.${element.methodName}(${(element.fileName)!""}:${element.lineNumber})<br /></#list></pre>
                    </div>

                    <#if (cause.rootCause.isPresent())>
                    <h4>
                        Caused by: ${cause.rootCause.get().errorType}
                    </h4>
                    <div>
                        <#if (cause.rootCause.get().message)??>
                            <pre>${formatter.messageBody(cause.rootCause.get().message)}</pre>
                        </#if>
                        <pre><#list cause.rootCause.get().stackTrace as element>${element.className?trim}.${element.methodName}(${(element.fileName)!""} :${element.lineNumber})<br /></#list></pre>
                    </div>
                    </#if>
                </div>
            </div>
        </div>
    </div>
</#macro>
