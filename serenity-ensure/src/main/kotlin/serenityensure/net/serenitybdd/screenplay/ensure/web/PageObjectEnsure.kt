package serenityensure.net.serenitybdd.screenplay.ensure.web

import serenityscreenplay.net.serenitybdd.screenplay.Actor
import serenityscreenplaywebdriver.net.serenitybdd.screenplay.abilities.BrowseTheWeb
import serenityensure.net.serenitybdd.screenplay.ensure.KnowableValue
import serenityensure.net.serenitybdd.screenplay.ensure.StringEnsure

class PageObjectEnsure() {

    fun title() : StringEnsure = StringEnsure(titleValue())
    fun currentUrl() : StringEnsure = StringEnsure(currentUrlValue())
    fun pageSource() : StringEnsure = StringEnsure(pageSourceValue())
    fun windowHandle() : StringEnsure = StringEnsure(windowHandleValue())

    private fun titleValue() : KnowableValue<String> = fun(actor: Actor) : String = BrowseTheWeb.`as`(actor).title
    private fun currentUrlValue() : KnowableValue<String> = fun(actor: Actor) : String = BrowseTheWeb.`as`(actor).driver.currentUrl
    private fun pageSourceValue() : KnowableValue<String> = fun(actor: Actor) : String = BrowseTheWeb.`as`(actor).driver.pageSource
    private fun windowHandleValue() : KnowableValue<String> = fun(actor: Actor) : String = BrowseTheWeb.`as`(actor).driver.windowHandle
}
