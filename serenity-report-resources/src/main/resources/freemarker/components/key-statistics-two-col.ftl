<#macro key_statistics_two_columns(testOutcomes)>
    <#assign manualTotalCount = testOutcomes.count("MANUAL").total >
    <#assign manualSuccessCount = testOutcomes.count("MANUAL").withResult("success") >
    <#assign manualPendingCount = testOutcomes.count("MANUAL").withResult("pending") >
    <#assign manualIgnoredCount = testOutcomes.count("MANUAL").withResult("ignored") >
    <#assign manualFailureOrErrorCount = testOutcomes.count("MANUAL").withFailureOrError() >
    <div>
        <h3><i class="bi bi-speedometer2"></i> Key Statistics</h3>
        <div>
            <table class="table table-striped table-hover">
                <tbody>
                <tr scope="row">
                    <td>
                        <i class="bi bi-card-checklist"></i> Number of Scenarios
                    </td>
                    <td>${testOutcomes.scenarioCount}</td>
                    <td>
                        <i class="bi bi-stopwatch"></i> Total
                        Duration
                    </td>
                    <td>${totalClockDuration}</td>
                </tr>
                <tr scope="row">
                    <td>
                        <i class="bi bi-caret-right"></i> Total Number of Test Cases
                    </td>
                    <td>${testOutcomes.testCaseCount}</td>
                    <td>
                        <i class="bi bi-trophy"></i> Fastest Test
                    </td>
                    <td>${minTestDuration}</td>
                </tr>
                <tr scope="row">
                    <td>
                        <i class="bi bi-person"></i> Number of Manual Test Cases
                    </td>
                    <td>${manualTotalCount}</td>
                    <td>
                        <i class="bi bi-skip-start"></i> Slowest
                        Test
                    </td>
                    <td>${maxTestDuration}</td>
                </tr>
                <tr scope="row">
                    <td>
                        <i class="bi bi-flag-fill"></i> Tests Started
                    </td>
                    <td>${startTimestamp}</td>
                    <td>
                        <i class="bi bi-stopwatch"></i> Average
                        Execution Time
                    </td>
                    <td>${averageTestDuration}</td>
                </tr>
                <tr scope="row">
                    <td>
                        <i class="bi bi-stop-circle"></i> Tests
                        Finished
                    </td>
                    <td>${endTimestamp}</td>
                    <td>
                        <i class="bi bi-stopwatch-fill"></i> Total
                        Execution Time
                    </td>
                    <td>${totalTestDuration}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</#macro>
