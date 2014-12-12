<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>${screenshot.description}</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="font-awesome/css/font-awesome.min.css">
    <!--[if IE 7]>
    <link rel="stylesheet" href="font-awesome/css/font-awesome-ie7.min.css">
    <![endif]-->

    <!-- JQuery -->
    <script type="text/javascript" src="scripts/jquery-1.11.1.min.js"></script>

    <!-- Bootstrap -->
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <script src="bootstrap/js/bootstrap.min.js"></script>
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
