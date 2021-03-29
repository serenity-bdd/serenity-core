package sample.elements;

import serenitycore.net.thucydides.core.annotations.ImplementedBy;
import serenitycore.net.thucydides.core.pages.WebElementFacade;

@ImplementedBy(BadImplementer.class)
public interface HasBadImplementer extends WebElementFacade{

}
