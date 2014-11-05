package sample.elements;

import net.thucydides.core.annotations.ImplementedBy;
import net.thucydides.core.pages.WebElementFacade;

@ImplementedBy(WebElementFacadeInputImpl.class)
public interface WebElementFacadeInput extends WebElementFacade {
	
	public void enterText(String text);
}
