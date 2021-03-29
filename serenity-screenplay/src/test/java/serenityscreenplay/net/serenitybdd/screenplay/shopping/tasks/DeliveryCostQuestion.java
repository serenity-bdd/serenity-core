
package serenityscreenplay.net.serenitybdd.screenplay.shopping.tasks;

import serenityscreenplay.net.serenitybdd.screenplay.Actor;
import serenityscreenplay.net.serenitybdd.screenplay.Question;
import serenitymodel.net.thucydides.core.annotations.Step;

public class DeliveryCostQuestion implements Question<Integer> {

	@Step("Then {0} expects the delivery cost to be correct")
	public Integer answeredBy(Actor actor) {
		 return 20;
	 }

}