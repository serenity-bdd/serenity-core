Serenity report generated ${timestamp}

Test Cases:         ${testCount} <#if (testOutcomes.hasDataDrivenTests())> (including ${testOutcomes.totalDataRows} rows of test data)</#if>
Passed:             ${testOutcomes.totalScenarios.withResult("success")}
Failed:             ${testOutcomes.totalScenarios.withResult("failure")}
Failed with errors: ${testOutcomes.totalScenarios.withResult("error")}
Compromised:        ${testOutcomes.totalScenarios.withResult("compromised")}
Pending:            ${testOutcomes.totalScenarios.withResult("pending")}
Ignored:            ${testOutcomes.totalScenarios.withResult("ignored")}
Skipped:            ${testOutcomes.totalScenarios.withResult("skipped")}