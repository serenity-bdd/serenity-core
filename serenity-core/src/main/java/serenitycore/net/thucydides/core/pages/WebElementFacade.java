package serenitycore.net.thucydides.core.pages;

import serenitycore.net.serenitybdd.core.annotations.ImplementedBy;

/** @deprecated Use same-named class in serenitybdd package
 *
 */
@Deprecated
@ImplementedBy(WebElementFacadeImpl.class)
public interface WebElementFacade extends serenitycore.net.serenitybdd.core.pages.WebElementFacade, WebElementState {
}
