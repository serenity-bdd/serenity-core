package net.thucydides.core.model

import net.thucydides.model.domain.stacktrace.FailureCause
import spock.lang.Specification

/**
 * Created by john on 4/07/2014.
 */
class WhenParsingWebdriverJSONErrors extends Specification {

    def "should extract the error message text from a JSON error message"() {
        given:
            def errorMessageJson = "{'errorMessage':'Unable to find element with partial link text 'Book'','request':{'headers':{'Accept':'application/json, image/png','Connection':'Keep-Alive','Content-Length':'44','Content-Type':'application/json; charset=utf-8','Host':'localhost:29489'},'httpVersion':'1.1','method':'POST','post':'{\\'using\\':\\'partial link text\\',\\'value\\':\\'Book\\'}','url':'/element','urlParsed':{'anchor':'','query':'','file':'element','directory':'/','path':'/element','relative':'/element','port':'','host':'','password':'','user':'','userInfo':'','authority':'','protocol':'','source':'/element','queryKey':{},'chunks':['element']},'urlOriginal':'/session/ef34b380-0346-11e4-9007-dfa6d8e8b250/element'}}"
        when:
            def failureCause = new FailureCause("WebdriverError", errorMessageJson, null)
        then:
            failureCause.message == "Unable to find element with partial link text 'Book'"
    }
}
