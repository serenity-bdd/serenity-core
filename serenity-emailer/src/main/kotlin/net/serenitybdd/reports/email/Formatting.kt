package net.serenitybdd.reports.email

import com.sun.xml.internal.ws.util.StringUtils

class Formatted {
    fun asATitle(text:String) = StringUtils.capitalize(text)
}