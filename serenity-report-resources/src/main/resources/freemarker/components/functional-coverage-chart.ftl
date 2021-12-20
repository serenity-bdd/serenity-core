<#macro coverage_chart(id, feature)>

    <#assign tagCoverageEntries = feature.tagCoverage />

    <!-- COVERAGE BAR CHART -->
    <script>

        // The coverage bar chart
        const coverageChartCtx = document.getElementById('${id}');

        // Options define for display value on top of bars

        const coverageReports = [
            <#list tagCoverageEntries as tagCoverage>
            {
                title: '${formatter.javascriptCompatible(tagCoverage.tagName)}',
                result: '${formatter.javascriptCompatible(tagCoverage.resultName)}',
<#--                <#if tagCoverage.testCount = 0>-->
                link: "${tagCoverage.report}"
<#--                </#if>-->
            },
            </#list>
        ]
        const coverageData = {
            labels: [
                <#list tagCoverageEntries as tagCoverage>
                '${formatter.javascriptCompatible(tagCoverage.tagName)}',
                </#list>
            ],
            datasets: [
                {
                    label: ['Passing Tests'],
                    data: ${feature.renderedTestCountsWithStatus('SUCCESS')},
                    backgroundColor: [ ${colorScheme.backgroundColorFor('SUCCESS')} ]
                },
                {
                    label: ['Pending Tests'],
                    data: ${feature.renderedTestCountsWithStatus('PENDING')},
                    backgroundColor: [ ${colorScheme.backgroundColorFor('PENDING')} ]
                },
                {
                    label: ['Ignored Tests'],
                    data: ${feature.renderedTestCountsWithStatus('IGNORED')},
                    backgroundColor: [ ${colorScheme.backgroundColorFor('IGNORED')} ]
                },
                {
                    label: ['Skipped Tests'],
                    data: ${feature.renderedTestCountsWithStatus('SKIPPED')},
                    backgroundColor: [ ${colorScheme.backgroundColorFor('SKIPPED')} ]
                },
                {
                    label: ['Aborted Tests'],
                    data: ${feature.renderedTestCountsWithStatus('ABORTED')},
                    backgroundColor: [ ${colorScheme.backgroundColorFor('ABORTED')} ]
                },
                {
                    label: ['Failed Tests'],
                    data: ${feature.renderedTestCountsWithStatus('FAILURE')},
                    backgroundColor: [ ${colorScheme.backgroundColorFor('FAILURE')} ]
                },
                {
                    label: ['Errors'],
                    data: ${feature.renderedTestCountsWithStatus('ERROR')},
                    backgroundColor: [ ${colorScheme.backgroundColorFor('ERROR')} ]
                },
                {
                    label: ['Compromised Tests'],
                    data: ${feature.renderedTestCountsWithStatus('COMPROMISED')},
                    backgroundColor: [ ${colorScheme.backgroundColorFor('COMPROMISED')} ]
                },
            ]
        }
        const coverageOptions = {
            responsive: true,
            indexAxis: 'y',
            // barPercentage: ,
            barThickness: 'flex',
            maxBarThickness: 30,
            minBarLength: 2,
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
                    'font.weight': 'bold'
                },
                tooltip: {
                    enabled: true,
                    usePointStyle: true,
                },
            },
        }

        const coverageChart = new Chart(coverageChartCtx, {
            type: 'bar',
            data: coverageData,
            options: coverageOptions
        });

        function coverageClickHandler(click) {
            const points = coverageChart.getElementsAtEventForMode(click, 'nearest', {intersect: true}, true);
            if (points.length) {
                const firstPoint = points[0];
                if (coverageReports[firstPoint.index].link) {
                    window.open(coverageReports[firstPoint.index].link, "_self")
                }
            }
        }

        coverageChartCtx.onclick = coverageClickHandler;
    </script>
    <!-- END COVERAGE CHART -->
</#macro>