<#include "bootstrap-icons.ftl">
<#include "jquery.ftl">
<#include "treeview.ftl">
<#include "datatables.ftl">
<#include "css.ftl">

<!--CHART.JS-->
<script src="chartjs/chart.js"></script>
<script src="chartjs/chartjs-plugin-datalabels@2.0.0"></script>
<script src="chartjs/patternomaly.min.js"></script>
<script>
    // Register the plugin to all charts:
    Chart.register(ChartDataLabels);
</script>
