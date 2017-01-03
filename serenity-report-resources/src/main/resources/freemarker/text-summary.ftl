Serenity report generated ${timestamp}
${testOutcomes.totalTestScenarios} test scenarios <#if (testOutcomes.hasDataDrivenTests())>(${testOutcomes.total} tests in all, including ${testOutcomes.totalDataRows} rows of test data)</#if>

Passed:             ${testOutcomes.totalScenarios.withResult("success")}
Pending             ${testOutcomes.totalScenarios.withResult("pending")}
Failed:             ${testOutcomes.totalScenarios.withResult("failure")}
Failed with errors: ${testOutcomes.totalScenarios.withResult("error")}
Compromised:        ${testOutcomes.totalScenarios.withResult("compromised")}
Pending:            ${testOutcomes.totalScenarios.withResult("pending")}
Ignored:            ${testOutcomes.totalScenarios.withResult("ignored")}
Skipped:            ${testOutcomes.totalScenarios.withResult("skipped")}