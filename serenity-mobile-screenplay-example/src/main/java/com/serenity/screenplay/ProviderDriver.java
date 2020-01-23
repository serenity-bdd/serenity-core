package com.serenity.screenplay;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.webdriver.WebDriverFacade;
/**
 * A proxy class for mobile driver instances, designed to provide driver for one particular platform.
 * 
 * @author jacob
 */
public abstract class ProviderDriver<T>{
		
	@SuppressWarnings("unchecked")
	public T getDriver(Actor actor) {
		return (T) ((WebDriverFacade) BrowseTheWeb.as(actor).getDriver()).getProxiedDriver();
	}
	
}
