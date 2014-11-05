package sample.elements;

import net.thucydides.core.annotations.ImplementedBy;
import net.thucydides.core.pages.WebElementFacade;

@ImplementedBy(BadImplementer.class)
public interface HasBadImplementer extends WebElementFacade{

}
