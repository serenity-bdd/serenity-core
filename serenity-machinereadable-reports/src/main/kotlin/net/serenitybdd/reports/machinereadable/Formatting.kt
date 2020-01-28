package net.serenitybdd.reports.machinereadable

import org.apache.commons.lang3.StringUtils

class Formatted {
    fun asATitle(text:String) = StringUtils.capitalize(text)
}