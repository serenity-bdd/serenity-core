<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>${screenshot.description}</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" type="text/css" href="jqplot/jquery.jqplot.min.css"/>

    <script src="scripts/jquery.js" type="text/javascript"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>

    <script type="text/javascript" src="datatables/media/js/jquery.dataTables.min.js"></script>
    <script src="scripts/imgpreview.full.jquery.js" type="text/javascript"></script>

    <link type="text/css" href="jqueryui/css/start/jquery-ui-1.8.18.custom.css" rel="Stylesheet"/>
    <script type="text/javascript" src="jqueryui/js/jquery-ui-1.8.18.custom.min.js"></script>
</head>

<body>
    <h3>${screenshot.description}</h3>

    <img src="${screenshot.filename}}" class="screenshot" width="500">

    <table>
        <tr>
            <td><a href='${screenshot.previousStep.screenshotPage}'>PREV</a></td>
            <td><a href='${screenshot.nextStep.screenshotPage}'>NEXT</a></td>
        </tr>
    </table>
</body>
</html>
