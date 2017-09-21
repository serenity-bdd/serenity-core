<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>${screenshot.description}</title>
<#include "libraries/favicon.ftl">

    <#include "libraries/common.ftl">

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
