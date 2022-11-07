<#macro requirements_result_chart(id)>
    <!-- PIE CHART -->
    <script>

        // The results pie chart
        const requirementsChartCtx = document.getElementById('${id}');

        // Options define for display value on top of bars
        const requirementsOutcomeDetailsReports = [
            { title: 'Passing Test Cases', link: "${successReport}" },
            { title: 'Pending Test Cases', link: "${pendingReport}" },
            { title: 'Ignored Test Cases', link: "${ignoredReport}" },
            { title: 'Skipped Test Cases', link: "${skippedReport}" },
            { title: 'Aborted Test Cases',  link: "${abortedReport}"},
            { title: 'Failed Test Cases', link: "${failureReport}"  },
            { title: 'Broken Test Cases', link: "${errorReport}"    },
            { title: 'Compromised Test Cases',  link: "${compromisedReport}" },
            { title: 'Undefined Test Cases',  link: "" },
        ]
        const requirementsOutcomeData = {
            labels: ['Passing Test Cases', 'Pending Test Cases', 'Ignored Test Cases', 'Skipped Test Cases', 'Aborted Test Cases', 'Failed Test Cases', 'Broken Test Cases', 'Compromised Test Cases','Undefined Test Cases'],
            datasets: [{
                label: 'Feature Coverage',
                fill: false,
                data: ${testResultData},
                backgroundColor: ${colorScheme.backgroundColors},
                borderColor: ${colorScheme.borderColors},
                borderWidth: 1,
            }]
        }
        const requirementsOutcomeOptions = {
            responsive: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom'
                },
                // Change options for ALL labels of THIS CHART
                datalabels: {
                    color: '#444444',
                    'font.weight': 'bold',
                    formatter: (value, ctx) => {
                        let sum = 0;
                        let dataArr = ctx.chart.data.datasets[0].data;
                        dataArr.map(data => {
                            sum += data;
                        });
                        let percentage = (value * 100 / sum).toFixed(0) + "%";
                        if (percentage === '0%' || percentage === 'NaN%') {
                            return '';
                        } else {
                            return percentage;
                        }
                    },
                },
            }
        }

        const requirementsChart = new Chart(requirementsChartCtx, {
            type: 'doughnut',
            data: requirementsOutcomeData,
            options: requirementsOutcomeOptions,
            plugins: [
                {
                    id: 'text',
                    beforeDraw: function (chart, a, b) {
                        var width = chart.width,
                            height = chart.height,
                            ctx = chart.ctx;

                        ctx.restore();
                        var fontSize = (height / 200).toFixed(1);
                        ctx.font = fontSize + "em sans-serif";
                        ctx.textBaseline = "middle";

                        var text = "${resultCounts.getOverallTestPercentageLabel("success")}"
                        textX = 10 + Math.round((width - ctx.measureText(text).width) / 2);
                        textY = (height / 2) - 40;

                        ctx.fillText(text, textX, textY);
                        ctx.save();
                    }
                }]
        });

        function reqsClickHandler(click) {
            const points = requirementsChart.getElementsAtEventForMode(click, 'nearest', {intersect: true}, true);
            if (points.length && requirementsOutcomeDetailsReports[firstPoint.index].link) {
                const firstPoint = points[0];
                window.open(requirementsOutcomeDetailsReports[firstPoint.index].link, "_self")

            }
        }
        requirementsChartCtx.onclick = reqsClickHandler;
    </script>
    <!-- END PIE CHART -->
</#macro>
