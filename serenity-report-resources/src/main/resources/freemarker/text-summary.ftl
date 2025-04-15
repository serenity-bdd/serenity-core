Serenity report generated ${timestamp}

Number of scenarios:  ${testOutcomes.scenarioCount}
Number of test cases: ${testOutcomes.testCaseCount}
Passed:               ${testOutcomes.totalScenarios.withResult("success")}
Failed:               ${testOutcomes.totalScenarios.withResult("failure")}
Failed with errors:   ${testOutcomes.totalScenarios.withResult("error")}
Compromised:          ${testOutcomes.totalScenarios.withResult("compromised")}
Pending:              ${testOutcomes.totalScenarios.withResult("pending")}
Ignored:              ${testOutcomes.totalScenarios.withResult("ignored")}
Skipped:              ${testOutcomes.totalScenarios.withResult("skipped")}