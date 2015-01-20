<!-- jQplot -->
<#include "excanvas.ftl">
<link rel="stylesheet" type="text/css" href="jqplot/1.0.8/jquery.jqplot.min.css"/>
<script type="text/javascript" src="jqplot/1.0.8/jquery.jqplot.min.js"></script>
<#if pie!false>
<script type="text/javascript" src="jqplot/1.0.8/plugins/jqplot.pieRenderer.min.js"></script>
<#else>
<script type="text/javascript" src="jqplot/1.0.8/plugins/jqplot.categoryAxisRenderer.min.js"></script>
<script type="text/javascript" src="jqplot/1.0.8/plugins/jqplot.dragable.min.js"></script>
<script type="text/javascript" src="jqplot/1.0.8/plugins/jqplot.highlighter.min.js"></script>
<script type="text/javascript" src="jqplot/1.0.8/plugins/jqplot.dateAxisRenderer.min.js"></script>
<script type="text/javascript" src="jqplot/1.0.8/plugins/jqplot.cursor.min.js"></script>
</#if>