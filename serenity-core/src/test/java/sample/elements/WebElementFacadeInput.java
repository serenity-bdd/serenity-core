package sample.elements;

import serenitycore.net.thucydides.core.annotations.ImplementedBy;
import serenitycore.net.thucydides.core.pages.WebElementFacade;

@ImplementedBy(WebElementFacadeInputImpl.class)
public interface WebElementFacadeInput extends WebElementFacade {
	
	public void enterText(String text);
}
