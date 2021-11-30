<#macro result_summary(id)>
    <#assign testAutomatedResultData = resultCounts.automatedResultValuesFor("success","pending","ignored","skipped","aborted","failure","error","compromised") >
    <#assign testManualResultData = resultCounts.manualResultValuesFor("success","pending","ignored","skipped","aborted","failure","error","compromised") >

    <!-- SUMMARY BAR CHART -->
    <script>

        // The Severity bar chart
        const severityChartCtx = document.getElementById('${id}');

        // Options define for display value on top of bars

        const severityDetailsReports = [
            { title: 'Passing Scenarios', link: "${successReport}" },
            { title: 'Pending Scenarios', link: "${pendingReport}" },
            { title: 'Ignored Scenarios', link: "${ignoredReport}" },
            { title: 'Skipped Scenarios', link: "${skippedReport}" },
            { title: 'Aborted Scenarios',  link: "${abortedReport}"},
            { title: 'Failed Scenarios', link: "${failureReport}"  },
            { title: 'Broken Scenarios', link: "${errorReport}"    },
            { title: 'Compromised Scenarios',  link: "${compromisedReport}" },
        ]
        const severityData = {
            labels: ['Passing', 'Pending', 'Ignored', 'Skipped', 'Aborted', 'Failed', 'Broken', 'Compromised'],
            datasets: [
                {
                    label: 'Automated',
                    data: ${testAutomatedResultData},
                    backgroundColor: ${colorScheme.backgroundColors},
                    borderColor: ${colorScheme.borderColors},
                    borderWidth: 1
                },
                {
                    label: 'Manual',
                    data: ${testManualResultData},
                    backgroundColor: ${colorScheme.manualBackgroundColors},
                    borderColor: ${colorScheme.borderColors},
                    borderWidth: 1
                },

            ]
        }
        const severityOptions = {
            responsive: true,
            skipNull: true,
            scales: {
                x: {
                    stacked: true,
                },
                y: {
                    stacked: true,
                }
            },
            plugins: {
                // Change options for ALL labels of THIS CHART
                datalabels: {
                    color: '#444444',
                    'font.weight': 'bold',
                    formatter: (value, ctx) => {
                        if (value === 0) {
                            return '';
                        } else {
                            return value;
                        }
                    },
                }
            }
        }
        const severityChart = new Chart(severityChartCtx, {
            type: 'bar',
            data: severityData,
            options: severityOptions
        });

        function clickHandler(click) {
            const points = severityChart.getElementsAtEventForMode(click, 'nearest', {intersect: true}, true);
            if (points.length) {
                const firstPoint = points[0];
                window.open(severityDetailsReports[firstPoint.index].link, "_self")

            }
        }
        severityChartCtx.onclick = clickHandler;
    </script>
    <!-- END SUMMARY CHART -->
</#macro>