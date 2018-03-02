<#macro stacktrace(cause, id) >
                <div><!-- Stack trace -->
                    <button type="button" class="btn btn-danger" data-toggle="modal"
                            data-target="#stacktraceModal-${id}">
                        View details
                    </button>
                </div>
                <!-- Modal -->
                <div class="modal fade" id="stacktraceModal-${id}" tabindex="-1" role="dialog"
                     aria-labelledby="stacktraceModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"
                                        aria-label="Close"><span aria-hidden="true">&times;</span>
                                </button>
                                <h4 class="modal-title" id="stacktraceModalLabel">
                                    ${cause.errorType}
                                </h4>
                            </div>
                            <div class="modal-body">
                                <#if (cause.message)??>
                                    <h5>
                                        <pre>${formatter.messageBody(cause.message)}</pre>
                                    </h5>
                                </#if>
                                <#list cause.stackTrace as element>
                                    ${element.className}.${element.methodName}(${(element.fileName)!""}
                                    :${element.lineNumber}) <br>
                                </#list>
                            </div>

                            <#if (cause.rootCause.isPresent())>
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal"
                                            aria-label="Close"><span aria-hidden="true">&times;</span>
                                    </button>
                                    <h4 class="modal-title" id="stacktraceModalLabel">
                                        Caused by: ${cause.rootCause.get().errorType}
                                    </h4>
                                </div>
                                <div class="modal-body">
                                    <#if (cause.rootCause.get().message)??>
                                        <pre>${formatter.messageBody(cause.rootCause.get().message)}</pre>
                                    </#if>
                                    <pre>
                                    <#list cause.rootCause.get().stackTrace as element>
                                        ${element.className}.${element.methodName}(${(element.fileName)!""} :${element.lineNumber}) <br>
                                    </#list>
                                    </pre>
                                </div>

                            </#if>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">
                                    Close
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
</#macro>
