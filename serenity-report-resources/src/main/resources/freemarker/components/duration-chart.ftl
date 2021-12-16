<#macro duration_chart(id)>
    <#assign testAutomatedResultData = resultCounts.automatedResultValuesFor("success","pending","ignored","skipped","aborted","failure","error","compromised") >
    <#assign testManualResultData = resultCounts.manualResultValuesFor("success","pending","ignored","skipped","aborted","failure","error","compromised") >
    <#assign numberOfTestsPerDurationRange = durations.numberOfTestsPerDuration />
    <#assign numberOfBuckets = durations.bucketCount />

    <!-- DURATION CHART -->
    <script>

        // Options define for display value on top of bars
        const durationDetailsReports = [
            <#list durations.durationBuckets as bucket>
                <#assign durationReport = reportName.withPrefix(currentTag).forTag(bucket.tag) >
                {title: "${bucket.duration}", link: "${durationReport}"},
            </#list>
        ]
        // The Severity bar chart
        const durationChartCtx = document.getElementById('${id}');

        // Options define for display value on top of bars

        const durationData = {
            labels: ${durations.bucketLabels},
            datasets: [{
                label: 'Number of tests per duration',
                fill: false,
                data: ${durations.numberOfTestsPerDuration},
                backgroundColor: 'rgba(83, 146, 255,0.5)',
                borderColor: 'rgba(83, 146, 255, 1)',
            }]
        }
        const durationOptions = {
            responsive: true,
            plugins: {
                // Change options for ALL labels of THIS CHART
                datalabels: {
                    color: '#444444',
                    'font.weight': 'bold',
                    formatter: (value, ctx) => {
                        if (value > 0) {
                            return value;
                        } else {
                            return '';
                        }
                    },
                },
                tooltip: {
                    enabled: true,
                    usePointStyle: true,
                    callbacks: {
                        label: (data) => {
                            return data.parsed.y + ' tests'
                        }
                    },
                },
            },
        }

        const durationChart = new Chart(durationChartCtx, {
            type: 'bar',
            data: durationData,
            options: durationOptions
        });

        function durationClickHandler(click) {
            const points = durationChart.getElementsAtEventForMode(click, 'nearest', {intersect: true}, true);
            if (points.length) {
                const firstPoint = points[0];
                window.open(durationDetailsReports[firstPoint.index].link, "_self")

            }
        }

        durationChartCtx.onclick = durationClickHandler;
    </script>
    <!-- END DURATION CHART -->
</#macro>