package net.serenitybdd.screenplay.ensure.web

import net.serenitybdd.screenplay.targets.Target
import org.openqa.selenium.By

class ElementLocated {
    companion object {
        @JvmStatic fun by(byLocator : By) : Target = Target.the("element located by $byLocator").located(byLocator)
        @JvmStatic fun by(locator : String) : Target = Target.the("element located by $locator").locatedBy(locator)
        @JvmStatic fun by(target: Target) : Target = target
    }
}