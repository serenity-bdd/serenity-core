package net.serenitybdd.screenplay.ensure.web

import net.serenitybdd.core.pages.PageObject
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.ensure.KnowableValue
import net.serenitybdd.screenplay.ensure.StringEnsure

class PageObjectEnsure(val value: PageObject) {

    fun title() : StringEnsure = StringEnsure(titleOf(value))
    fun currentUrl() : StringEnsure = StringEnsure(currentUrlOf(value))
    fun pageSource() : StringEnsure = StringEnsure(pageSourceOf(value))
    fun windowHandle() : StringEnsure = StringEnsure(windowHandleOf(value))

    private fun titleOf(page: PageObject) : KnowableValue<String> = fun(_: Actor) : String = page.title
    private fun currentUrlOf(page: PageObject) : KnowableValue<String> = fun(_: Actor) : String = page.driver.currentUrl
    private fun pageSourceOf(page: PageObject) : KnowableValue<String> = fun(_: Actor) : String = page.driver.pageSource
    private fun windowHandleOf(page: PageObject) : KnowableValue<String> = fun(_: Actor) : String = page.driver.windowHandle
}
