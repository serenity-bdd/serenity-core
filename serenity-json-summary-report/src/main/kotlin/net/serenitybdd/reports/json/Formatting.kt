package net.serenitybdd.reports.json

import org.apache.commons.lang3.StringUtils

class Formatted {
    fun asATitle(text:String) = StringUtils.capitalize(text)
}