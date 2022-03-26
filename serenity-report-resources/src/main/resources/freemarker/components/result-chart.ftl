<#macro result_chart(id)>
    <!-- PIE CHART -->
    <script>

        // The results pie chart
        const outcomeChartCtx = document.getElementById('${id}');

        // Options define for display value on top of bars
        const outcomeDetailsReports = [
            { title: 'Passing Scenarios', link: "${successReport}" },
            { title: 'Pending Scenarios', link: "${pendingReport}" },
            { title: 'Ignored Scenarios', link: "${ignoredReport}" },
            { title: 'Skipped Scenarios', link: "${skippedReport}" },
            { title: 'Aborted Scenarios',  link: "${abortedReport}"},
            { title: 'Failed Scenarios', link: "${failureReport}"  },
            { title: 'Broken Scenarios', link: "${errorReport}"    },
            { title: 'Compromised Scenarios',  link: "${compromisedReport}" },
        ]
        const outcomeData = {
            labels: ['Passing Scenarios', 'Pending Scenarios', 'Ignored Scenarios', 'Skipped Scenarios', 'Aborted Scenarios', 'Failed Scenarios', 'Broken Scenarios', 'Compromised Scenarios','Undefined Scenarios'],
            datasets: [{
                label: 'Test Results',
                fill: false,
                data: ${testResultData},
                backgroundColor: ${colorScheme.backgroundColors},
                borderColor: ${colorScheme.borderColors},
                borderWidth: 1,
            }]
        }
        const outcomeOptions = {
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

        const outcomeChart = new Chart(outcomeChartCtx, {
            type: 'doughnut',
            data: outcomeData,
            options: outcomeOptions,
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

        function clickHandler(click) {
            const points = outcomeChart.getElementsAtEventForMode(click, 'nearest', {intersect: true}, true);
            if (points.length) {
                const firstPoint = points[0];
                window.open(outcomeDetailsReports[firstPoint.index].link, "_self")

            }
        }
        outcomeChartCtx.onclick = clickHandler;
    </script>
    <!-- END PIE CHART -->
</#macro>
